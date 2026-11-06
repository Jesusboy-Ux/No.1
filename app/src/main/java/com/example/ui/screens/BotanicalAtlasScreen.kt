package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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
fun BotanicalAtlasScreen(
    plants: List<MedicinalPlant>,
    searchQuery: String,
    filterEvidence: EvidenceLevel?,
    onSearchChange: (String) -> Unit,
    onFilterChange: (EvidenceLevel?) -> Unit,
    onSelectPlant: (MedicinalPlant) -> Unit,
    onStartResearchQuery: (String) -> Unit,
    onViewInEvidenceGraph: (String) -> Unit,
    onOpenScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GreenAiTheme.colors
    var selectedFamily by remember { mutableStateOf<String?>(null) }

    val families = remember(plants) {
        plants.map { it.family }.distinct().sorted()
    }

    val filteredPlants = remember(plants, searchQuery, filterEvidence, selectedFamily) {
        plants.filter { p ->
            val matchesQuery = searchQuery.isBlank() ||
                    p.scientificName.contains(searchQuery, ignoreCase = true) ||
                    p.commonNamesList.contains(searchQuery, ignoreCase = true) ||
                    p.family.contains(searchQuery, ignoreCase = true) ||
                    p.traditionalUsesList.contains(searchQuery, ignoreCase = true) ||
                    p.phytochemicalsList.contains(searchQuery, ignoreCase = true)

            val matchesEvidence = filterEvidence == null || p.reviewStatus == filterEvidence
            val matchesFamily = selectedFamily == null || p.family.equals(selectedFamily, ignoreCase = true)

            matchesQuery && matchesEvidence && matchesFamily
        }
    }

    val heroPlant = remember(plants) {
        plants.firstOrNull { it.id == "PLANT-001" } ?: plants.firstOrNull()
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .background(colors.screenBackground)
    ) {
        // Editorial Masthead Banner
        item {
            EditorialAtlasMasthead(colors = colors)
        }

        // Spotlight Monograph Folio (Journal Feature)
        if (heroPlant != null && searchQuery.isBlank() && filterEvidence == null && selectedFamily == null) {
            item {
                SpotlightHerbariumFolio(
                    plant = heroPlant,
                    onOpenDossier = { onSelectPlant(heroPlant) },
                    onAskLab = { onStartResearchQuery("What are the active phytochemicals and therapeutic evidence for ${heroPlant.scientificName}?") },
                    onViewGraph = { onViewInEvidenceGraph(heroPlant.id) },
                    colors = colors
                )
            }
        }

        // Search & Taxonomic Filters Section
        item {
            Column {
                // Precision Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            text = "Search botanical taxonomy, vernacula, or phytochemical...",
                            fontSize = 12.sp,
                            color = colors.textMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = colors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        focusedBorderColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                        unfocusedBorderColor = colors.borderSubtle,
                        focusedContainerColor = colors.surfaceElevated,
                        unfocusedContainerColor = colors.surfaceElevated
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("atlas_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // The Evidence Spectrum Filter Strip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "THE EVIDENCE SPECTRUM:",
                        fontFamily = BotanicalDisplayFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "${filteredPlants.size} specimen",
                        fontSize = 10.sp,
                        fontFamily = BotanicalDisplayFontFamily,
                        color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = filterEvidence == null,
                            onClick = { onFilterChange(null) },
                            label = { Text("All Specimen", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                selectedLabelColor = if (colors.isDark) PrimaryDeepTeal else PureWhite,
                                containerColor = colors.surfaceElevated,
                                labelColor = colors.textPrimary
                            ),
                            modifier = Modifier.testTag("filter_evidence_all")
                        )
                    }

                    items(EvidenceLevel.entries) { level ->
                        FilterChip(
                            selected = filterEvidence == level,
                            onClick = { onFilterChange(if (filterEvidence == level) null else level) },
                            label = { Text(level.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                selectedLabelColor = if (colors.isDark) PrimaryDeepTeal else PureWhite,
                                containerColor = colors.surfaceElevated,
                                labelColor = colors.textPrimary
                            ),
                            modifier = Modifier.testTag("filter_evidence_${level.name.lowercase()}")
                        )
                    }
                }

                // Family Filter Bar
                if (families.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            Surface(
                                color = if (selectedFamily == null) {
                                    if (colors.isDark) colors.surfaceCard else Color(0xFFE2EFF2)
                                } else Color.Transparent,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier
                                    .border(0.5.dp, colors.borderSubtle, RoundedCornerShape(4.dp))
                                    .clickable { selectedFamily = null }
                            ) {
                                Text(
                                    text = "ALL FAMILIES",
                                    fontSize = 9.sp,
                                    fontFamily = BotanicalDisplayFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        items(families) { fam ->
                            val isSelected = selectedFamily.equals(fam, ignoreCase = true)
                            Surface(
                                color = if (isSelected) {
                                    if (colors.isDark) colors.brandAccent else colors.brandPrimary
                                } else Color.Transparent,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier
                                    .border(
                                        0.5.dp,
                                        if (isSelected) Color.Transparent else colors.borderSubtle,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .clickable { selectedFamily = if (isSelected) null else fam }
                            ) {
                                Text(
                                    text = fam.uppercase(),
                                    fontSize = 9.sp,
                                    fontFamily = BotanicalDisplayFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) {
                                        if (colors.isDark) PrimaryDeepTeal else PureWhite
                                    } else colors.textSecondary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Specimen Plates List
        items(filteredPlants, key = { it.id }) { plant ->
            HerbariumSpecimenPlateCard(
                plant = plant,
                onOpenDossier = { onSelectPlant(plant) },
                onAskLab = { onStartResearchQuery("Analyze phytochemicals and clinical efficacy of ${plant.scientificName}") },
                onViewGraph = { onViewInEvidenceGraph(plant.id) },
                colors = colors
            )
        }

        if (filteredPlants.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceElevated),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFlorist,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Botanical Specimen Matched",
                            fontFamily = BotanicalDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try adjusting your search query, family filter, or evidence spectrum tier.",
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditorialAtlasMasthead(colors: GreenAiColors) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (colors.isDark) colors.surfaceElevated else colors.brandPrimary
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                0.7.dp,
                if (colors.isDark) colors.borderSubtle else Color.Transparent,
                RoundedCornerShape(10.dp)
            )
            .testTag("atlas_masthead_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PureWhite.copy(alpha = 0.12f))
                            .border(0.6.dp, AccentLime.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.green_ai_logo),
                            contentDescription = "Official Green AI Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "FLORA AFRICANA",
                            fontFamily = BotanicalDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            letterSpacing = 1.2.sp,
                            color = PureWhite
                        )
                        Text(
                            text = "Curated Pharmacognosy & Botanical Atlas",
                            fontSize = 11.sp,
                            color = AccentLime
                        )
                    }
                }

                Surface(
                    color = AccentLime,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "MeNI 1.0",
                        fontSize = 9.sp,
                        fontFamily = BotanicalDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = DeepTealDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Preserving and authenticating centuries of indigenous African ethnobotanical wisdom through rigorous taxonomic records and modern pharmacological evidence tiers.",
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = PureWhite.copy(alpha = 0.9f)
            )
        }
    }
}

/**
 * Spotlight Herbarium Folio Hero Card
 */
@Composable
fun SpotlightHerbariumFolio(
    plant: MedicinalPlant,
    onOpenDossier: () -> Unit,
    onAskLab: () -> Unit,
    onViewGraph: () -> Unit,
    colors: GreenAiColors
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(0.8.dp, colors.borderSubtle, RoundedCornerShape(10.dp))
            .testTag("spotlight_herbarium_folio")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (colors.isDark) colors.surfaceElevated else Color(0xFFE8ECEF),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "FEATURED MONOGRAPH DOSSIER",
                        fontFamily = BotanicalDisplayFontFamily,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = colors.textSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                EvidenceBadge(level = plant.reviewStatus)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cover Visual Plate
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onOpenDossier() }
            ) {
                Image(
                    painter = painterResource(R.drawable.atlas_journal_cover),
                    contentDescription = "Atlas Journal Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    color = PrimaryDeepTeal.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(bottomStart = 8.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "BOTANICAL SPECIMEN #01",
                        fontSize = 9.sp,
                        fontFamily = BotanicalDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = PureWhite,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = plant.scientificName,
                fontFamily = BotanicalEditorialFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )

            Text(
                text = "${plant.commonNames.joinToString(", ")} • Family ${plant.family}",
                fontSize = 12.sp,
                color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = plant.traditionalUses.firstOrNull() ?: plant.preparationMethod,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = colors.textSecondary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenDossier,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                        contentColor = if (colors.isDark) PrimaryDeepTeal else PureWhite
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1.2f).testTag("spotlight_examine_dossier")
                ) {
                    Text("Examine Dossier", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onViewGraph,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f).testTag("spotlight_view_graph")
                ) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = null,
                        tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Graph",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                    )
                }
            }
        }
    }
}

