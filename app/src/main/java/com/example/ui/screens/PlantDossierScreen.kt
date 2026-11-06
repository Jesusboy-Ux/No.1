package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CuratedKnowledgeBase
import com.example.data.model.EvidenceLevel
import com.example.data.model.MedicinalPlant
import com.example.ui.components.EvidenceBadge
import com.example.ui.theme.AccentLime
import com.example.ui.theme.BotanicalDisplayFontFamily
import com.example.ui.theme.BotanicalEditorialFontFamily
import com.example.ui.theme.DeepTealDark
import com.example.ui.theme.GreenAiColors
import com.example.ui.theme.GreenAiTheme
import com.example.ui.theme.PrimaryDeepTeal
import com.example.ui.theme.PureWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlantDossierScreen(
    plant: MedicinalPlant,
    onBack: () -> Unit,
    onBookmarkPlant: () -> Unit,
    onAskAboutPlant: (String) -> Unit,
    onViewInEvidenceGraph: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GreenAiTheme.colors
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val references = rememberReferencesForPlant(plant.id)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.screenBackground)
    ) {
        // Archival Folio Top Action Bar
        Surface(
            color = if (colors.isDark) colors.surfaceCard else PureWhite,
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, colors.borderSubtle)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(40.dp).testTag("dossier_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Return to Botanical Atlas",
                            tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "SCIENTIFIC MONOGRAPH DOSSIER",
                            fontFamily = BotanicalDisplayFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                        )
                        Text(
                            text = "REF: ${plant.id} • HERBARIUM FOLIO",
                            fontSize = 10.sp,
                            fontFamily = BotanicalDisplayFontFamily,
                            color = colors.textMuted
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // View in Evidence Graph Button
                    IconButton(
                        onClick = { onViewInEvidenceGraph(plant.id) },
                        modifier = Modifier.size(40.dp).testTag("dossier_graph_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = "Inspect in Evidence Graph",
                            tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                        )
                    }

                    // Bookmark Button
                    IconButton(
                        onClick = onBookmarkPlant,
                        modifier = Modifier.size(40.dp).testTag("dossier_bookmark_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "Save Folio to Bookmarks",
                            tint = colors.textSecondary
                        )
                    }
                }
            }
        }

        // Folio Scrollable Content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Botanical Dossier Plate Card
            Card(
                colors = CardDefaults.cardColors(containerColor = if (colors.isDark) colors.surfaceCard else PureWhite),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(10.dp))
                    .testTag("dossier_header_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = if (colors.isDark) colors.surfaceElevated else Color(0xFFE8ECEF),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "FAMILY: ${plant.family.uppercase()}",
                                    fontFamily = BotanicalDisplayFontFamily,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = colors.textSecondary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = plant.scientificName,
                                fontFamily = BotanicalEditorialFontFamily,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                lineHeight = 28.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = plant.commonNames.joinToString(" • "),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                            )
                        }

                        // Curated Evidence Badge
                        EvidenceBadge(level = plant.reviewStatus)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Archival Specimen Plate Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(0.6.dp, colors.borderSubtle, RoundedCornerShape(8.dp))
                    ) {
                        Image(
                            painter = painterResource(
                                if (plant.id == "PLANT-001" || plant.id == "PLANT-004") {
                                    R.drawable.herbarium_specimen_plate
                                } else {
                                    R.drawable.atlas_journal_cover
                                }
                            ),
                            contentDescription = "Botanical Specimen Plate",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Botanical specimen label badge
                        Surface(
                            color = PrimaryDeepTeal.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(bottomStart = 8.dp),
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Text(
                                text = "HERBARIUM ARCHIVE PLATE",
                                fontSize = 9.sp,
                                fontFamily = BotanicalDisplayFontFamily,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = PureWhite,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Geographical & Ecological Biome
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFlorist,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = plant.geographicalContext,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            // THE EVIDENCE SPECTRUM GAUGE
            EvidenceSpectrumCard(
                activeLevel = plant.reviewStatus,
                colors = colors
            )

            // Phytochemical Composition Lab Section
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(10.dp))
                    .testTag("dossier_phytochemicals_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PHYTOCHEMICAL COMPOSITION & SECONDARY METABOLITES",
                            fontFamily = BotanicalDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.8.sp,
                            color = colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (chem in plant.phytochemicals) {
                            Surface(
                                color = if (colors.isDark) colors.surfaceElevated else Color(0xFFF0FDF4),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.border(
                                    0.6.dp,
                                    if (colors.isDark) colors.borderSubtle else Color(0xFFBBF7D0),
                                    RoundedCornerShape(6.dp)
                                )
                            ) {
                                Text(
                                    text = chem,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (colors.isDark) colors.brandAccent else Color(0xFF166534),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Laboratory Bioassay Observations:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = plant.laboratoryEvidence,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = colors.textSecondary
                    )
                }
            }

            // Traditional Uses vs Modern Clinical Pharmacology Comparison
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(10.dp))
                    .testTag("dossier_indications_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DOCUMENTED TRADITIONAL PREPARATIONS VS CLINICAL EVIDENCE",
                        fontFamily = BotanicalDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.8.sp,
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Traditional Ethnomedicinal Indications:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    for (use in plant.traditionalUses) {
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text("• ", fontWeight = FontWeight.Bold, color = colors.textSecondary)
                            Text(use, fontSize = 12.sp, color = colors.textPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Traditional Preparation Protocol: ${plant.preparationMethod}",
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        color = colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = colors.borderSubtle)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Human Clinical Research & Systematic Reviews:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (colors.isDark) Color(0xFF38BDF8) else Color(0xFF0369A1)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = plant.clinicalEvidence,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = colors.textSecondary
                    )
                }
            }

            // Critical Safety & Contraindication Warning Panel
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (colors.isDark) Color(0xFF2D1616) else Color(0xFFFEF2F2)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        0.8.dp,
                        if (colors.isDark) Color(0xFF7F1D1D) else Color(0xFFFECACA),
                        RoundedCornerShape(10.dp)
                    )
                    .testTag("dossier_safety_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Contraindications",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "PHARMACOLOGICAL SAFETY & CONTRAINDICATIONS",
                            fontFamily = BotanicalDisplayFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = if (colors.isDark) Color(0xFFFCA5A5) else Color(0xFF991B1B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = plant.safetyInformation,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = if (colors.isDark) Color(0xFFFECACA) else Color(0xFF7F1D1D)
                        )
                    }
                }
            }

            // Curated Scientific Literature & DOI References
            if (references.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(10.dp))
                        .testTag("dossier_references_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "AUTHORITATIVE LITERATURE & SYSTEMATIC REVIEWS",
                            fontFamily = BotanicalDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.8.sp,
                            color = colors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        for (ref in references) {
                            Surface(
                                color = colors.surfaceElevated,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .border(0.5.dp, colors.borderSubtle, RoundedCornerShape(6.dp))
                                    .clickable {
                                        if (ref.doiOrUrl.startsWith("http")) {
                                            try {
                                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ref.doiOrUrl)))
                                            } catch (_: Exception) {}
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = ref.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = colors.textPrimary
                                        )
                                        Text(
                                            text = "${ref.authors} (${ref.year})",
                                            fontSize = 10.sp,
                                            color = colors.textSecondary
                                        )
                                        Text(
                                            text = ref.journal,
                                            fontSize = 9.sp,
                                            fontStyle = FontStyle.Italic,
                                            color = colors.textMuted
                                        )
                                    }
                                    if (ref.doiOrUrl.startsWith("http")) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                            contentDescription = "Open literature reference",
                                            tint = colors.textSecondary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Contextual Bottom Research Dock
        Surface(
            color = if (colors.isDark) colors.surfaceCard else PureWhite,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, colors.borderSubtle)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: Investigate in AI Research Lab
                Button(
                    onClick = {
                        val query = "What are the primary active phytochemicals, pharmacological mechanisms, and documented clinical evidence for ${plant.scientificName}?"
                        onAskAboutPlant(query)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                        contentColor = if (colors.isDark) PrimaryDeepTeal else PureWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.3f).testTag("dossier_investigate_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Investigate in Lab",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Button 2: Inspect in Evidence Graph
                OutlinedButton(
                    onClick = { onViewInEvidenceGraph(plant.id) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).testTag("dossier_graph_dock_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = null,
                        tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Evidence Graph",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                    )
                }
            }
        }
    }
}

