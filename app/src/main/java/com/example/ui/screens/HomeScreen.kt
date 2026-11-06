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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.MedicinalPlant
import com.example.ui.components.EvidenceBadge
import com.example.ui.theme.AccentLime
import com.example.ui.theme.DeepTealContainer
import com.example.ui.theme.DeepTealDark
import com.example.ui.theme.GreenAiTheme
import com.example.ui.theme.PrimaryDeepTeal
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SecondaryBotanicalGreen
import com.example.ui.theme.SubtleBorderNeutral
import com.example.ui.theme.WarmBotanicalIvory
import com.example.ui.viewmodel.AppNavTab

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    featuredPlants: List<MedicinalPlant>,
    onNavigateTab: (AppNavTab) -> Unit,
    onSelectPlant: (MedicinalPlant) -> Unit,
    onStartResearchQuery: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var homeSearchQuery by remember { mutableStateOf("") }

    val sampleQueries = listOf(
        "Active phytochemicals in Artemisia afra?",
        "Is Bitter Leaf safe in pregnancy?",
        "Clinical trials for Devil's Claw in osteoarthritis",
        "Moringa leaf nutrition vs root bark toxicity",
        "Can Catharanthus roseus treat diabetes?"
    )

    val colors = GreenAiTheme.colors

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.screenBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Editorial Hero Brand Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = PrimaryDeepTeal),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_hero_card")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                                .border(0.6.dp, AccentLime.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
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
                                text = "GREEN AI",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                letterSpacing = 1.5.sp,
                                color = PureWhite
                            )
                            Text(
                                text = "Message Nexus International (MeNI)",
                                fontSize = 10.sp,
                                color = AccentLime,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Research Edition 1.0",
                            fontSize = 10.sp,
                            color = PureWhite,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Connecting African Botanical Heritage With Scientific Intelligence.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    lineHeight = 28.sp,
                    color = PureWhite
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "An evidence-grounded research platform preserving indigenous medicinal knowledge and cross-referencing ethnobotanical records with peer-reviewed laboratory, animal, and clinical literature.",
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = PureWhite.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Prominent Quick Search Field
                OutlinedTextField(
                    value = homeSearchQuery,
                    onValueChange = { homeSearchQuery = it },
                    placeholder = {
                        Text(
                            "Enter a research inquiry or plant name...",
                            fontSize = 13.sp,
                            color = PrimaryDeepTeal.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = PrimaryDeepTeal
                        )
                    },
                    trailingIcon = {
                        if (homeSearchQuery.isNotBlank()) {
                            IconButton(onClick = {
                                val q = homeSearchQuery
                                homeSearchQuery = ""
                                onStartResearchQuery(q)
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Run query",
                                    tint = PrimaryDeepTeal
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PureWhite,
                        unfocusedContainerColor = PureWhite,
                        focusedBorderColor = AccentLime,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = PrimaryDeepTeal,
                        unfocusedTextColor = PrimaryDeepTeal
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Useful research question prompt chips
                Text(
                    text = "Suggested Research Questions:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = AccentLime,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (query in sampleQueries.take(4)) {
                        Surface(
                            color = Color.White.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .clickable { onStartResearchQuery(query) }
                                .testTag("home_sample_chip")
                        ) {
                            Text(
                                text = query,
                                fontSize = 11.sp,
                                color = PureWhite,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Authentic Botanical Photography Banner
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
                    contentDescription = "African medicinal plant laboratory specimens",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Curated Pharmacognosy & Botanical Archive",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "High-evidence specimens including Artemisia afra, Moringa oleifera, and Vernonia amygdalina documented with precise phytochemical, in vitro, and clinical citations.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI Plant Scanner Feature Spotlight Card
        Surface(
            color = PrimaryDeepTeal,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateTab(AppNavTab.SCANNER) }
                .testTag("home_plant_scanner_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AccentLime),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "AI Plant Scanner",
                            tint = DeepTealDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AI Plant Scanner",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = PureWhite
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = AccentLime,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "1.0",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepTealDark,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Photograph specimens for online identification & Kew POWO cross-checking",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = PureWhite.copy(alpha = 0.85f)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = AccentLime,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Route Actions Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionTile(
                title = "AI Research",
                subtitle = "Evidence Q&A",
                icon = Icons.Default.Psychology,
                onClick = { onNavigateTab(AppNavTab.RESEARCH) },
                modifier = Modifier.weight(1f)
            )
            ActionTile(
                title = "Plant Library",
                subtitle = "Monographs",
                icon = Icons.Default.LocalFlorist,
                onClick = { onNavigateTab(AppNavTab.LIBRARY) },
                modifier = Modifier.weight(1f)
            )
            ActionTile(
                title = "Evidence",
                subtitle = "Citations & DOIs",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                onClick = { onNavigateTab(AppNavTab.EVIDENCE) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Featured Medicinal Plants Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Featured Botanical Monographs",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colors.textPrimary
                )
                Text(
                    text = "Grounded records with verifiable published studies",
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )
            }
            Text(
                text = "View All →",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                modifier = Modifier
                    .clickable { onNavigateTab(AppNavTab.LIBRARY) }
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        for (plant in featuredPlants.take(3)) {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(10.dp))
                    .clickable { onSelectPlant(plant) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = plant.scientificName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            fontStyle = FontStyle.Italic,
                            color = colors.textPrimary
                        )
                        Text(
                            text = plant.commonNames.take(2).joinToString(", "),
                            fontSize = 12.sp,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Family: ${plant.family}",
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }
                    EvidenceBadge(level = plant.reviewStatus)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Research Integrity & Transparency Note
        Card(
            colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ethical Grounding & Zero Fabrication",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = colors.textPrimary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Green AI does not invent papers, authors, study outcomes, or chemical properties. Where evidence is absent or preliminary, the system explicitly states its limitations. This is a scientific research and educational platform, not a substitute for clinical healthcare advice.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ActionTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GreenAiTheme.colors
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (colors.isDark) colors.surfaceElevated else PrimaryDeepTeal.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = colors.textPrimary
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = colors.textSecondary
            )
        }
    }
}
