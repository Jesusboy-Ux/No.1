package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SourceReference
import com.example.ui.components.EvidenceLegendCard
import com.example.ui.theme.AccentLime
import com.example.ui.theme.GreenAiColors
import com.example.ui.theme.GreenAiTheme
import com.example.ui.theme.PureWhite

@Composable
fun EvidenceSourcesScreen(
    sources: List<SourceReference>,
    modifier: Modifier = Modifier
) {
    val colors = GreenAiTheme.colors
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.screenBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header Card
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
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = AccentLime,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Research Literature & Evidence Corpus",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PureWhite
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Verifiable published pharmacognosy literature, ethnobotanical surveys, and Cochrane systematic reviews indexed in Green AI. Every citation corresponds to real peer-reviewed scholarship.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = PureWhite.copy(alpha = 0.85f)
                    )
                }
            }
        }

        item {
            EvidenceLegendCard()
        }

        item {
            Text(
                text = "Indexed Scientific Publications (${sources.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = colors.textPrimary
            )
        }

        items(sources, key = { it.id }) { ref ->
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(10.dp))
                    .clickable {
                        if (ref.doiOrUrl.startsWith("http")) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ref.doiOrUrl))
                            context.startActivity(intent)
                        }
                    }
                    .testTag("source_card_${ref.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            color = if (colors.isDark) colors.surfaceElevated else colors.brandPrimary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = ref.evidenceType,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        if (ref.doiOrUrl.startsWith("http")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Open literature DOI",
                                tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = ref.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        lineHeight = 19.sp,
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Authors: ${ref.authors} (${ref.year})",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )

                    Text(
                        text = "Venue: ${ref.journal}",
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        color = colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Identifier: ${ref.doiOrUrl}",
                        fontSize = 11.sp,
                        color = colors.aiLinkText
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
