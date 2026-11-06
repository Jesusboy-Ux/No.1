package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MedicinalPlant
import com.example.data.model.SavedBookmark
import com.example.ui.components.EvidenceBadge
import com.example.ui.theme.AccentLime
import com.example.ui.theme.DeepTealDark
import com.example.ui.theme.GreenAiColors
import com.example.ui.theme.GreenAiTheme
import com.example.ui.theme.PureWhite

@Composable
fun SavedResearchScreen(
    bookmarks: List<SavedBookmark>,
    allPlants: List<MedicinalPlant>,
    onSelectPlant: (MedicinalPlant) -> Unit,
    onDeleteBookmark: (String) -> Unit,
    onCopyText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GreenAiTheme.colors
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredBookmarks = remember(bookmarks, selectedFilter) {
        when (selectedFilter) {
            "PLANTS" -> bookmarks.filter { it.itemType == "PLANT" }
            "RESEARCH" -> bookmarks.filter { it.itemType == "RESEARCH_ANSWER" }
            else -> bookmarks
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.screenBackground)
            .padding(16.dp)
    ) {
        // Header
        Card(
            colors = CardDefaults.cardColors(containerColor = if (colors.isDark) colors.surfaceElevated else colors.brandPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(0.7.dp, if (colors.isDark) colors.borderSubtle else Color.Transparent, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PureWhite.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = AccentLime,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Saved Research & Bookmarks",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = PureWhite
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Locally persisted plant monographs and AI research findings for offline access and thesis documentation.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = PureWhite.copy(alpha = 0.85f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val filterOptions = listOf(
                Pair("ALL", "All Saved (${bookmarks.size})"),
                Pair("PLANTS", "Plant Monographs"),
                Pair("RESEARCH", "AI Research Syntheses")
            )

            for ((key, label) in filterOptions) {
                val isSelected = selectedFilter == key
                item {
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = key },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                            selectedLabelColor = if (colors.isDark) DeepTealDark else PureWhite,
                            containerColor = colors.surfaceCard,
                            labelColor = colors.textPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = colors.borderSubtle,
                            selectedBorderColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredBookmarks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = colors.textMuted.copy(alpha = 0.5f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "No saved bookmarks yet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Bookmark research findings or plant profiles to review them here anytime.",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredBookmarks, key = { it.id }) { bm ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                        shape = RoundedCornerShape(10.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(10.dp))
                            .clickable {
                                if (bm.itemType == "PLANT") {
                                    val matched = allPlants.firstOrNull { it.id == bm.targetId }
                                    if (matched != null) onSelectPlant(matched)
                                }
                            }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (bm.itemType == "PLANT") Icons.Default.LocalFlorist else Icons.Default.Psychology,
                                        contentDescription = null,
                                        tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = bm.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        fontStyle = if (bm.itemType == "PLANT") FontStyle.Italic else FontStyle.Normal,
                                        color = colors.textPrimary
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onCopyText(bm.snippet) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy text",
                                            tint = colors.textSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { onDeleteBookmark(bm.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete bookmark",
                                            tint = colors.alertBorder,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            if (bm.subtitle.isNotBlank()) {
                                Text(
                                    text = bm.subtitle,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.textPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = bm.snippet.take(240) + if (bm.snippet.length > 240) "..." else "",
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = colors.textSecondary
                            )

                            bm.evidenceLevel?.let { level ->
                                Spacer(modifier = Modifier.height(8.dp))
                                EvidenceBadge(level = level)
                            }
                        }
                    }
                }
            }
        }
    }
}