/**
 * Herbarium Specimen Plate Card for each botanical entity in the Atlas
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HerbariumSpecimenPlateCard(
    plant: MedicinalPlant,
    onOpenDossier: () -> Unit,
    onAskLab: () -> Unit,
    onViewGraph: () -> Unit,
    colors: GreenAiColors
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(8.dp))
            .clickable { onOpenDossier() }
            .testTag("specimen_plate_${plant.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Specimen ID & Family & Evidence Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "HERBARIUM NO. ${plant.id}",
                        fontFamily = BotanicalDisplayFontFamily,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = colors.textMuted
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = if (colors.isDark) colors.surfaceElevated else Color(0xFFE8ECEF),
                        shape = RoundedCornerShape(3.dp)
                    ) {
                        Text(
                            text = plant.family.uppercase(),
                            fontSize = 8.sp,
                            fontFamily = BotanicalDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = colors.textSecondary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                EvidenceBadge(level = plant.reviewStatus)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Latin Scientific Binomial in Playfair Display
            Text(
                text = plant.scientificName,
                fontFamily = BotanicalEditorialFontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )

            // Common / Vernacular Names
            Text(
                text = plant.commonNames.joinToString(", "),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Phytochemical Tags
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (chem in plant.phytochemicals.take(4)) {
                    Surface(
                        color = if (colors.isDark) colors.surfaceElevated else Color(0xFFF3F7F8),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.border(0.5.dp, colors.borderSubtle, RoundedCornerShape(4.dp))
                    ) {
                        Text(
                            text = chem,
                            fontSize = 10.sp,
                            color = colors.textSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Traditional preparation summary
            Text(
                text = plant.traditionalUses.firstOrNull() ?: plant.preparationMethod,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = colors.textSecondary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onViewGraph,
                        modifier = Modifier.size(32.dp).testTag("plate_view_graph_${plant.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = "View in Evidence Graph",
                            tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onAskLab,
                        modifier = Modifier.size(32.dp).testTag("plate_ask_lab_${plant.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Ask in Research Lab",
                            tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Surface(
                    color = if (colors.isDark) colors.surfaceElevated else colors.brandPrimary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .clickable { onOpenDossier() }
                        .testTag("plate_inspect_folio_${plant.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Inspect Folio",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}
