package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AccentLime
import com.example.ui.theme.GreenAiColors
import com.example.ui.theme.GreenAiTheme
import com.example.ui.theme.PrimaryDeepTeal

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier
) {
    val colors = GreenAiTheme.colors
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.screenBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Hero Card
        Card(
            colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (colors.isDark) colors.surfaceElevated else PrimaryDeepTeal)
                        .border(1.dp, AccentLime.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.green_ai_logo),
                        contentDescription = "Official Green AI Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(78.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Green AI 1.0",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = colors.textPrimary
                )

                Text(
                    text = "Preserving African Traditional Medicine Through Evidence-Aware AI",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = AccentLime
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Developed by Team Message ATM (MeNI)",
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    color = colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mission Card
        AboutSectionCard(
            title = "Mission & Purpose",
            icon = Icons.Default.LocalFlorist,
            colors = colors
        ) {
            Text(
                text = "Africa is home to tens of thousands of medicinal plant species and deep healing wisdom cultivated over generations. However, this knowledge is too often fragmented, undocumented, or unvetted against modern pharmacognosy.\n\n" +
                        "Green AI bridges traditional ethnobotanical reports with peer-reviewed laboratory and clinical evidence. Our goal is to empower researchers, students, and healthcare scholars with verifiable, non-hallucinated data that respects indigenous heritage while upholding scientific rigor.",
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = colors.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Evidence Grounding & RAG Transparency
        AboutSectionCard(
            title = "Evidence Grounding & RAG Architecture",
            icon = Icons.Default.Psychology,
            colors = colors
        ) {
            Text(
                text = "Unlike general-purpose conversational models that frequently hallucinate clinical trials, authors, and pharmacological mechanisms, Green AI uses strict retrieval-augmented grounding:\n\n" +
                        "1. Identifiable Curated Sources: Queries first retrieve indexed chunks from real published peer-reviewed papers and botanical monographs.\n" +
                        "2. Uncertainty Communication: If adequate evidence is absent, the system explicitly responds: 'I could not find sufficient supporting evidence in the available sources.'\n" +
                        "3. Explicit Evidence Tiers: We clearly separate 'Traditionally Reported' community history from 'Scientific Evidence Available' and 'Insufficient or Conflicting Evidence'.\n" +
                        "4. Clickable Citations: Every claim cites the exact underlying passage, author, and publication.",
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = colors.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Safety Disclaimer Card (Prominent)
        Card(
            colors = CardDefaults.cardColors(containerColor = colors.alertBg),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, colors.alertBorder.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = null,
                        tint = colors.alertBorder,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Medical & Educational Disclaimer",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = colors.alertBorder
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Green AI is strictly a research and educational platform. It is NOT a substitute for professional clinical medical advice, diagnosis, or treatment by a licensed physician, pharmacist, or qualified healthcare provider.\n\n" +
                            "• Never use information here to replace prescribed pharmaceuticals for chronic or life-threatening conditions.\n" +
                            "• Exercise extreme caution with pregnancy, lactation, pediatric use, and herb-drug interactions.\n" +
                            "• Traditional use does not automatically establish clinical safety or efficacy.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = colors.alertText
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Green AI 2.0 Roadmap
        AboutSectionCard(
            title = "Export & Green AI 2.0 Roadmap",
            icon = Icons.Default.Code,
            colors = colors
        ) {
            Text(
                text = "Green AI 1.0 is engineered with modular clean architecture (Room SQLite, Retrofit/OkHttp, Coroutines, Jetpack Compose).\n\n" +
                        "Continuing on your laptop:\n" +
                        "• Export: Use the AI Studio top bar settings to export the project as a ZIP or push to GitHub.\n" +
                        "• Build on Laptop: Open in Android Studio Iguana/Ladybug, run 'gradle assembleDebug' to produce APKs.\n" +
                        "• Green AI 2.0 Extensions: Connect on-device vector embeddings (Chroma/FAISS or SQLite-vec), import bulk herbarium PDF datasets, and expand field study records across all 54 African nations.",
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = colors.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun AboutSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    colors: GreenAiColors = GreenAiTheme.colors,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = colors.textPrimary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}
