package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EvidenceLevel
import com.example.ui.theme.AccentLime
import com.example.ui.theme.DeepTealDark
import com.example.ui.theme.GreenAiColors
import com.example.ui.theme.GreenAiTheme
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.IngestionFormState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IngestionScreen(
    formState: IngestionFormState,
    onUpdateForm: ((IngestionFormState) -> IngestionFormState) -> Unit,
    onSubmit: () -> Unit,
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
        // Ingestion Header Card
        Card(
            colors = CardDefaults.cardColors(containerColor = if (colors.isDark) colors.surfaceElevated else colors.brandPrimary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(0.7.dp, if (colors.isDark) colors.borderSubtle else Color.Transparent, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = null,
                        tint = AccentLime,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Document & Record Ingestion",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = PureWhite
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Add verified plant monographs, published research excerpts, or ethnobotanical survey texts. Ingested passages are immediately indexed in the local knowledge base and made searchable for grounded research synthesis.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = PureWhite.copy(alpha = 0.85f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Success / Error alerts
        formState.successMessage?.let { success ->
            Card(
                colors = CardDefaults.cardColors(containerColor = if (colors.isDark) Color(0xFF064E3B) else Color(0xFFD1FAE5)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (colors.isDark) Color(0xFF6EE7B7) else Color(0xFF047857),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = success,
                        color = if (colors.isDark) PureWhite else Color(0xFF065F46),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        formState.errorMessage?.let { error ->
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.alertBg),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.alertBorder.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = colors.alertBorder,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        color = colors.alertText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
            focusedBorderColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
            unfocusedBorderColor = colors.borderSubtle,
            focusedContainerColor = colors.surfaceElevated,
            unfocusedContainerColor = colors.surfaceElevated,
            focusedLabelColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
            unfocusedLabelColor = colors.textSecondary
        )

        // Form Fields
        Text(
            text = "Botanical Taxonomy & Identification",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.scientificName,
            onValueChange = { value -> onUpdateForm { it.copy(scientificName = value) } },
            label = { Text("Scientific Name (e.g. Securidaca longepedunculata Fresen.)") },
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ingest_scientific_name")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.commonNames,
            onValueChange = { value -> onUpdateForm { it.copy(commonNames = value) } },
            label = { Text("Common Names (comma separated e.g. Violet Tree, Ipeta)") },
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ingest_common_names")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = formState.family,
                onValueChange = { value -> onUpdateForm { it.copy(family = value) } },
                label = { Text("Family (e.g. Polygalaceae)") },
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = formState.plantParts,
                onValueChange = { value -> onUpdateForm { it.copy(plantParts = value) } },
                label = { Text("Parts (e.g. Roots, Leaves)") },
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.localNames,
            onValueChange = { value -> onUpdateForm { it.copy(localNames = value) } },
            label = { Text("Documented Local/Indigenous African Names (e.g. Yoruba: Ipeta; Hausa: Uwar magunguna)") },
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ethnobotanical & Scientific Evidence",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.traditionalUses,
            onValueChange = { value -> onUpdateForm { it.copy(traditionalUses = value) } },
            label = { Text("Traditionally Reported Uses (pipe-separated e.g. Fever | Toothache | Inflammation)") },
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.preparation,
            onValueChange = { value -> onUpdateForm { it.copy(preparation = value) } },
            label = { Text("Preparation Method as Reported in Source") },
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.phytochemicals,
            onValueChange = { value -> onUpdateForm { it.copy(phytochemicals = value) } },
            label = { Text("Phytochemical Findings (e.g. Methyl salicylate, saponins, xanthones)") },
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.laboratoryEvidence,
            onValueChange = { value -> onUpdateForm { it.copy(laboratoryEvidence = value) } },
            label = { Text("Laboratory & In Vitro Findings") },
            maxLines = 3,
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.safetyInformation,
            onValueChange = { value -> onUpdateForm { it.copy(safetyInformation = value) } },
            label = { Text("Safety, Toxicity & Contraindications (Crucial)") },
            maxLines = 3,
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Research Document & Chunk Indexing",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = formState.sourceAuthor,
                onValueChange = { value -> onUpdateForm { it.copy(sourceAuthor = value) } },
                label = { Text("Authors") },
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                modifier = Modifier.weight(1.5f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = formState.sourceYear,
                onValueChange = { value -> onUpdateForm { it.copy(sourceYear = value) } },
                label = { Text("Year") },
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.sourceTitle,
            onValueChange = { value -> onUpdateForm { it.copy(sourceTitle = value) } },
            label = { Text("Source Publication / Document Title") },
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.excerptChunkText,
            onValueChange = { value -> onUpdateForm { it.copy(excerptChunkText = value) } },
            label = { Text("Text Passage / Document Chunk to Index for RAG Retrieval") },
            maxLines = 5,
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ingest_chunk_text")
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Evidence Level Assignment:",
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = colors.textSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            for (level in EvidenceLevel.entries) {
                val isSelected = formState.evidenceTier == level
                FilterChip(
                    selected = isSelected,
                    onClick = { onUpdateForm { it.copy(evidenceTier = level) } },
                    label = { Text(level.label, fontSize = 11.sp) },
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

        Spacer(modifier = Modifier.height(20.dp))

        // Submit Button (touch target >= 48dp)
        Button(
            onClick = onSubmit,
            enabled = !formState.isSubmitting,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                contentColor = if (colors.isDark) DeepTealDark else PureWhite
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("submit_ingestion_button")
        ) {
            if (formState.isSubmitting) {
                CircularProgressIndicator(
                    color = if (colors.isDark) DeepTealDark else PureWhite,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Indexing Document...", fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ingest & Index Record", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
