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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EvidenceLevel
import com.example.data.model.MedicinalPlant
import com.example.ui.components.EvidenceBadge
import com.example.ui.theme.AccentLime
import com.example.ui.theme.DeepTealDark
import com.example.ui.theme.GreenAiColors
import com.example.ui.theme.GreenAiTheme
import com.example.ui.theme.PureWhite
import org.json.JSONObject

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlantLibraryScreen(
    plants: List<MedicinalPlant>,
    searchQuery: String,
    filterEvidence: EvidenceLevel?,
    onSearchChange: (String) -> Unit,
    onFilterChange: (EvidenceLevel?) -> Unit,
    onSelectPlant: (MedicinalPlant) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GreenAiTheme.colors

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.screenBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = {
                Text(
                    "Search by scientific, common, or local African name...",
                    color = colors.textMuted,
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = colors.textSecondary
                        )
                    }
                }
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
                focusedBorderColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                unfocusedBorderColor = colors.borderSubtle,
                focusedContainerColor = colors.surfaceElevated,
                unfocusedContainerColor = colors.surfaceElevated,
                cursorColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("plant_search_field")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Evidence Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = filterEvidence == null,
                    onClick = { onFilterChange(null) },
                    label = { Text("All Flora (${plants.size})", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                        selectedLabelColor = if (colors.isDark) DeepTealDark else PureWhite,
                        containerColor = colors.surfaceCard,
                        labelColor = colors.textPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = filterEvidence == null,
                        borderColor = colors.borderSubtle,
                        selectedBorderColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                    ),
                    modifier = Modifier.testTag("filter_chip_all")
                )
            }
            items(EvidenceLevel.entries.toTypedArray()) { level ->
                FilterChip(
                    selected = filterEvidence == level,
                    onClick = {
                        if (filterEvidence == level) onFilterChange(null) else onFilterChange(level)
                    },
                    label = { Text(level.label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                        selectedLabelColor = if (colors.isDark) DeepTealDark else PureWhite,
                        containerColor = colors.surfaceCard,
                        labelColor = colors.textPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = filterEvidence == level,
                        borderColor = colors.borderSubtle,
                        selectedBorderColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                    ),
                    modifier = Modifier.testTag("filter_chip_${level.name.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Plant Cards List
        if (plants.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocalFlorist,
                        contentDescription = null,
                        tint = colors.textMuted.copy(alpha = 0.5f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "No medicinal plants found matching '$searchQuery'",
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try clearing the active filter or searching by botanical family.",
                        color = colors.textSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(plants, key = { it.id }) { plant ->
                    PlantCard(
                        plant = plant,
                        colors = colors,
                        onClick = { onSelectPlant(plant) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlantCard(
    plant: MedicinalPlant,
    colors: GreenAiColors = GreenAiTheme.colors,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag("plant_card_${plant.id}")
    ) {
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
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Italic,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = plant.commonNames.take(3).joinToString(", "),
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Botanical Family: ${plant.family}",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }

                EvidenceBadge(level = plant.reviewStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Local Names Badges
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

            if (localNamesList.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (item in localNamesList.take(4)) {
                        Surface(
                            color = if (colors.isDark) colors.surfaceElevated else colors.brandPrimary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${item.first}: ${item.second}",
                                fontSize = 11.sp,
                                color = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Traditional uses excerpt
            Text(
                text = "Traditionally Reported: ${plant.traditionalUses.take(2).joinToString(" • ")}",
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = colors.textSecondary
            )

            if (plant.safetyInformation.contains("WARNING", ignoreCase = true) ||
                plant.safetyInformation.contains("CONTRAINDICATED", ignoreCase = true)
            ) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "⚠️ Specific contraindications documented in clinical literature",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.alertBorder
                )
            }
        }
    }
}
