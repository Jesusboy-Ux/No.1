package com.example.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.ExternalBotanicalPortal
import com.example.data.api.ScanServiceStatus
import com.example.data.model.AlternativeSpeciesCandidate
import com.example.data.model.IdentificationLabel
import com.example.data.model.MedicinalPlant
import com.example.data.model.PlantOrgan
import com.example.data.model.PlantScanResult
import com.example.data.model.TaxonomicCrossReference
import com.example.ui.theme.AccentLime
import com.example.ui.theme.BotanicalDisplayFontFamily
import com.example.ui.theme.BotanicalGreen
import com.example.ui.theme.DeepTealDark
import com.example.ui.theme.GreenAiTheme
import com.example.ui.theme.PrimaryDeepTeal
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SafetyAlertBg
import com.example.ui.theme.SafetyAlertBorder
import com.example.ui.theme.SafetyAlertText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlantScannerScreen(
    scannedPhotos: List<Pair<Bitmap, PlantOrgan>>,
    selectedOrgan: PlantOrgan,
    scannerStatus: ScanServiceStatus,
    externalPortals: List<ExternalBotanicalPortal>,
    onSelectOrgan: (PlantOrgan) -> Unit,
    onAddPhoto: (Bitmap, PlantOrgan) -> Unit,
    onRemovePhoto: (Int) -> Unit,
    onUpdatePhotoOrgan: (Int, PlantOrgan) -> Unit,
    onClearScanner: () -> Unit,
    onSubmitIdentification: () -> Unit,
    onCrossCheckName: (String) -> Unit,
    onSelectCuratedPlant: (MedicinalPlant) -> Unit,
    onAskAboutPlant: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = GreenAiTheme.colors
    var manualLookupQuery by remember { mutableStateOf("") }

    // Modern Zero-Permission Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    if (bitmap != null) {
                        onAddPhoto(bitmap, selectedOrgan)
                    }
                }
            } catch (_: Exception) {}
        }
    }

    // Direct Camera Preview Capture (zero runtime camera permissions required)
    val cameraPreviewLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            onAddPhoto(it, selectedOrgan)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.screenBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .testTag("plant_scanner_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = 720.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Header Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (colors.isDark) colors.surfaceElevated else PrimaryDeepTeal)
                        .border(0.6.dp, AccentLime.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.green_ai_logo),
                        contentDescription = "Official Green AI Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(34.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "OPTICAL BOTANICAL SCANNER",
                        fontFamily = BotanicalDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "AI Optical Taxonomy & Kew POWO Cross-Verification",
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Guidance & Scientific Framing Banner
            Surface(
                color = colors.surfaceCard,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "BOTANICAL IDENTIFICATION WORKFLOW",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Take or upload specimen photographs of leaves, flowers, fruits, or bark. Green AI queries online botanical identification services (Pl@ntNet API & Gemini Vision) and cross-checks scientific names against authoritative global taxonomy databases (Kew Gardens POWO & GBIF).",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Organ Selector Bar
            Text(
                text = "1. SELECT PLANT ORGAN BEING PHOTOGRAPHED",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                PlantOrgan.entries.forEach { organ ->
                    val isSelected = organ == selectedOrgan
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectOrgan(organ) },
                        label = {
                            Text(
                                text = organ.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                            selectedLabelColor = if (colors.isDark) DeepTealDark else AccentLime,
                            containerColor = colors.surfaceCard,
                            labelColor = colors.textPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) (if (colors.isDark) colors.brandAccent else colors.brandPrimary) else colors.borderSubtle
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: Capture Photo & Pick Image
            Text(
                text = "2. ADD SPECIMEN PHOTOGRAPHS (${scannedPhotos.size}/5)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { cameraPreviewLauncher.launch(null) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                        contentColor = if (colors.isDark) DeepTealDark else PureWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("scanner_camera_button")
                ) {
                    Icon(
                        Icons.Default.AddAPhoto,
                        contentDescription = "Camera",
                        tint = if (colors.isDark) DeepTealDark else AccentLime,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Take Photo", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (colors.isDark) colors.brandAccent else colors.brandPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("scanner_upload_button")
                ) {
                    Icon(
                        Icons.Default.PhotoLibrary,
                        contentDescription = "Upload",
                        tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload Image", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Preview thumbnails list
            if (scannedPhotos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "SPECIMEN IMAGES READY FOR ANALYSIS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    scannedPhotos.forEachIndexed { index, (bitmap, organ) ->
                        Surface(
                            color = colors.surfaceCard,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Specimen photo ${index + 1}",
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(6.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Photograph #${index + 1}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = colors.textPrimary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Surface(
                                            color = if (colors.isDark) colors.surfaceElevated else PrimaryDeepTeal.copy(alpha = 0.08f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "Organ: ${organ.label}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                IconButton(
                                    onClick = { onRemovePhoto(index) },
                                    modifier = Modifier.testTag("remove_photo_$index")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove photo", tint = SafetyAlertBorder)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onSubmitIdentification,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentLime, contentColor = DeepTealDark),
                        shape = RoundedCornerShape(8.dp),
                        enabled = scannerStatus !is ScanServiceStatus.Analyzing,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("submit_identification_button")
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = DeepTealDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Identify Specimen Online",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onClearScanner,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text("Reset", fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Scanner Status / Progress
            when (scannerStatus) {
                is ScanServiceStatus.Idle -> {
                    // Manual Taxonomic Verification Section
                    Surface(
                        color = colors.surfaceCard,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalFlorist, contentDescription = null, tint = BotanicalGreen, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Taxonomic Cross-Reference Lookup",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Have a candidate scientific name from field observation or literature? Validate it directly against Kew Gardens Plants of the World Online (POWO) and the GBIF Backbone Taxonomy without uploading an image.",
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = manualLookupQuery,
                                    onValueChange = { manualLookupQuery = it },
                                    placeholder = { Text("e.g. Vernonia amygdalina or Artemisia afra", fontSize = 12.sp, color = colors.textMuted) },
                                    singleLine = true,
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
                                        .weight(1f)
                                        .testTag("manual_taxon_input")
                                )

                                Button(
                                    onClick = { onCrossCheckName(manualLookupQuery) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                        contentColor = if (colors.isDark) DeepTealDark else PureWhite
                                    ),
                                    enabled = manualLookupQuery.isNotBlank(),
                                    modifier = Modifier
                                        .height(52.dp)
                                        .testTag("manual_taxon_check_button")
                                ) {
                                    Text("Verify Taxon", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                is ScanServiceStatus.Analyzing -> {
                    Surface(
                        color = colors.surfaceCard,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Botanical Intelligence Pipeline Active",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = scannerStatus.stepMessage,
                                fontSize = 12.sp,
                                color = colors.textSecondary,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }

                is ScanServiceStatus.Success -> {
                    PlantScanResultView(
                        result = scannerStatus.result,
                        onSelectCuratedPlant = onSelectCuratedPlant,
                        onAskAboutPlant = onAskAboutPlant,
                        onReset = onClearScanner
                    )
                }

                is ScanServiceStatus.MissingCredentials -> {
                    Surface(
                        color = colors.surfaceCard,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Identification Engine Configuration",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = colors.textPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = scannerStatus.message,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = colors.textSecondary
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = colors.borderSubtle)
                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "AUTHORITATIVE BOTANICAL PORTALS & OPEN REFERENCES",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            scannerStatus.availableExternalPortals.forEach { portal ->
                                ExternalPortalCard(portal = portal)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                is ScanServiceStatus.Error -> {
                    Surface(
                        color = SafetyAlertBg,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SafetyAlertBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = SafetyAlertBorder, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Botanical Identification Pipeline Error",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = SafetyAlertText
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = scannerStatus.message,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = SafetyAlertText
                            )

                            if (scannerStatus.canRetry) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = onSubmitIdentification,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                        contentColor = if (colors.isDark) DeepTealDark else PureWhite
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(40.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Retry Identification", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Authoritative External References List (Always visible as reference)
            Text(
                text = "ESTABLISHED BOTANICAL REFERENCE SOURCES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            externalPortals.forEach { portal ->
                ExternalPortalCard(portal = portal)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun PlantScanResultView(
    result: PlantScanResult,
    onSelectCuratedPlant: (MedicinalPlant) -> Unit,
    onAskAboutPlant: (String) -> Unit,
    onReset: () -> Unit
) {
    val context = LocalContext.current
    val colors = GreenAiTheme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("scan_result_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Result Primary Card
        Surface(
            color = colors.surfaceCard,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (colors.isDark) colors.brandAccent.copy(alpha = 0.5f) else colors.brandPrimary.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Label Badge strictly adhering to instructions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (badgeBg, badgeText, badgeIcon) = when (result.primaryLabel) {
                        IdentificationLabel.IDENTIFICATION_SUGGESTED ->
                            Triple(AccentLime, DeepTealDark, Icons.Default.AutoAwesome)
                        IdentificationLabel.CROSS_REFERENCE_FOUND ->
                            Triple(BotanicalGreen, DeepTealDark, Icons.Default.CheckCircle)
                        IdentificationLabel.UNCERTAIN_IDENTIFICATION ->
                            Triple(SafetyAlertBg, SafetyAlertText, Icons.AutoMirrored.Filled.HelpOutline)
                    }

                    Surface(
                        color = badgeBg,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(badgeIcon, contentDescription = null, tint = badgeText, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = result.primaryLabel.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeText
                            )
                        }
                    }

                    Text(
                        text = result.identificationService,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scientific Name & Authority
                Text(
                    text = result.primaryScientificName,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = colors.textPrimary
                )
                result.authorship?.let {
                    Text(
                        text = "Authority: $it",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Family: ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = colors.textPrimary
                    )
                    Text(
                        text = result.family,
                        fontSize = 13.sp,
                        color = colors.textPrimary
                    )
                    if (result.genus.isNotBlank()) {
                        Text(
                            text = "  •  Genus: ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = colors.textPrimary
                        )
                        Text(
                            text = result.genus,
                            fontStyle = FontStyle.Italic,
                            fontSize = 13.sp,
                            color = colors.textPrimary
                        )
                    }
                }

                if (result.commonNames.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Common Names: ${result.commonNames.joinToString(", ")}",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }

                // Confidence score only if provided by service
                if (result.confidenceScore != null && result.confidenceDocumentation != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = colors.surfaceElevated,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Service Confidence Index: ",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "${(result.confidenceScore * 100).toInt()}%",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = result.confidenceDocumentation,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = colors.borderSubtle)
                Spacer(modifier = Modifier.height(10.dp))

                // Morphological Observations
                Text(
                    text = "MORPHOLOGICAL CHARACTERISTICS OBSERVED",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = result.morphologicalObservations,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = colors.textPrimary
                )
            }
        }

        // Authoritative Taxonomic Cross-Reference Card
        result.taxonomicReference?.let { ref ->
            Surface(
                color = colors.surfaceCard,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BotanicalGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BotanicalGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Authoritative Cross-Reference Found",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = colors.textPrimary
                            )
                        }
                        Surface(
                            color = BotanicalGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = ref.taxonomicStatus,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (colors.isDark) AccentLime else DeepTealDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "The botanical name '${ref.canonicalName}' matches a validated taxon in the ${ref.sourceName}. Rank: ${ref.rank}, Family: ${ref.family}.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Note: Database cross-referencing confirms this botanical name is formally established; it does not substitute for physical voucher specimen verification by a certified herbarium.",
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 15.sp,
                        color = colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ref.powoUrl))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (colors.isDark) colors.brandAccent else colors.brandPrimary
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Text("Kew POWO", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(12.dp))
                        }

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ref.worldFloraOnlineUrl))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (colors.isDark) colors.brandAccent else colors.brandPrimary
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Text("WFO Flora", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(12.dp))
                        }

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ref.gbifUrl))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (colors.isDark) colors.brandAccent else colors.brandPrimary
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Text("GBIF", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }
        }

        // Curated Knowledge Base Match Card (if exists)
        result.curatedMonographMatch?.let { plant ->
            Surface(
                color = if (colors.isDark) colors.surfaceElevated else PrimaryDeepTeal,
                shape = RoundedCornerShape(10.dp),
                border = if (colors.isDark) androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle) else null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalFlorist, contentDescription = null, tint = AccentLime, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Curated Ethnobotanical Record Available",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = PureWhite
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Green AI maintains a verified peer-reviewed monograph for ${plant.scientificName} (${plant.commonNames.firstOrNull() ?: ""}), including documented African traditional uses and laboratory phytochemical literature.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = PureWhite.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onSelectCuratedPlant(plant) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentLime, contentColor = DeepTealDark),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                        ) {
                            Text("View Monograph", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { onAskAboutPlant("What are the documented pharmacological properties and safety warnings for ${plant.scientificName}?") },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PureWhite.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = AccentLime, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Research with AI", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Plausible Alternatives Section
        if (result.alternatives.isNotEmpty()) {
            Surface(
                color = colors.surfaceCard,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PLAUSIBLE ALTERNATIVES & LOOKALIKES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Multiple botanical species share morphological overlap. When visual diagnostic characters are inconclusive, consider these alternatives:",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    result.alternatives.forEach { alt ->
                        AlternativeCandidateRow(alt = alt)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Mandatory Prominent Medical & Botanical Safety Warning Banner
        Surface(
            color = colors.warningBannerBg,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, colors.warningBannerBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = colors.warningBannerBorder,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CRITICAL BOTANICAL SAFETY & CLINICAL WARNING",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp,
                        color = colors.warningBannerText
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result.safetyDisclaimer,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = colors.warningBannerText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "A photographic suggestion does NOT prove chemical composition, medicinal efficacy, or safety. Do NOT ingest or prepare any medicinal remedy based on automated identification. Always consult a certified botanist, pharmacognosist, or medical practitioner.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 16.sp,
                    color = colors.warningBannerText
                )
            }
        }

        // Scan Again Button
        Button(
            onClick = onReset,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                contentColor = if (colors.isDark) DeepTealDark else PureWhite
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = null,
                tint = if (colors.isDark) DeepTealDark else AccentLime
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Scan Another Specimen", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AlternativeCandidateRow(alt: AlternativeSpeciesCandidate) {
    val colors = GreenAiTheme.colors

    Surface(
        color = colors.surfaceElevated,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, colors.borderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = alt.scientificName,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = colors.textPrimary
                )
                if (alt.scorePercentage != null) {
                    Text(
                        text = "${alt.scorePercentage.toInt()}% match",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                    )
                }
            }

            if (alt.family.isNotBlank()) {
                Text(
                    text = "Family: ${alt.family}",
                    fontSize = 11.sp,
                    color = colors.textSecondary
                )
            }

            if (alt.commonNames.isNotEmpty()) {
                Text(
                    text = "Common: ${alt.commonNames.joinToString(", ")}",
                    fontSize = 11.sp,
                    color = colors.textSecondary
                )
            }

            if (alt.reasoning.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = alt.reasoning,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = colors.textPrimary
                )
            }
        }
    }
}

@Composable
fun ExternalPortalCard(portal: ExternalBotanicalPortal) {
    val context = LocalContext.current
    val colors = GreenAiTheme.colors

    Surface(
        color = colors.surfaceCard,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(portal.url))
                context.startActivity(intent)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = portal.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = portal.description,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = "Open portal",
                tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
