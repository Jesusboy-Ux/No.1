package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSender
import com.example.data.model.DocumentChunk
import com.example.data.model.NotebookSource
import com.example.data.model.ResearchMode
import com.example.ui.components.AiMarkdownResponseView
import com.example.ui.components.CitationChip
import com.example.ui.components.EvidenceBadge
import com.example.ui.components.SafetyWarningBanner
import com.example.ui.theme.AccentLime
import com.example.ui.theme.BotanicalDisplayFontFamily
import com.example.ui.theme.DeepTealDark
import com.example.ui.theme.GreenAiColors
import com.example.ui.theme.GreenAiTheme
import com.example.ui.theme.PrimaryDeepTeal
import com.example.ui.theme.PureWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResearchScreen(
    messages: List<ChatMessage>,
    isThinking: Boolean,
    researchMode: ResearchMode,
    notebookSources: List<NotebookSource>,
    activeSourceIds: Set<String>,
    onSelectResearchMode: (ResearchMode) -> Unit,
    onToggleSourceActive: (String) -> Unit,
    onSetAllSourcesActive: (Boolean) -> Unit,
    onImportPastedNote: (title: String, content: String, author: String) -> Unit,
    onImportDocumentFile: (Uri) -> Unit,
    onDeleteNotebookSource: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onSelectCitation: (DocumentChunk) -> Unit,
    onBookmarkMessage: (ChatMessage) -> Unit,
    onCopyText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GreenAiTheme.colors
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var showSourcesDialog by remember { mutableStateOf(false) }

    // File picker launcher for documents
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { onImportDocumentFile(it) }
    }

    val quickQuestions = if (researchMode == ResearchMode.LOCAL_NOTEBOOK_RAG) {
        listOf(
            "Active phytochemicals in Artemisia afra?",
            "Is Bitter Leaf safe in pregnancy?",
            "Clinical trials for Devil's Claw in back pain",
            "Moringa leaf nutrition vs root bark toxicity",
            "Catharanthus roseus traditional uses & vinca alkaloids"
        )
    } else {
        listOf(
            "Recent 2024 studies on Sutherlandia frutescens?",
            "Global pharmacology of African ginger (Siphonochilus aethiopicus)",
            "How does Prunus africana compare to synthetic BPH medications?",
            "Phytochemical extraction protocols for Pelargonium sidoides"
        )
    }

    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Sources Management Dialog
    if (showSourcesDialog) {
        NotebookSourcesDialog(
            sources = notebookSources,
            activeSourceIds = activeSourceIds,
            onToggleSource = onToggleSourceActive,
            onSetAllActive = onSetAllSourcesActive,
            onOpenFilePicker = {
                filePickerLauncher.launch(
                    arrayOf(
                        "text/plain",
                        "text/markdown",
                        "text/csv",
                        "application/json",
                        "*/*"
                    )
                )
            },
            onSavePastedNote = onImportPastedNote,
            onDeleteSource = onDeleteNotebookSource,
            onDismiss = { showSourcesDialog = false },
            colors = colors
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.screenBackground)
            .imePadding()
    ) {
        // Top Research Mode Switcher (Local Notebook RAG vs Direct Internet AI)
        ResearchModeSelector(
            currentMode = researchMode,
            onSelectMode = onSelectResearchMode,
            colors = colors
        )

        // Sources Status Bar (visible in Local Notebook RAG mode)
        if (researchMode == ResearchMode.LOCAL_NOTEBOOK_RAG) {
            NotebookSourcesStatusBar(
                activeSourceCount = activeSourceIds.size,
                totalSourceCount = notebookSources.size + 1, // +1 for curated corpus
                onManageSources = { showSourcesDialog = true },
                colors = colors
            )
        } else {
            // Direct Internet Mode Disclaimer Bar
            DirectInternetNoticeBar(colors = colors)
        }

        // Chat Messages List
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            item {
                ResearchWorkspaceHeader(
                    mode = researchMode,
                    colors = colors
                )
            }

            item {
                Column {
                    Text(
                        text = if (researchMode == ResearchMode.LOCAL_NOTEBOOK_RAG) {
                            "Curated Grounded Inquiries:"
                        } else {
                            "Live Global Research Inquiries:"
                        },
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quickQuestions) { query ->
                            Surface(
                                color = colors.surfaceCard,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(6.dp))
                                    .clickable { onSendMessage(query) }
                                    .testTag("quick_query_chip")
                            ) {
                                Text(
                                    text = query,
                                    fontSize = 12.sp,
                                    color = colors.textPrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            items(messages, key = { it.id }) { message ->
                ResearchMessageItem(
                    message = message,
                    colors = colors,
                    onSelectCitation = onSelectCitation,
                    onBookmark = { onBookmarkMessage(message) },
                    onCopy = { onCopyText(message.text) }
                )
            }

            if (isThinking) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(8.dp))
                            .testTag("ai_thinking_card")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (colors.isDark) colors.surfaceElevated else colors.brandPrimary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.green_ai_logo),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                color = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (researchMode == ResearchMode.LOCAL_NOTEBOOK_RAG) {
                                        "Retrieving Active Notebook & Curated Sources..."
                                    } else {
                                        "Querying Live Global Botanical Intelligence..."
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = if (researchMode == ResearchMode.LOCAL_NOTEBOOK_RAG) {
                                        "Matching selected materials against strict evidence tiers."
                                    } else {
                                        "Synthesizing open-web pharmacognosy knowledge via Gemini 3.5."
                                    },
                                    fontSize = 11.sp,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Clean Research Input Bar
        Surface(
            tonalElevation = 4.dp,
            color = colors.surfaceCard,
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, colors.borderSubtle)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = if (researchMode == ResearchMode.LOCAL_NOTEBOOK_RAG) {
                                "Ask query grounded in active sources..."
                            } else {
                                "Ask unconstrained internet AI query..."
                            },
                            fontSize = 13.sp,
                            color = colors.textMuted
                        )
                    },
                    maxLines = 3,
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
                        .testTag("research_query_input")
                )

                Spacer(modifier = Modifier.width(8.dp))

                val isReadyToSend = inputText.isNotBlank() && !isThinking
                IconButton(
                    onClick = {
                        if (isReadyToSend) {
                            val text = inputText
                            inputText = ""
                            onSendMessage(text)
                        }
                    },
                    enabled = isReadyToSend,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isReadyToSend) {
                                if (colors.isDark) colors.brandAccent else colors.brandPrimary
                            } else {
                                colors.borderSubtle
                            }
                        )
                        .testTag("send_query_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send research inquiry",
                        tint = if (isReadyToSend) {
                            if (colors.isDark) PrimaryDeepTeal else AccentLime
                        } else {
                            colors.textMuted
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Dedicated Segmented Pill Switcher between Local Notebook RAG and Direct Internet AI
 */
@Composable
fun ResearchModeSelector(
    currentMode: ResearchMode,
    onSelectMode: (ResearchMode) -> Unit,
    colors: GreenAiColors
) {
    Surface(
        color = colors.surfaceCard,
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, colors.borderSubtle)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surfaceElevated, RoundedCornerShape(8.dp))
                .border(0.8.dp, colors.borderSubtle, RoundedCornerShape(8.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Mode 1: Local Notebook RAG
            val isLocalSelected = currentMode == ResearchMode.LOCAL_NOTEBOOK_RAG
            Surface(
                color = if (isLocalSelected) {
                    if (colors.isDark) colors.brandAccent else colors.brandPrimary
                } else Color.Transparent,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelectMode(ResearchMode.LOCAL_NOTEBOOK_RAG) }
                    .testTag("mode_toggle_local_rag")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = if (isLocalSelected) {
                            if (colors.isDark) PrimaryDeepTeal else PureWhite
                        } else colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Notebook RAG",
                        fontWeight = if (isLocalSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (isLocalSelected) {
                            if (colors.isDark) PrimaryDeepTeal else PureWhite
                        } else colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Mode 2: Direct Internet AI
            val isInternetSelected = currentMode == ResearchMode.DIRECT_INTERNET_AI
            Surface(
                color = if (isInternetSelected) {
                    if (colors.isDark) colors.brandAccent else colors.brandPrimary
                } else Color.Transparent,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelectMode(ResearchMode.DIRECT_INTERNET_AI) }
                    .testTag("mode_toggle_direct_internet")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = if (isInternetSelected) {
                            if (colors.isDark) PrimaryDeepTeal else PureWhite
                        } else colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Direct Internet AI",
                        fontWeight = if (isInternetSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (isInternetSelected) {
                            if (colors.isDark) PrimaryDeepTeal else PureWhite
                        } else colors.textSecondary
                    )
                }
            }
        }
    }
}