/**
 * Visual Evidence Spectrum Card demonstrating the 5-tier scientific continuum
 */
@Composable
fun EvidenceSpectrumCard(
    activeLevel: EvidenceLevel,
    colors: GreenAiColors
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(10.dp))
            .testTag("evidence_spectrum_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "THE EVIDENCE SPECTRUM",
                    fontFamily = BotanicalDisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    color = colors.textPrimary
                )
                Text(
                    text = "EVALUATED TIER",
                    fontSize = 9.sp,
                    fontFamily = BotanicalDisplayFontFamily,
                    color = colors.textMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Spectrum Horizontal Stepper / Gauge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val levels = listOf(
                    EvidenceLevel.TRADITIONAL_USE_REPORTED to "Traditional",
                    EvidenceLevel.LABORATORY_EVIDENCE to "In Vitro",
                    EvidenceLevel.ANIMAL_RESEARCH to "In Vivo",
                    EvidenceLevel.HUMAN_RESEARCH_AVAILABLE to "Clinical",
                    EvidenceLevel.INSUFFICIENT_OR_CONFLICTING to "Conflicting"
                )

                for ((lvl, label) in levels) {
                    val isActive = lvl == activeLevel
                    Surface(
                        color = if (isActive) {
                            if (colors.isDark) colors.brandAccent else colors.brandPrimary
                        } else {
                            colors.surfaceElevated
                        },
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                0.6.dp,
                                if (isActive) (if (colors.isDark) colors.brandAccent else colors.brandPrimary) else colors.borderSubtle,
                                RoundedCornerShape(4.dp)
                            )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isActive) {
                                            if (colors.isDark) PrimaryDeepTeal else AccentLime
                                        } else {
                                            colors.textMuted
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label,
                                fontSize = 9.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                color = if (isActive) {
                                    if (colors.isDark) PrimaryDeepTeal else PureWhite
                                } else {
                                    colors.textSecondary
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = activeLevel.description,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = colors.textSecondary
            )
        }
    }
}

private fun rememberReferencesForPlant(plantId: String) = when (plantId) {
    "PLANT-001" -> CuratedKnowledgeBase.curatedReferences.filter { it.id.startsWith("REF-AFRA") }
    "PLANT-002" -> CuratedKnowledgeBase.curatedReferences.filter { it.id.startsWith("REF-MORI") }
    "PLANT-003" -> CuratedKnowledgeBase.curatedReferences.filter { it.id.startsWith("REF-VERN") }
    "PLANT-004" -> CuratedKnowledgeBase.curatedReferences.filter { it.id.startsWith("REF-HARP") }
    "PLANT-005" -> CuratedKnowledgeBase.curatedReferences.filter { it.id.startsWith("REF-CATH") }
    else -> emptyList()
}
