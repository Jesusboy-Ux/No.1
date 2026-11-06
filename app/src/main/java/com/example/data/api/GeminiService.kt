package com.example.data.api

import com.example.BuildConfig
import com.example.data.model.DocumentChunk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val MODEL_NAME = "gemini-3.5-flash"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"
    }

    suspend fun generateGroundedAnswer(
        question: String,
        retrievedChunks: List<DocumentChunk>
    ): GroundedAiResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        val hasValidKey = apiKey.isNotBlank() &&
                apiKey != "MY_GEMINI_API_KEY" &&
                !apiKey.startsWith("YOUR_")

        // Build the grounded system prompt and contextual sources
        val systemPrompt = """
            You are Green AI, an evidence-aware African Traditional Medicine research and ethnobotanical assistant developed by Team Message ATM.
            Your purpose is to preserve and organize African Traditional Medicine knowledge by synthesizing scientific research and verified ethnobotanical reports.

            CRITICAL EVIDENCE GROUNDING RULES:
            1. Rely STRICTLY on the provided RETRIEVED SOURCES. Do not invent papers, authors, citations, dosages, or conclusions.
            2. If the retrieved sources do NOT contain adequate evidence to answer the question, explicitly state:
               "I could not find sufficient supporting evidence in the available sources."
            3. Always distinguish clearly between:
               - Traditionally Reported Uses (cultural/community medicinal history)
               - Laboratory & Phytochemical Findings (in vitro, ex vivo, animal assays)
               - Human Clinical Evidence (randomized trials, observational human data)
            4. In every point made, append bracketed citation markers like [1], [2] referencing the specific retrieved source.
            5. Always emphasize documented safety warnings, toxicity risks (especially regarding pregnancy, children, hepatic/renal toxicity, or interactions).
            6. State the appropriate Evidence Status at the end:
               - "Traditionally Reported"
               - "Scientific Evidence Available"
               - "Insufficient or Conflicting Evidence"
        """.trimIndent()

        val sourcesPrompt = if (retrievedChunks.isNotEmpty()) {
            val sourcesList = retrievedChunks.mapIndexed { index, chunk ->
                """
                [${index + 1}] Source: ${chunk.sourceTitle} (${chunk.authors}, ${chunk.year})
                Section: ${chunk.section} | Plant: ${chunk.plantScientificName}
                Passage Content:
                ${chunk.text}
                """.trimIndent()
            }.joinToString("\n\n---\n\n")

            """
            RETRIEVED SOURCES FROM GREEN AI KNOWLEDGE BASE:
            $sourcesList

            USER RESEARCH QUESTION:
            $question
            """.trimIndent()
        } else {
            """
            RETRIEVED SOURCES: None found in the curated knowledge base for this query.

            USER RESEARCH QUESTION:
            $question
            """.trimIndent()
        }

        if (!hasValidKey) {
            // Local fallback synthesis engine grounded directly on retrieved passages
            return@withContext generateLocalGroundedResponse(question, retrievedChunks)
        }

        try {
            val rootJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", sourcesPrompt)
                            })
                        }
                        put("parts", partsArray)
                    })
                }
                put("contents", contentsArray)

                val systemObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", systemPrompt)
                        })
                    }
                    put("parts", partsArray)
                }
                put("systemInstruction", systemObj)

                val configObj = JSONObject().apply {
                    put("temperature", 0.2)
                    put("topP", 0.9)
                }
                put("generationConfig", configObj)
            }

            val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                return@withContext generateLocalGroundedResponse(
                    question,
                    retrievedChunks,
                    notice = "Using local grounded knowledge engine (API response code: ${response.code}). Configure your API Key in the AI Studio Secrets panel for extended live LLM reasoning."
                )
            }

            val responseJson = JSONObject(responseBody)
            val candidates = responseJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                GroundedAiResult(
                    answerText = text,
                    isLiveApi = true
                )
            } else {
                generateLocalGroundedResponse(question, retrievedChunks)
            }
        } catch (e: Exception) {
            generateLocalGroundedResponse(
                question,
                retrievedChunks,
                notice = "Using local grounded knowledge engine. (${e.localizedMessage ?: "Network connection"})."
            )
        }
    }

    private fun generateLocalGroundedResponse(
        question: String,
        retrievedChunks: List<DocumentChunk>,
        notice: String? = null
    ): GroundedAiResult {
        if (retrievedChunks.isEmpty()) {
            return GroundedAiResult(
                answerText = "I could not find sufficient supporting evidence in the available sources for this query.\n\n" +
                        "The Green AI knowledge base currently contains verified records for key African medicinal plants (*Artemisia afra*, *Moringa oleifera*, *Vernonia amygdalina*, *Harpagophytum procumbens*, and *Catharanthus roseus*). You can ingest additional verified documents or structured monographs via the Ingestion Studio.",
                isLiveApi = false,
                externalNotice = notice
            )
        }

        val plantName = retrievedChunks.first().plantScientificName
        val sb = StringBuilder()

        sb.append("### Evidence Summary: $plantName\n\n")

        // Group by sections
        val traditionalChunks = retrievedChunks.filter { it.section.contains("Traditional", ignoreCase = true) }
        val phytochemChunks = retrievedChunks.filter { it.section.contains("Phytochem", ignoreCase = true) }
        val labChunks = retrievedChunks.filter { it.section.contains("Laborat", ignoreCase = true) || it.section.contains("Pharmacol", ignoreCase = true) }
        val clinicalChunks = retrievedChunks.filter { it.section.contains("Clinical", ignoreCase = true) || it.section.contains("Cochrane", ignoreCase = true) }
        val safetyChunks = retrievedChunks.filter { it.section.contains("Safety", ignoreCase = true) || it.section.contains("Toxici", ignoreCase = true) }

        if (traditionalChunks.isNotEmpty()) {
            sb.append("**Traditionally Reported Knowledge:**\n")
            traditionalChunks.forEachIndexed { i, c ->
                val sourceIdx = retrievedChunks.indexOf(c) + 1
                sb.append("• ${c.text.take(300)}... [$sourceIdx]\n\n")
            }
        }

        if (phytochemChunks.isNotEmpty()) {
            sb.append("**Phytochemical Findings:**\n")
            phytochemChunks.forEachIndexed { i, c ->
                val sourceIdx = retrievedChunks.indexOf(c) + 1
                sb.append("• ${c.text.take(300)}... [$sourceIdx]\n\n")
            }
        }

        if (labChunks.isNotEmpty()) {
            sb.append("**Laboratory & Pharmacological Findings:**\n")
            labChunks.forEachIndexed { i, c ->
                val sourceIdx = retrievedChunks.indexOf(c) + 1
                sb.append("• ${c.text.take(300)}... [$sourceIdx]\n\n")
            }
        }

        if (clinicalChunks.isNotEmpty()) {
            sb.append("**Human Clinical Evidence:**\n")
            clinicalChunks.forEachIndexed { i, c ->
                val sourceIdx = retrievedChunks.indexOf(c) + 1
                sb.append("• ${c.text.take(300)}... [$sourceIdx]\n\n")
            }
        }

        if (safetyChunks.isNotEmpty()) {
            sb.append("⚠️ **Documented Safety & Toxicity Considerations:**\n")
            safetyChunks.forEachIndexed { i, c ->
                val sourceIdx = retrievedChunks.indexOf(c) + 1
                sb.append("• ${c.text} [$sourceIdx]\n\n")
            }
        }

        sb.append("**Evidence Status:** ")
        val highestEvidence = when {
            clinicalChunks.isNotEmpty() -> "Human Research Available"
            labChunks.isNotEmpty() -> "Laboratory Evidence"
            traditionalChunks.isNotEmpty() -> "Traditional Use Reported"
            else -> "Insufficient or Conflicting Evidence"
        }
        sb.append(highestEvidence)
        sb.append("\n\n*Note: This synthesis is strictly grounded on the ${retrievedChunks.size} retrieved source passage(s) listed below.*")

        return GroundedAiResult(
            answerText = sb.toString(),
            isLiveApi = false,
            externalNotice = notice ?: "Synthesized directly from verified knowledge base passages."
        )
    }

    suspend fun generateDirectInternetAnswer(
        question: String
    ): GroundedAiResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val hasValidKey = apiKey.isNotBlank() &&
                apiKey != "MY_GEMINI_API_KEY" &&
                !apiKey.startsWith("YOUR_")

        if (!hasValidKey) {
            return@withContext GroundedAiResult(
                answerText = "### Direct Internet & Live AI Mode\n\n" +
                        "This mode queries live web and pharmacological knowledge beyond your local database.\n\n" +
                        "**To enable live AI queries:**\n" +
                        "1. Open the **Secrets panel in AI Studio**.\n" +
                        "2. Add your **GEMINI_API_KEY**.\n\n" +
                        "In the meantime, you can switch back to **Local Notebook RAG** mode above to query your uploaded research materials and verified African botanical monographs completely offline!",
                isLiveApi = false,
                externalNotice = "Direct Internet AI mode requires GEMINI_API_KEY in the Secrets panel."
            )
        }

        val systemPrompt = """
            You are Green AI operating in Direct Internet & Live Research Mode, developed by Team Message ATM (MeNI).
            Your purpose is to provide up-to-date, comprehensive, and evidence-aware scientific syntheses on botanical medicine, pharmacognosy, phytochemistry, and African traditional healing systems.

            DIRECT INTERNET AI GUIDELINES:
            1. You are NOT constrained to local database chunks. Synthesize broadly from established published scientific literature, global ethnobotanical surveys, and pharmacological databases.
            2. Distinguish clearly between:
               - Traditional / Ethnobotanical History
               - In vitro & Laboratory Assays
               - Animal / In vivo Studies
               - Human Clinical Trials & Systematic Reviews
            3. Cite established researchers, journals, and publication years where known.
            4. Include prominent warnings regarding toxicity, contraindications (especially pregnancy, hepatic/renal conditions, or drug-herb interactions).
            5. Structure your response with clear Markdown headings, lists, and tables for high readability.
        """.trimIndent()

        try {
            val rootJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", question)
                            })
                        }
                        put("parts", partsArray)
                    })
                }
                put("contents", contentsArray)

                val systemObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", systemPrompt)
                        })
                    }
                    put("parts", partsArray)
                }
                put("systemInstruction", systemObj)

                val configObj = JSONObject().apply {
                    put("temperature", 0.3)
                    put("topP", 0.95)
                }
                put("generationConfig", configObj)
            }

            val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                return@withContext GroundedAiResult(
                    answerText = "Direct internet research request failed (HTTP ${response.code}). Please verify your network connection and API key quota.",
                    isLiveApi = false,
                    externalNotice = "API connection error: HTTP ${response.code}"
                )
            }

            val responseJson = JSONObject(responseBody)
            val candidates = responseJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                GroundedAiResult(
                    answerText = text,
                    isLiveApi = true,
                    externalNotice = "Direct Internet AI synthesis (Live Model: $MODEL_NAME)"
                )
            } else {
                GroundedAiResult(
                    answerText = "The AI service returned an empty response. Please rephrase your query.",
                    isLiveApi = false
                )
            }
        } catch (e: Exception) {
            GroundedAiResult(
                answerText = "Encountered a network error connecting to live AI research: ${e.localizedMessage ?: "Unknown error"}. Check your internet connection.",
                isLiveApi = false,
                externalNotice = "Network error"
            )
        }
    }
}

data class GroundedAiResult(
    val answerText: String,
    val isLiveApi: Boolean,
    val externalNotice: String? = null
)