/**
 * Status bar showing active source count and "Manage Sources" button
 */
@Composable
fun NotebookSourcesStatusBar(
    activeSourceCount: Int,
    totalSourceCount: Int,
    onManageSources: () -> Unit,
    colors: GreenAiColors
) {
    Surface(
        color = colors.surfaceElevated,
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, colors.borderSubtle)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = null,
                    tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Grounded Sources: $activeSourceCount of $totalSourceCount active",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                )
            }

            Surface(
                color = if (colors.isDark) colors.surfaceCard else colors.brandPrimary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .border(
                        0.7.dp,
                        if (colors.isDark) colors.borderSubtle else colors.brandPrimary.copy(alpha = 0.3f),
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { onManageSources() }
                    .testTag("manage_sources_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Manage Sources",
                        tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Manage Sources",
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
 * Notice bar when Direct Internet AI mode is selected
 */
@Composable
fun DirectInternetNoticeBar(colors: GreenAiColors) {
    Surface(
        color = if (colors.isDark) Color(0xFF132F38) else Color(0xFFE8F4F8),
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, if (colors.isDark) Color(0xFF244A56) else Color(0xFFBCE0EC))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = if (colors.isDark) Color(0xFF64D2EC) else Color(0xFF007A99),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Live AI Mode: Answers draw unconstrained from global pharmacology & web knowledge.",
                fontSize = 11.sp,
                color = if (colors.isDark) Color(0xFFB4E6F3) else Color(0xFF004D60)
            )
        }
    }
}

