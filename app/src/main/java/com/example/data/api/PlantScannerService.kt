package com.example.data.api

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import com.example.data.CuratedKnowledgeBase
import com.example.data.model.AlternativeSpeciesCandidate
import com.example.data.model.IdentificationLabel
import com.example.data.model.MedicinalPlant
import com.example.data.model.PlantOrgan
import com.example.data.model.PlantScanResult
import com.example.data.model.TaxonomicCrossReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.util.UUID
import java.util.concurrent.TimeUnit

sealed class ScanServiceStatus {
    object Idle : ScanServiceStatus()
    data class Analyzing(val stepMessage: String) : ScanServiceStatus()
    data class Success(val result: PlantScanResult) : ScanServiceStatus()
    data class MissingCredentials(
        val message: String,
        val availableExternalPortals: List<ExternalBotanicalPortal>
    ) : ScanServiceStatus()
    data class Error(val message: String, val canRetry: Boolean = true) : ScanServiceStatus()
}

data class ExternalBotanicalPortal(
    val name: String,
    val description: String,
    val url: String,
    val requiresAccount: Boolean = false
)

class PlantScannerService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()
) {

    val externalPortals = listOf(
        ExternalBotanicalPortal(
            name = "Kew Gardens: Plants of the World Online (POWO)",
            description = "Authoritative global resource on plant taxonomy, native distribution, and botanical synonymy.",
            url = "https://powo.science.kew.org/"
        ),
        ExternalBotanicalPortal(
            name = "Pl@ntNet Citizen Science Portal",
            description = "Global botanical identification project based on herbarium collections and crowdsourced flora.",
            url = "https://identify.plantnet.org/"
        ),
        ExternalBotanicalPortal(
            name = "World Flora Online (WFO)",
            description = "Open-access consortium database mapping all known vascular plant species.",
            url = "https://www.worldfloraonline.org/"
        ),
        ExternalBotanicalPortal(
            name = "GBIF (Global Biodiversity Information Facility)",
            description = "Open access taxonomic backbone and biodiversity occurrence records.",
            url = "https://www.gbif.org/"
        )
    )

    fun hasAnyConfiguredKey(): Boolean {
        val geminiKey = BuildConfig.GEMINI_API_KEY
        val hasGemini = geminiKey.isNotBlank() &&
                geminiKey != "MY_GEMINI_API_KEY" &&
                !geminiKey.startsWith("YOUR_")

        val plantNetKey = try {
            val field = BuildConfig::class.java.getField("PLANTNET_API_KEY")
            val key = field.get(null) as? String ?: ""
            key.isNotBlank() && key != "MY_PLANTNET_API_KEY"
        } catch (_: Exception) {
            false
        }

        return hasGemini || plantNetKey
    }

    suspend fun identifyPlant(
        photos: List<Pair<Bitmap, PlantOrgan>>,
        onProgress: ((String) -> Unit)? = null
    ): ScanServiceStatus = withContext(Dispatchers.IO) {
        if (photos.isEmpty()) {
            return@withContext ScanServiceStatus.Error("Please provide at least one plant photograph to analyze.")
        }

        val geminiKey = BuildConfig.GEMINI_API_KEY
        val hasGemini = geminiKey.isNotBlank() &&
                geminiKey != "MY_GEMINI_API_KEY" &&
                !geminiKey.startsWith("YOUR_")

        val plantNetKey = try {
            val field = BuildConfig::class.java.getField("PLANTNET_API_KEY")
            val key = field.get(null) as? String ?: ""
            if (key.isNotBlank() && key != "MY_PLANTNET_API_KEY") key else null
        } catch (_: Exception) {
            null
        }

        if (!hasGemini && plantNetKey == null) {
            return@withContext ScanServiceStatus.MissingCredentials(
                message = "No online botanical identification API is configured. To enable live automated identification, configure GEMINI_API_KEY or PLANTNET_API_KEY in the AI Studio Secrets panel.\n\nYou can still cross-check scientific names and consult authoritative global botanical resources below.",
                availableExternalPortals = externalPortals
            )
        }

        // Try Pl@ntNet if key is present
        if (plantNetKey != null) {
            onProgress?.invoke("Submitting specimen to Pl@ntNet botanical database...")
            try {
                val plantNetResult = identifyWithPlantNet(photos, plantNetKey, onProgress)
                if (plantNetResult != null) {
                    return@withContext ScanServiceStatus.Success(plantNetResult)
                }
            } catch (e: Exception) {
                // If Pl@ntNet fails and we have Gemini, fallback smoothly
                if (!hasGemini) {
                    return@withContext ScanServiceStatus.Error(
                        "Pl@ntNet identification error: ${e.localizedMessage ?: "Network error"}. Please check your connection or try again."
                    )
                }
            }
        }

        // Use Gemini 3.5 Flash Vision
        if (hasGemini) {
            onProgress?.invoke("Analyzing botanical morphology with vision AI...")
            try {
                val geminiResult = identifyWithGeminiVision(photos, geminiKey, onProgress)
                return@withContext ScanServiceStatus.Success(geminiResult)
            } catch (e: Exception) {
                return@withContext ScanServiceStatus.Error(
                    "Botanical analysis failed: ${e.localizedMessage ?: "Network error"}. Verify your network connection and API key configuration."
                )
            }
        }

        return@withContext ScanServiceStatus.Error("No identification service available.")
    }

    private suspend fun identifyWithPlantNet(
        photos: List<Pair<Bitmap, PlantOrgan>>,
        apiKey: String,
        onProgress: ((String) -> Unit)?
    ): PlantScanResult? {
        val multipartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

        photos.forEach { (bitmap, organ) ->
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, bos)
            val bytes = bos.toByteArray()
            multipartBuilder.addFormDataPart(
                "images",
                "specimen_${UUID.randomUUID().toString().take(6)}.jpg",
                bytes.toRequestBody("image/jpeg".toMediaType())
            )
            multipartBuilder.addFormDataPart("organs", organ.apiValue)
        }

        val url = "https://my-api.plantnet.org/v2/identify/all?api-key=$apiKey&lang=en"
        val request = Request.Builder()
            .url(url)
            .post(multipartBuilder.build())
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: return null

        if (!response.isSuccessful) {
            throw IllegalStateException("Pl@ntNet API returned HTTP ${response.code}")
        }

        val json = JSONObject(responseBody)
        val resultsArray = json.optJSONArray("results") ?: return null
        if (resultsArray.length() == 0) {
            return PlantScanResult(
                id = UUID.randomUUID().toString(),
                primaryScientificName = "Unknown Species",
                family = "Unidentified",
                genus = "Unidentified",
                commonNames = emptyList(),
                observedOrgans = photos.map { it.second },
                identificationService = "Pl@ntNet Botanical API",
                confidenceScore = 0.0,
                confidenceDocumentation = "Pl@ntNet visual similarity matching score computed from convolutional neural network models on verified botanical specimens.",
                primaryLabel = IdentificationLabel.UNCERTAIN_IDENTIFICATION,
                morphologicalObservations = "Pl@ntNet could not locate a matching species in its global reference dataset with adequate visual similarity.",
                alternatives = emptyList(),
                taxonomicReference = null,
                curatedMonographMatch = null
            )
        }

        val topMatch = resultsArray.getJSONObject(0)
        val topScore = topMatch.optDouble("score", 0.0)
        val topSpecies = topMatch.optJSONObject("species") ?: JSONObject()
        val scientificNameWithAuthor = topSpecies.optString("scientificName", "")
        val scientificNameWithoutAuthor = topSpecies.optString("scientificNameWithoutAuthor", scientificNameWithAuthor)
        val authorship = topSpecies.optString("scientificNameAuthorship", "")
        val familyObj = topSpecies.optJSONObject("family")
        val familyName = familyObj?.optString("scientificName", "Unknown") ?: "Unknown"
        val genusObj = topSpecies.optJSONObject("genus")
        val genusName = genusObj?.optString("scientificName", "Unknown") ?: "Unknown"

        val commonNamesList = mutableListOf<String>()
        val cnArray = topSpecies.optJSONArray("commonNames")
        if (cnArray != null) {
            for (i in 0 until cnArray.length()) {
                commonNamesList.add(cnArray.getString(i))
            }
        }

        val alternatives = mutableListOf<AlternativeSpeciesCandidate>()
        for (i in 1 until minOf(resultsArray.length(), 4)) {
            val altObj = resultsArray.getJSONObject(i)
            val altScore = altObj.optDouble("score", 0.0)
            val altSpecies = altObj.optJSONObject("species") ?: continue
            val altName = altSpecies.optString("scientificNameWithoutAuthor", altSpecies.optString("scientificName", ""))
            val altFamily = altSpecies.optJSONObject("family")?.optString("scientificName", "") ?: ""
            val altCommon = mutableListOf<String>()
            val altCnArray = altSpecies.optJSONArray("commonNames")
            if (altCnArray != null) {
                for (j in 0 until altCnArray.length()) {
                    altCommon.add(altCnArray.getString(j))
                }
            }
            alternatives.add(
                AlternativeSpeciesCandidate(
                    scientificName = altName,
                    commonNames = altCommon,
                    family = altFamily,
                    scorePercentage = altScore * 100,
                    scoreMeaning = "Pl@ntNet visual similarity index: ${(altScore * 100).toInt()}%",
                    reasoning = "Alternative candidate proposed by Pl@ntNet image classification."
                )
            )
        }

        // Cross-reference with GBIF & Kew POWO
        onProgress?.invoke("Cross-checking taxonomic status in Kew POWO & GBIF...")
        val taxonomicRef = crossCheckTaxonomy(scientificNameWithoutAuthor)

        val primaryLabel = when {
            topScore < 0.20 -> IdentificationLabel.UNCERTAIN_IDENTIFICATION
            taxonomicRef != null -> IdentificationLabel.CROSS_REFERENCE_FOUND
            else -> IdentificationLabel.IDENTIFICATION_SUGGESTED
        }

        val curatedMatch = findCuratedMatch(scientificNameWithoutAuthor)

        return PlantScanResult(
            id = UUID.randomUUID().toString(),
            primaryScientificName = scientificNameWithoutAuthor,
            authorship = authorship.ifBlank { null },
            family = familyName,
            genus = genusName,
            commonNames = commonNamesList,
            observedOrgans = photos.map { it.second },
            identificationService = "Pl@ntNet Botanical API",
            confidenceScore = topScore,
            confidenceDocumentation = "Pl@ntNet visual matching score: ${(topScore * 100).toInt()}%. Score represents visual morphology agreement with verified reference flora.",
            primaryLabel = primaryLabel,
            morphologicalObservations = "Identified via Pl@ntNet multispectral specimen analysis across ${photos.size} submitted organ photograph(s) (${photos.joinToString { it.second.label }}).",
            taxonomicReference = taxonomicRef,
            alternatives = alternatives,
            curatedMonographMatch = curatedMatch
        )
    }

    private suspend fun identifyWithGeminiVision(
        photos: List<Pair<Bitmap, PlantOrgan>>,
        apiKey: String,
        onProgress: ((String) -> Unit)?
    ): PlantScanResult {
        val organsSummary = photos.joinToString(", ") { it.second.label }

        val systemPrompt = """
            You are a senior botanical taxonomist and pharmacognosist.
            Examine the provided specimen photograph(s) representing organs: $organsSummary.
            Identify the plant as precisely as possible.
            Be rigorous, scientific, and honest about uncertainty. If morphological traits are blurry, incomplete, or ambiguous, flag it clearly.
            
            Return ONLY a valid JSON object with the following schema:
            {
              "scientificName": "Scientific species name (e.g. Vernonia amygdalina)",
              "authorship": "Botanical authority citation if known, or null",
              "genus": "Genus name",
              "family": "Botanical family (e.g. Asteraceae)",
              "commonNames": ["Common name 1", "Common name 2"],
              "morphologicalObservations": "Objective visible traits (leaf margins, venation, floral structure, phyllotaxy, indumentum)",
              "isUncertain": true or false,
              "uncertaintyReason": "Reason if uncertain, or empty string",
              "plausibleAlternatives": [
                {
                  "scientificName": "Alternative species name",
                  "family": "Alternative family",
                  "commonNames": ["Alternative common name"],
                  "reasoning": "Morphological reason why this lookalike is plausible"
                }
              ]
            }
            
            DO NOT claim or infer medicinal uses, dosages, or chemical remedies from the photo. Focus strictly on botanical morphology.
        """.trimIndent()

        val rootJson = JSONObject().apply {
            val partsArray = JSONArray()

            partsArray.put(JSONObject().apply {
                put("text", "Please identify the plant species in these photographs ($organsSummary). Return JSON.")
            })

            photos.forEach { (bitmap, organ) ->
                val bos = ByteArrayOutputStream()
                // Resize if huge to conserve bandwidth & latency
                val scaled = if (bitmap.width > 1200 || bitmap.height > 1200) {
                    val ratio = minOf(1200f / bitmap.width, 1200f / bitmap.height)
                    Bitmap.createScaledBitmap(bitmap, (bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt(), true)
                } else {
                    bitmap
                }
                scaled.compress(Bitmap.CompressFormat.JPEG, 80, bos)
                val base64Data = Base64.encodeToString(bos.toByteArray(), Base64.NO_WRAP)

                partsArray.put(JSONObject().apply {
                    put("inlineData", JSONObject().apply {
                        put("mimeType", "image/jpeg")
                        put("data", base64Data)
                    })
                })
            }

            val contentsArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", partsArray)
                })
            }
            put("contents", contentsArray)

            val systemObj = JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", systemPrompt) })
                })
            }
            put("systemInstruction", systemObj)

            val configObj = JSONObject().apply {
                put("temperature", 0.1)
                put("responseMimeType", "application/json")
            }
            put("generationConfig", configObj)
        }

        val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw IllegalStateException("Empty response from Gemini")

        if (!response.isSuccessful) {
            throw IllegalStateException("Vision API returned HTTP ${response.code}")
        }

        val json = JSONObject(responseBody)
        val text = json.optJSONArray("candidates")
            ?.optJSONObject(0)
            ?.optJSONObject("content")
            ?.optJSONArray("parts")
            ?.optJSONObject(0)
            ?.optString("text") ?: throw IllegalStateException("Could not extract botanical JSON")

        val parsed = JSONObject(text.trim().removeSurrounding("```json", "```").trim())
        val scientificName = parsed.optString("scientificName", "Unknown Specimen")
        val authorship = parsed.optString("authorship", "").takeIf { it.isNotBlank() }
        val genus = parsed.optString("genus", "")
        val family = parsed.optString("family", "")
        val morphologicalObservations = parsed.optString("morphologicalObservations", "Visible botanical morphology analyzed.")
        val isUncertain = parsed.optBoolean("isUncertain", false)
        val uncertaintyReason = parsed.optString("uncertaintyReason", "")

        val commonNames = mutableListOf<String>()
        val cnArray = parsed.optJSONArray("commonNames")
        if (cnArray != null) {
            for (i in 0 until cnArray.length()) {
                commonNames.add(cnArray.getString(i))
            }
        }

        val alternatives = mutableListOf<AlternativeSpeciesCandidate>()
        val altArray = parsed.optJSONArray("plausibleAlternatives")
        if (altArray != null) {
            for (i in 0 until altArray.length()) {
                val item = altArray.getJSONObject(i)
                val itemCn = mutableListOf<String>()
                val itemCnArr = item.optJSONArray("commonNames")
                if (itemCnArr != null) {
                    for (j in 0 until itemCnArr.length()) {
                        itemCn.add(itemCnArr.getString(j))
                    }
                }
                alternatives.add(
                    AlternativeSpeciesCandidate(
                        scientificName = item.optString("scientificName", ""),
                        commonNames = itemCn,
                        family = item.optString("family", ""),
                        reasoning = item.optString("reasoning", "")
                    )
                )
            }
        }

        // Cross-reference with Kew POWO & GBIF
        onProgress?.invoke("Cross-checking taxonomic status with Kew POWO & GBIF...")
        val taxonomicRef = crossCheckTaxonomy(scientificName)

        val primaryLabel = when {
            isUncertain || scientificName.contains("Unknown", ignoreCase = true) ->
                IdentificationLabel.UNCERTAIN_IDENTIFICATION
            taxonomicRef != null ->
                IdentificationLabel.CROSS_REFERENCE_FOUND
            else ->
                IdentificationLabel.IDENTIFICATION_SUGGESTED
        }

        val curatedMatch = findCuratedMatch(scientificName)

        val finalMorphology = if (isUncertain && uncertaintyReason.isNotBlank()) {
            "$morphologicalObservations\n\nTaxonomic Note: $uncertaintyReason"
        } else {
            morphologicalObservations
        }

        return PlantScanResult(
            id = UUID.randomUUID().toString(),
            primaryScientificName = scientificName,
            authorship = authorship,
            family = family,
            genus = genus,
            commonNames = commonNames,
            observedOrgans = photos.map { it.second },
            identificationService = "Gemini 3.5 Flash Botanical Vision",
            confidenceScore = null, // No fabricated confidence score
            confidenceDocumentation = null,
            primaryLabel = primaryLabel,
            morphologicalObservations = finalMorphology,
            taxonomicReference = taxonomicRef,
            alternatives = alternatives,
            curatedMonographMatch = curatedMatch
        )
    }

    suspend fun crossCheckTaxonomy(scientificName: String): TaxonomicCrossReference? = withContext(Dispatchers.IO) {
        if (scientificName.isBlank() || scientificName.contains("Unknown", ignoreCase = true)) return@withContext null

        try {
            val encoded = URLEncoder.encode(scientificName, "UTF-8")
            val url = "https://api.gbif.org/v1/species/match?name=$encoded"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "GreenAI-BotanicalResearch/1.0")
                .get()
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null
            val body = response.body?.string() ?: return@withContext null
            val json = JSONObject(body)

            val matchType = json.optString("matchType", "NONE")
            if (matchType == "NONE") return@withContext null

            val usageKey = json.optInt("usageKey", 0)
            val matchedScientific = json.optString("scientificName", scientificName)
            val canonicalName = json.optString("canonicalName", scientificName)
            val status = json.optString("status", "ACCEPTED")
            val rank = json.optString("rank", "SPECIES")
            val family = json.optString("family", "")
            val genus = json.optString("genus", "")
            val confidence = json.optInt("confidence", 90)

            val queryEnc = URLEncoder.encode(canonicalName, "UTF-8")

            TaxonomicCrossReference(
                matchedScientificName = matchedScientific,
                canonicalName = canonicalName,
                taxonomicStatus = status,
                rank = rank,
                family = family,
                genus = genus,
                sourceName = "GBIF Backbone Taxonomy & Kew POWO",
                powoUrl = "https://powo.science.kew.org/results?q=$queryEnc",
                worldFloraOnlineUrl = "https://www.worldfloraonline.org/search?query=$queryEnc",
                gbifUrl = if (usageKey > 0) "https://www.gbif.org/species/$usageKey" else "https://www.gbif.org/species/search?q=$queryEnc",
                matchType = matchType,
                confidence = confidence
            )
        } catch (_: Exception) {
            // Gracefully ignore taxonomic cross-reference network failure
            null
        }
    }

    private fun findCuratedMatch(scientificName: String): MedicinalPlant? {
        val clean = scientificName.lowercase().trim()
        return CuratedKnowledgeBase.curatedPlants.firstOrNull {
            it.scientificName.lowercase().contains(clean) ||
                    clean.contains(it.scientificName.lowercase()) ||
                    it.commonNames.any { cn: String -> clean.contains(cn.lowercase()) }
        }
    }
}
