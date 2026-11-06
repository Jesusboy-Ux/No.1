package com.example.ui.components

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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocumentChunk
import com.example.data.model.EvidenceLevel
import com.example.ui.theme.EvidenceAnimalBg
import com.example.ui.theme.EvidenceAnimalColor
import com.example.ui.theme.EvidenceAnimalDarkBg
import com.example.ui.theme.EvidenceAnimalDarkColor
import com.example.ui.theme.EvidenceHumanBg
import com.example.ui.theme.EvidenceHumanColor
import com.example.ui.theme.EvidenceHumanDarkBg
import com.example.ui.theme.EvidenceHumanDarkColor
import com.example.ui.theme.EvidenceInsufficientBg
import com.example.ui.theme.EvidenceInsufficientColor
import com.example.ui.theme.EvidenceInsufficientDarkBg
import com.example.ui.theme.EvidenceInsufficientDarkColor
import com.example.ui.theme.EvidenceLaboratoryBg
import com.example.ui.theme.EvidenceLaboratoryColor
import com.example.ui.theme.EvidenceLaboratoryDarkBg
import com.example.ui.theme.EvidenceLaboratoryDarkColor
import com.example.ui.theme.EvidenceTraditionalBg
import com.example.ui.theme.EvidenceTraditionalColor
import com.example.ui.theme.EvidenceTraditionalDarkBg
import com.example.ui.theme.EvidenceTraditionalDarkColor
import com.example.ui.theme.GreenAiTheme
import com.example.ui.theme.PureWhite

@Composable
fun EvidenceBadge(
    level: EvidenceLevel,
    modifier: Modifier = Modifier
) {
    val colors = GreenAiTheme.colors
    val (bgColor, textColor, icon) = when (level) {
        EvidenceLevel.TRADITIONAL_USE_REPORTED -> if (colors.isDark) {
            Triple(EvidenceTraditionalDarkBg, EvidenceTraditionalDarkColor, Icons.Default.LocalFlorist)
        } else {
            Triple(EvidenceTraditionalBg, EvidenceTraditionalColor, Icons.Default.LocalFlorist)
        }
        EvidenceLevel.LABORATORY_EVIDENCE -> if (colors.isDark) {
            Triple(EvidenceLaboratoryDarkBg, EvidenceLaboratoryDarkColor, Icons.Default.Science)
        } else {
            Triple(EvidenceLaboratoryBg, EvidenceLaboratoryColor, Icons.Default.Science)
        }
        EvidenceLevel.ANIMAL_RESEARCH -> if (colors.isDark) {
            Triple(EvidenceAnimalDarkBg, EvidenceAnimalDarkColor, Icons.Default.Pets)
        } else {
            Triple(EvidenceAnimalBg, EvidenceAnimalColor, Icons.Default.Pets)
        }
        EvidenceLevel.HUMAN_RESEARCH_AVAILABLE -> if (colors.isDark) {
            Triple(EvidenceHumanDarkBg, EvidenceHumanDarkColor, Icons.Default.VerifiedUser)
        } else {
            Triple(EvidenceHumanBg, EvidenceHumanColor, Icons.Default.VerifiedUser)
        }
        EvidenceLevel.INSUFFICIENT_OR_CONFLICTING -> if (colors.isDark) {
            Triple(EvidenceInsufficientDarkBg, EvidenceInsufficientDarkColor, Icons.Default.Warning)
        } else {
            Triple(EvidenceInsufficientBg, EvidenceInsufficientColor, Icons.Default.Warning)
        }
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
            .border(0.5.dp, textColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .testTag("evidence_badge_${level.name.lowercase()}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = level.label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.2.sp
            )
        }
    }
}

@Composable
fun SafetyWarningBanner(
    warningText: String,
    modifier: Modifier = Modifier
) {
    val colors = GreenAiTheme.colors
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.alertBg),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, colors.alertBorder.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
            .testTag("safety_warning_banner")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(colors.alertBorder.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Safety Alert",
                    tint = colors.alertBorder,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Pharmacological Contraindication & Safety Notice",
                    fontWeight = FontWeight.Bold,
                    color = colors.alertBorder,
                    fontSize = 13.sp,
                    letterSpacing = 0.2.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = warningText,
                    color = colors.alertText,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
fun CitationChip(
    index: Int,
    chunk: DocumentChunk,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GreenAiTheme.colors
    Surface(
        color = colors.aiCitationBg,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
            .border(0.7.dp, colors.aiCitationBorder, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .testTag("citation_chip_$index")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "[$index] ${chunk.citation}",
                fontSize = 11.sp,
                color = colors.aiCitationText,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = "Inspect source passage",
                tint = colors.aiCitationText.copy(alpha = 0.7f),
                modifier = Modifier.size(11.dp)
            )
        }
    }
}

@Composable
fun CitationInspectorDialog(
    chunk: DocumentChunk,
    onDismiss: () -> Unit
) {
    val colors = GreenAiTheme.colors
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceCard,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Source Passage Attribution",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Peer-Reviewed Literature Record",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close dialog",
                        tint = colors.textPrimary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                EvidenceBadge(level = chunk.evidenceTier)
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = chunk.sourceTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Authors: ${chunk.authors} (${chunk.year})",
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )
                Text(
                    text = "Botanical Subject: ${chunk.plantScientificName}",
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                )
                Text(
                    text = "Monograph Section: ${chunk.section}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Extracted Source Text Passage:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = colors.surfaceMuted,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, colors.borderSubtle, RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = chunk.text,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = colors.textPrimary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                    contentColor = if (colors.isDark) colors.topBarBg else PureWhite
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Close Record", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
fun EvidenceLegendCard(modifier: Modifier = Modifier) {
    val colors = GreenAiTheme.colors
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Green AI Evidence Integrity Framework",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colors.textPrimary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Every record and AI-synthesized finding is categorized into verifiable evidence levels to prevent misleading clinical claims:",
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
            for (level in EvidenceLevel.entries) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    EvidenceBadge(level = level)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = level.description,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}