@Composable
fun ResearchWorkspaceHeader(
    mode: ResearchMode,
    colors: GreenAiColors
) {
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
            .testTag("research_intro_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.12f))
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
                        text = if (mode == ResearchMode.LOCAL_NOTEBOOK_RAG) {
                            "RESEARCH LAB: NOTEBOOK RAG"
                        } else {
                            "RESEARCH LAB: DIRECT INTERNET AI"
                        },
                        fontFamily = BotanicalDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 0.8.sp,
                        color = PureWhite
                    )
                    Text(
                        text = if (mode == ResearchMode.LOCAL_NOTEBOOK_RAG) {
                            "Grounded in Active Sources & Verified African Monographs"
                        } else {
                            "Broad Pharmacological & Web Intelligence via Gemini 3.5"
                        },
                        fontSize = 11.sp,
                        color = AccentLime
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (mode == ResearchMode.LOCAL_NOTEBOOK_RAG) {
                    "Queries are strictly retrieved and grounded against your active notebook materials and peer-reviewed African ethnobotanical monographs with exact source citations."
                } else {
                    "Ask open-ended botanical and pharmacological questions without local corpus restrictions. Powered by direct internet/AI knowledge synthesis."
                },
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = PureWhite.copy(alpha = 0.9f)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResearchMessageItem(
    message: ChatMessage,
    colors: GreenAiColors,
    onSelectCitation: (DocumentChunk) -> Unit,
    onBookmark: () -> Unit,
    onCopy: () -> Unit
) {
    if (message.sender == ChatSender.USER) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                color = if (colors.isDark) colors.surfaceElevated else colors.brandPrimary,
                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 2.dp, bottomStart = 10.dp, bottomEnd = 10.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .border(
                        0.5.dp,
                        if (colors.isDark) colors.borderSubtle else Color.Transparent,
                        RoundedCornerShape(topStart = 10.dp, topEnd = 2.dp, bottomStart = 10.dp, bottomEnd = 10.dp)
                    )
                    .testTag("user_chat_bubble")
            ) {
                Text(
                    text = message.text,
                    color = PureWhite,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.aiCardBg),
                shape = RoundedCornerShape(topStart = 2.dp, topEnd = 10.dp, bottomStart = 10.dp, bottomEnd = 10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        0.7.dp,
                        colors.aiCardBorder,
                        RoundedCornerShape(topStart = 2.dp, topEnd = 10.dp, bottomStart = 10.dp, bottomEnd = 10.dp)
                    )
                    .testTag("assistant_chat_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Bar with Evidence Status & Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Green AI Analysis",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = colors.aiTextHeading
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            // Mode-specific indicator
                            if (message.researchMode == ResearchMode.DIRECT_INTERNET_AI) {
                                Surface(
                                    color = if (colors.isDark) Color(0xFF1E3A44) else Color(0xFFE0F2F7),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.border(
                                        0.5.dp,
                                        if (colors.isDark) Color(0xFF35697B) else Color(0xFFA5D8E8),
                                        RoundedCornerShape(4.dp)
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Public,
                                            contentDescription = null,
                                            tint = if (colors.isDark) Color(0xFF64D2EC) else Color(0xFF007A99),
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Direct Internet AI",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (colors.isDark) Color(0xFF64D2EC) else Color(0xFF007A99)
                                        )
                                    }
                                }
                            } else {
                                message.evidenceStatus?.let { status ->
                                    EvidenceBadge(level = status)
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Copy button (Touch target >= 48dp)
                            IconButton(
                                onClick = onCopy,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("copy_research_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy research analysis",
                                    tint = colors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Bookmark button
                            IconButton(
                                onClick = onBookmark,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("bookmark_research_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = "Save analysis to bookmarks",
                                    tint = colors.textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Structured Main Answer Content rendered with AiMarkdownResponseView
                    AiMarkdownResponseView(
                        markdownText = message.text,
                        colors = colors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Safety Warning Banner
                    message.safetyWarning?.let { warning ->
                        Spacer(modifier = Modifier.height(12.dp))
                        SafetyWarningBanner(warningText = warning)
                    }

                    // Cited Sources Section (in Local RAG mode)
                    if (message.retrievedChunks.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Grounded Literature Citations (${message.retrievedChunks.size}):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for ((idx, chunk) in message.retrievedChunks.withIndex()) {
                                CitationChip(
                                    index = idx + 1,
                                    chunk = chunk,
                                    onClick = { onSelectCitation(chunk) }
                                )
                            }
                        }
                    }

                    // Grounding Notice
                    message.externalNotice?.let { notice ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = notice,
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Notebook Sources Dialog:
 * Allows user to manage local RAG materials:
 * 1. Upload files (.txt, .md, .csv)
 * 2. Paste custom research notes
 * 3. Filter citations with checkboxes
 * 4. Remove uploaded materials
 */
@Composable
fun NotebookSourcesDialog(
    sources: List<NotebookSource>,
    activeSourceIds: Set<String>,
    onToggleSource: (String) -> Unit,
    onSetAllActive: (Boolean) -> Unit,
    onOpenFilePicker: () -> Unit,
    onSavePastedNote: (title: String, content: String, author: String) -> Unit,
    onDeleteSource: (String) -> Unit,
    onDismiss: () -> Unit,
    colors: GreenAiColors
) {
    var showPasteForm by remember { mutableStateOf(false) }
    var pasteTitle by remember { mutableStateOf("") }
    var pasteAuthor by remember { mutableStateOf("") }
    var pasteContent by remember { mutableStateOf("") }

    val isCuratedCorpusActive = activeSourceIds.contains("CURATED_CORPUS")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.surfaceCard,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, colors.borderSubtle, RoundedCornerShape(12.dp))
                .testTag("notebook_sources_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "NotebookLM Research Sources",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Manage & filter materials cited in queries",
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actions Bar: Upload File & Paste Note
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenFilePicker,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                            contentColor = if (colors.isDark) PrimaryDeepTeal else PureWhite
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("upload_file_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Document", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { showPasteForm = !showPasteForm },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("paste_note_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.NoteAdd,
                            contentDescription = null,
                            tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (showPasteForm) "Hide Form" else "Paste Note",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                        )
                    }
                }

                // Collapsible Paste Note Form
                AnimatedVisibility(visible = showPasteForm) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceElevated),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                            .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(8.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "New Research Note",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = pasteTitle,
                                onValueChange = { pasteTitle = it },
                                placeholder = { Text("Title (e.g. Field Study on Sutherlandia)", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("paste_note_title_input")
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = pasteAuthor,
                                onValueChange = { pasteAuthor = it },
                                placeholder = { Text("Author / Origin (e.g. Dr. K. Mthembu, 2023)", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("paste_note_author_input")
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = pasteContent,
                                onValueChange = { pasteContent = it },
                                placeholder = { Text("Paste monograph, field observations, or notes...", fontSize = 11.sp) },
                                maxLines = 4,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("paste_note_content_input")
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    if (pasteContent.isNotBlank()) {
                                        onSavePastedNote(pasteTitle, pasteContent, pasteAuthor)
                                        pasteTitle = ""
                                        pasteAuthor = ""
                                        pasteContent = ""
                                        showPasteForm = false
                                    }
                                },
                                enabled = pasteContent.isNotBlank(),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                    contentColor = if (colors.isDark) PrimaryDeepTeal else PureWhite
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("save_pasted_note_button")
                            ) {
                                Text("Index Note into RAG", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Select All / Deselect All Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Sources (${activeSourceIds.size}):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Row {
                        TextButton(
                            onClick = { onSetAllActive(true) },
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Text("Select All", fontSize = 11.sp, color = if (colors.isDark) colors.brandAccent else colors.brandPrimary)
                        }
                        TextButton(
                            onClick = { onSetAllActive(false) },
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Text("Deselect All", fontSize = 11.sp, color = colors.textSecondary)
                        }
                    }
                }

                HorizontalDivider(color = colors.borderSubtle)

                // Scrollable Sources List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    // Item 1: Curated African Medicinal Corpus
                    item {
                        SourceItemRow(
                            title = "Curated African Medicinal Flora Database",
                            authorOrOrigin = "POWO / WFO / WHO Monographs (80+ Verified Studies)",
                            tag = "BUILT-IN CORPUS",
                            details = "Peer-reviewed pharmacological evidence",
                            isActive = isCuratedCorpusActive,
                            canDelete = false,
                            onToggle = { onToggleSource("CURATED_CORPUS") },
                            onDelete = {},
                            colors = colors
                        )
                    }

                    // User Imported Sources
                    items(sources, key = { it.id }) { src ->
                        SourceItemRow(
                            title = src.title,
                            authorOrOrigin = src.authorOrOrigin,
                            tag = if (src.sourceType == "FILE_UPLOAD") "FILE" else "NOTE",
                            details = "${src.chunkCount} chunks • ${src.wordCount} words",
                            isActive = activeSourceIds.contains(src.id),
                            canDelete = true,
                            onToggle = { onToggleSource(src.id) },
                            onDelete = { onDeleteSource(src.id) },
                            colors = colors
                        )
                    }

                    if (sources.isEmpty()) {
                        item {
                            Surface(
                                color = colors.surfaceElevated,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = colors.textSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "No custom documents yet. Upload files (.txt, .md, .csv) or paste field study notes above.",
                                        fontSize = 11.sp,
                                        color = colors.textSecondary,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Done button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                        contentColor = if (colors.isDark) PrimaryDeepTeal else PureWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("close_sources_dialog_button")
                ) {
                    Text("Apply & Return to Research", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

/**
 * Individual source item row with checkbox, metadata, and optional delete action
 */
@Composable
fun SourceItemRow(
    title: String,
    authorOrOrigin: String,
    tag: String,
    details: String,
    isActive: Boolean,
    canDelete: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    colors: GreenAiColors
) {
    Surface(
        color = if (isActive) colors.surfaceElevated else colors.surfaceCard,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                0.6.dp,
                if (isActive) (if (colors.isDark) colors.brandAccent.copy(alpha = 0.5f) else colors.brandPrimary.copy(alpha = 0.4f)) else colors.borderSubtle,
                RoundedCornerShape(8.dp)
            )
            .clickable { onToggle() }
            .testTag("source_row_${title.take(10)}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isActive,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                    checkmarkColor = if (colors.isDark) PrimaryDeepTeal else PureWhite,
                    uncheckedColor = colors.textSecondary
                ),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = colors.textPrimary,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(
                        color = if (colors.isDark) colors.surfaceCard else Color(0xFFE8ECEF),
                        shape = RoundedCornerShape(3.dp)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textSecondary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Text(
                    text = authorOrOrigin,
                    fontSize = 10.sp,
                    color = colors.textSecondary,
                    maxLines = 1
                )

                Text(
                    text = details,
                    fontSize = 9.sp,
                    color = colors.textMuted
                )
            }

            if (canDelete) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete source",
                        tint = Color(0xFFE57373),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
