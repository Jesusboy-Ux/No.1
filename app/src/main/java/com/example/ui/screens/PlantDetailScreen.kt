package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.MedicinalPlant
import com.example.ui.components.EvidenceBadge
import com.example.ui.components.SafetyWarningBanner
import com.example.ui.theme.AccentLime
import com.example.ui.theme.DeepTealDark
import com.example.ui.theme.GreenAiColors
import com.example.ui.theme.GreenAiTheme
import com.example.ui.theme.PureWhite
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlantDetailScreen(
    plant: MedicinalPlant,
    onBack: () -> Unit,
    onBookmarkPlant: () -> Unit,
    onAskAboutPlant: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val colors = GreenAiTheme.colors
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val localNamesList: List<Pair<String, String>> = remember(plant.localNamesJson) {
        try {
            val json = JSONObject(plant.localNamesJson)
            val list = mutableListOf<Pair<String, String>>()
            val keys = json.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                list.add(Pair(k, json.getString(k)))
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    val referencesList = remember(plant.referencesJson) {
        try {
            val arr = JSONArray(plant.referencesJson)
            val refs = mutableListOf<Triple<String, String, String>>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                refs.add(
                    Triple(
                        obj.optString("title"),
                        "${obj.optString("authors")} (${obj.optInt("year")}) - ${obj.optString("journal")}",
                        obj.optString("doiOrUrl")
                    )
                )
            }
            refs
        } catch (e: Exception) {
            emptyList()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.screenBackground)
    ) {
        // Top App Bar
        Surface(
            color = colors.topBarBg,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_from_plant_detail")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to library",
                            tint = PureWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Scientific Monograph",
                            fontSize = 11.sp,
                            color = AccentLime,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = plant.scientificName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            fontStyle = FontStyle.Italic,
                            color = PureWhite
                        )
                    }
                }

                IconButton(
                    onClick = onBookmarkPlant,
                    modifier = Modifier.testTag("bookmark_plant_header")
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Save monograph",
                        tint = PureWhite
                    )
                }
            }
        }

        // Monograph Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Botanical Photography Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(12.dp))
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.botanical_hero),
                        contentDescription = "${plant.scientificName} specimen photograph",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = plant.scientificName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = plant.commonNames.joinToString(" • "),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "Family: ${plant.family}",
                                    fontSize = 13.sp,
                                    color = colors.textSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        EvidenceBadge(level = plant.reviewStatus)

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Parts Documented: ${plant.plantParts.joinToString(", ")}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = plant.geographicalContext,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = colors.textSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick AI Research Button
                        Button(
                            onClick = { onAskAboutPlant("What are the proven pharmacological properties and traditional uses of ${plant.scientificName}?") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                contentColor = if (colors.isDark) DeepTealDark else PureWhite
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(
                                Icons.Default.Psychology,
                                contentDescription = null,
                                tint = if (colors.isDark) DeepTealDark else AccentLime,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Research ${plant.commonNames.firstOrNull() ?: "Specimen"} with AI",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Safety Warning Callout
            if (plant.safetyInformation.isNotBlank() && plant.safetyInformation != "Not documented") {
                SafetyWarningBanner(warningText = plant.safetyInformation)
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Local & Indigenous African Names
            if (localNamesList.isNotEmpty()) {
                ScientificSectionCard(title = "Documented African Indigenous Names", icon = Icons.Default.LocalFlorist, colors = colors) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (item in localNamesList) {
                            Surface(
                                color = if (colors.isDark) colors.surfaceElevated else colors.brandPrimary.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = "${item.first}: ",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                                    )
                                    Text(
                                        text = item.second,
                                        fontSize = 12.sp,
                                        color = colors.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Traditionally Reported Uses & Preparation
            ScientificSectionCard(title = "Traditionally Reported Uses & Ethnobotany", icon = Icons.Default.Info, colors = colors) {
                Column {
                    for (use in plant.traditionalUses) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Text("• ", color = if (colors.isDark) colors.brandAccent else colors.brandPrimary, fontWeight = FontWeight.Bold)
                            Text(
                                text = use,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = colors.textPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Documented Preparation Method:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = plant.preparationMethod,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Phytochemical Compounds
            ScientificSectionCard(title = "Phytochemical Profile", icon = Icons.Default.Science, colors = colors) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (chemical in plant.phytochemicals) {
                        Surface(
                            color = if (colors.isDark) colors.surfaceElevated else colors.brandPrimary.copy(alpha = 0.06f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.border(0.5.dp, colors.borderSubtle, RoundedCornerShape(6.dp))
                        ) {
                            Text(
                                text = chemical,
                                fontSize = 12.sp,
                                color = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Laboratory & Experimental Findings
            ScientificSectionCard(title = "Laboratory & In Vitro Findings", icon = Icons.Default.Science, colors = colors) {
                Text(
                    text = plant.laboratoryEvidence,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = colors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Human Clinical Evidence
            ScientificSectionCard(title = "Human Clinical Evidence", icon = Icons.Default.VerifiedUser, colors = colors) {
                Text(
                    text = plant.clinicalEvidence,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = colors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Limitations & Research Gaps
            ScientificSectionCard(title = "Scientific Limitations & Uncertainties", icon = Icons.Default.Warning, colors = colors) {
                Text(
                    text = plant.limitations,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = colors.textSecondary
                )
            }

            // References
            if (referencesList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                ScientificSectionCard(title = "Verifiable Scientific Literature", icon = Icons.Default.Bookmark, colors = colors) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        for ((idx, ref) in referencesList.withIndex()) {
                            val (title, citation, url) = ref
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (url.startsWith("http")) {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            context.startActivity(intent)
                                        }
                                    }
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "[${idx + 1}] $title",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                                    )
                                    if (url.startsWith("http")) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                            contentDescription = "Open literature reference",
                                            tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = citation,
                                    fontSize = 11.sp,
                                    color = colors.textSecondary
                                )
                                if (url.isNotBlank()) {
                                    Text(
                                        text = url,
                                        fontSize = 10.sp,
                                        fontStyle = FontStyle.Italic,
                                        color = colors.aiLinkText
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ScientificSectionCard(
    title: String,
    icon: ImageVector,
    colors: GreenAiColors = GreenAiTheme.colors,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colors.textPrimary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}
