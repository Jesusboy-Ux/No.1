package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppDatabase
import com.example.data.model.ResearchMode
import com.example.data.repository.PlantRepository
import androidx.compose.material.icons.filled.Hub
import com.example.ui.components.CitationInspectorDialog
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.BotanicalAtlasScreen
import com.example.ui.screens.EvidenceGraphScreen
import com.example.ui.screens.EvidenceSourcesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IngestionScreen
import com.example.ui.screens.PlantDetailScreen
import com.example.ui.screens.PlantDossierScreen
import com.example.ui.screens.PlantLibraryScreen
import com.example.ui.screens.PlantScannerScreen
import com.example.ui.screens.ResearchScreen
import com.example.ui.screens.SavedResearchScreen
import com.example.ui.theme.AccentLime
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.BotanicalDisplayFontFamily
import com.example.ui.theme.BotanicalEditorialFontFamily
import com.example.ui.theme.DeepTealDark
import com.example.ui.theme.GreenAiColors
import com.example.ui.theme.GreenAiTheme
import com.example.ui.theme.PrimaryDeepTeal
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.GreenAiViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: GreenAiViewModel by viewModels {
        val database = AppDatabase.getInstance(applicationContext)
        val repository = PlantRepository(database)
        GreenAiViewModel.Factory(repository, applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            GreenAiTheme(themeMode = themeMode) {
                GreenAiApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreenAiApp(viewModel: GreenAiViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val selectedPlant by viewModel.selectedPlant.collectAsStateWithLifecycle()
    val plants by viewModel.plants.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterEvidence by viewModel.filterEvidence.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isAiThinking by viewModel.isAiThinking.collectAsStateWithLifecycle()
    val selectedCitationChunk by viewModel.selectedCitationChunk.collectAsStateWithLifecycle()
    val ingestionForm by viewModel.ingestionForm.collectAsStateWithLifecycle()
    val savedBookmarks by viewModel.savedBookmarks.collectAsStateWithLifecycle()
    val scannedPhotos by viewModel.scannedPhotos.collectAsStateWithLifecycle()
    val selectedOrgan by viewModel.selectedOrgan.collectAsStateWithLifecycle()
    val scannerStatus by viewModel.scannerStatus.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val researchMode by viewModel.researchMode.collectAsStateWithLifecycle()
    val notebookSources by viewModel.notebookSources.collectAsStateWithLifecycle()
    val activeSourceIds by viewModel.activeSourceIds.collectAsStateWithLifecycle()
    val evidenceGraphData by viewModel.evidenceGraphData.collectAsStateWithLifecycle()
    val selectedGraphNode by viewModel.selectedGraphNode.collectAsStateWithLifecycle()
    val graphFilterType by viewModel.graphFilterType.collectAsStateWithLifecycle()
    val graphFilterEvidence by viewModel.graphFilterEvidence.collectAsStateWithLifecycle()

    val colors = GreenAiTheme.colors
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Back button handling
    BackHandler(enabled = drawerState.isOpen || selectedPlant != null || (currentTab != AppNavTab.ATLAS && currentTab != AppNavTab.HOME)) {
        when {
            drawerState.isOpen -> coroutineScope.launch { drawerState.close() }
            selectedPlant != null -> viewModel.selectPlant(null)
            currentTab != AppNavTab.ATLAS && currentTab != AppNavTab.HOME -> viewModel.setTab(AppNavTab.ATLAS)
        }
    }

    // Citation inspector dialog
    selectedCitationChunk?.let { chunk ->
        CitationInspectorDialog(
            chunk = chunk,
            onDismiss = { viewModel.selectCitationChunk(null) }
        )
    }

    if (plants.isEmpty()) {
        GreenAiSplashScreen(colors = colors)
    } else if (selectedPlant != null) {
        PlantDossierScreen(
            plant = selectedPlant!!,
            onBack = { viewModel.selectPlant(null) },
            onBookmarkPlant = {
                val p = selectedPlant!!
                viewModel.toggleBookmark(
                    targetId = p.id,
                    type = "PLANT",
                    title = p.scientificName,
                    subtitle = p.commonNames.joinToString(", "),
                    snippet = p.traditionalUses.firstOrNull() ?: p.preparationMethod,
                    evidence = p.reviewStatus
                )
            },
            onAskAboutPlant = { query ->
                viewModel.startResearchWithQuery(query)
            },
            onViewInEvidenceGraph = { plantId ->
                viewModel.navigateToGraphWithPlant(plantId)
            },
            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
        )
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = if (colors.isDark) colors.surfaceElevated else colors.brandPrimary,
                    drawerContentColor = PureWhite,
                    modifier = Modifier.width(310.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Drawer Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .border(0.8.dp, AccentLime.copy(alpha = 0.25f), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.green_ai_logo),
                                        contentDescription = "Official Green AI Logo",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.size(38.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "GREEN AI",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        letterSpacing = 1.2.sp,
                                        color = PureWhite
                                    )
                                    Text(
                                        text = "Team Message ATM (MeNI)",
                                        fontSize = 11.sp,
                                        color = AccentLime
                                    )
                                }
                            }

                            IconButton(onClick = { coroutineScope.launch { drawerState.close() } }) {
                                Icon(Icons.Default.Close, contentDescription = "Close navigation menu", tint = PureWhite)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Preserving African Traditional Medicine Through Evidence-Aware AI",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = PureWhite.copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = PureWhite.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Appearance Theme Controls
                        Text(
                            text = "APPEARANCE & THEME",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = AccentLime
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PureWhite.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val modes = listOf(
                                Triple(AppThemeMode.LIGHT, "Light", Icons.Default.LightMode),
                                Triple(AppThemeMode.DARK, "Dark", Icons.Default.DarkMode),
                                Triple(AppThemeMode.SYSTEM, "System", Icons.Default.BrightnessAuto)
                            )

                            for ((mode, label, icon) in modes) {
                                val isSelected = themeMode == mode
                                Surface(
                                    color = if (isSelected) AccentLime else Color.Transparent,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setThemeMode(mode) }
                                        .testTag("theme_mode_${mode.name.lowercase()}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 7.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = label,
                                            tint = if (isSelected) DeepTealDark else PureWhite,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) DeepTealDark else PureWhite
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = PureWhite.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "RESEARCH PLATFORM SECTIONS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = AccentLime
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val navItems = listOf(
                            Triple(AppNavTab.ATLAS, "Botanical Atlas", Icons.Default.LocalFlorist),
                            Triple(AppNavTab.SCANNER, "Plant Scanner", Icons.Default.CameraAlt),
                            Triple(AppNavTab.RESEARCH, "Research Lab", Icons.Default.Psychology),
                            Triple(AppNavTab.EVIDENCE_GRAPH, "Evidence Graph", Icons.Default.Hub),
                            Triple(AppNavTab.SAVED, "Saved Research", Icons.Default.Bookmark),
                            Triple(AppNavTab.CONTRIBUTE, "Contribute Knowledge", Icons.Default.UploadFile),
                            Triple(AppNavTab.ABOUT, "About Green AI", Icons.Default.Info)
                        )

                        for ((tab, label, icon) in navItems) {
                            val selected = currentTab == tab
                            NavigationDrawerItem(
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 13.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) DeepTealDark else PureWhite
                                    )
                                },
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (selected) DeepTealDark else PureWhite
                                    )
                                },
                                selected = selected,
                                onClick = {
                                    viewModel.setTab(tab)
                                    coroutineScope.launch { drawerState.close() }
                                },
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = AccentLime,
                                    unselectedContainerColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .padding(vertical = 2.dp)
                                    .testTag("drawer_item_${tab.name.lowercase()}")
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f, fill = false))
                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = PureWhite.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Version 1.0 Research Edition\nZero Hallucination Protocol Active",
                            fontSize = 10.sp,
                            color = PureWhite.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    Surface(
                        color = colors.topBarBg,
                        tonalElevation = 2.dp,
                        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { coroutineScope.launch { drawerState.open() } },
                                    modifier = Modifier.testTag("menu_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Open navigation menu",
                                        tint = PureWhite
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .border(0.6.dp, AccentLime.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.green_ai_logo),
                                        contentDescription = "Official Green AI Logo",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "GREEN AI",
                                            fontFamily = BotanicalDisplayFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            letterSpacing = 1.sp,
                                            color = PureWhite
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = AccentLime,
                                            shape = CircleShape
                                        ) {
                                            Text(
                                                text = "MeNI",
                                                fontSize = 8.sp,
                                                fontFamily = BotanicalDisplayFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                color = DeepTealDark,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = currentTab.title.uppercase(),
                                        fontFamily = BotanicalDisplayFontFamily,
                                        fontSize = 10.sp,
                                        letterSpacing = 0.8.sp,
                                        color = AccentLime,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Quick Actions: Theme toggle, Scanner, and AI Research
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Theme toggle switch
                                IconButton(
                                    onClick = { viewModel.toggleTheme() },
                                    modifier = Modifier.testTag("top_bar_theme_toggle")
                                ) {
                                    Icon(
                                        imageVector = if (colors.isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = if (colors.isDark) "Switch to Light mode" else "Switch to Dark mode",
                                        tint = AccentLime
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.setTab(AppNavTab.SCANNER) },
                                    modifier = Modifier.testTag("top_bar_scanner_action")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Plant Scanner",
                                        tint = AccentLime
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.setTab(AppNavTab.RESEARCH) },
                                    modifier = Modifier.testTag("top_bar_research_action")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = "AI Research",
                                        tint = AccentLime
                                    )
                                }
                            }
                        }
                    }
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = colors.bottomNavBg,
                        tonalElevation = 8.dp,
                        windowInsets = WindowInsets.navigationBars,
                        modifier = Modifier
                            .border(0.5.dp, colors.bottomNavBorder)
                            .testTag("green_ai_bottom_nav")
                    ) {
                        val bottomNavItems = listOf(
                            Triple(AppNavTab.ATLAS, "Atlas", Icons.Default.LocalFlorist),
                            Triple(AppNavTab.SCANNER, "Scanner", Icons.Default.CameraAlt),
                            Triple(AppNavTab.RESEARCH, "Lab", Icons.Default.Psychology),
                            Triple(AppNavTab.EVIDENCE_GRAPH, "Graph", Icons.Default.Hub)
                        )

                        for ((tab, label, icon) in bottomNavItems) {
                            val selected = currentTab == tab ||
                                    (tab == AppNavTab.ATLAS && (currentTab == AppNavTab.HOME || currentTab == AppNavTab.LIBRARY)) ||
                                    (tab == AppNavTab.EVIDENCE_GRAPH && currentTab == AppNavTab.EVIDENCE)
                            NavigationBarItem(
                                selected = selected,
                                onClick = { viewModel.setTab(tab) },
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label
                                    )
                                },
                                label = { Text(label, fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                    selectedTextColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                    unselectedIconColor = colors.bottomNavUnselected,
                                    unselectedTextColor = colors.bottomNavUnselected,
                                    indicatorColor = if (colors.isDark) colors.surfaceElevated else AccentLime
                                ),
                                modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) { paddingValues ->
                Crossfade(
                    targetState = currentTab,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) { tab ->
                    when (tab) {
                        AppNavTab.ATLAS, AppNavTab.HOME, AppNavTab.LIBRARY -> {
                            BotanicalAtlasScreen(
                                plants = plants,
                                searchQuery = searchQuery,
                                filterEvidence = filterEvidence,
                                onSearchChange = { q -> viewModel.setSearchQuery(q) },
                                onFilterChange = { level -> viewModel.setFilterEvidence(level) },
                                onSelectPlant = { plant -> viewModel.selectPlant(plant) },
                                onStartResearchQuery = { query -> viewModel.startResearchWithQuery(query) },
                                onViewInEvidenceGraph = { plantId -> viewModel.navigateToGraphWithPlant(plantId) },
                                onOpenScanner = { viewModel.setTab(AppNavTab.SCANNER) }
                            )
                        }
                        AppNavTab.EVIDENCE_GRAPH, AppNavTab.EVIDENCE -> {
                            EvidenceGraphScreen(
                                graphData = evidenceGraphData,
                                selectedNode = selectedGraphNode,
                                selectedTypeFilter = graphFilterType,
                                selectedEvidenceFilter = graphFilterEvidence,
                                plants = plants,
                                onSelectNode = { node -> viewModel.selectGraphNode(node) },
                                onFilterType = { type -> viewModel.setGraphFilterType(type) },
                                onFilterEvidence = { level -> viewModel.setGraphFilterEvidence(level) },
                                onOpenPlantDossier = { plant -> viewModel.selectPlant(plant) },
                                onInvestigateInLab = { query -> viewModel.startResearchWithQuery(query) }
                            )
                        }
                        AppNavTab.RESEARCH -> {
                            ResearchScreen(
                                messages = chatMessages,
                                isThinking = isAiThinking,
                                researchMode = researchMode,
                                notebookSources = notebookSources,
                                activeSourceIds = activeSourceIds,
                                onSelectResearchMode = { mode -> viewModel.setResearchMode(mode) },
                                onToggleSourceActive = { sourceId -> viewModel.toggleSourceActive(sourceId) },
                                onSetAllSourcesActive = { active -> viewModel.setAllSourcesActive(active) },
                                onImportPastedNote = { title, content, author -> viewModel.importPastedNote(title, content, author) },
                                onImportDocumentFile = { uri -> viewModel.importDocumentFile(uri, context) },
                                onDeleteNotebookSource = { sourceId -> viewModel.deleteNotebookSource(sourceId) },
                                onSendMessage = { query -> viewModel.askQuestion(query) },
                                onSelectCitation = { chunk -> viewModel.selectCitationChunk(chunk) },
                                onBookmarkMessage = { msg ->
                                    viewModel.toggleBookmark(
                                        targetId = msg.id,
                                        type = "RESEARCH_ANSWER",
                                        title = "AI Synthesis",
                                        subtitle = msg.retrievedChunks.firstOrNull()?.sourceTitle ?: "Pharmacognosy Evidence",
                                        snippet = msg.text,
                                        evidence = msg.evidenceStatus
                                    )
                                },
                                onCopyText = { text ->
                                    viewModel.copyTextToClipboard(context, text)
                                }
                            )
                        }
                        AppNavTab.SCANNER -> {
                            PlantScannerScreen(
                                scannedPhotos = scannedPhotos,
                                selectedOrgan = selectedOrgan,
                                scannerStatus = scannerStatus,
                                externalPortals = viewModel.externalBotanicalPortals,
                                onSelectOrgan = { organ -> viewModel.setScannerOrgan(organ) },
                                onAddPhoto = { bitmap, organ -> viewModel.addScannedPhoto(bitmap, organ) },
                                onRemovePhoto = { idx -> viewModel.removeScannedPhoto(idx) },
                                onUpdatePhotoOrgan = { idx, organ -> viewModel.updatePhotoOrgan(idx, organ) },
                                onClearScanner = { viewModel.clearScanner() },
                                onSubmitIdentification = { viewModel.submitPlantForIdentification() },
                                onCrossCheckName = { name -> viewModel.crossCheckScientificName(name) },
                                onSelectCuratedPlant = { plant -> viewModel.selectPlant(plant) },
                                onAskAboutPlant = { query -> viewModel.startResearchWithQuery(query) }
                            )
                        }
                        AppNavTab.CONTRIBUTE -> {
                            IngestionScreen(
                                formState = ingestionForm,
                                onUpdateForm = { update -> viewModel.updateIngestionField(update) },
                                onSubmit = { viewModel.submitIngestion() }
                            )
                        }
                        AppNavTab.SAVED -> {
                            SavedResearchScreen(
                                bookmarks = savedBookmarks,
                                allPlants = plants,
                                onSelectPlant = { plant -> viewModel.selectPlant(plant) },
                                onDeleteBookmark = { id -> viewModel.deleteBookmark(id) },
                                onCopyText = { text -> viewModel.copyTextToClipboard(context, text) }
                            )
                        }
                        AppNavTab.ABOUT -> {
                            AboutScreen()
                        }
                    }
                }
            }
        }
    }
}

/**
 * Brand Loading Screen featuring the official Green AI logo
 */
@Composable
fun GreenAiSplashScreen(colors: GreenAiColors) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.brandPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, AccentLime.copy(alpha = 0.35f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.green_ai_logo),
                    contentDescription = "Official Green AI Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(92.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "GREEN AI",
                fontFamily = BotanicalDisplayFontFamily,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.5.sp,
                color = PureWhite
            )

            Text(
                text = "Message Nexus International (MeNI)",
                fontFamily = BotanicalDisplayFontFamily,
                fontSize = 13.sp,
                letterSpacing = 1.sp,
                color = AccentLime
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Evidence-Aware African Traditional Medicine & Botanical Research",
                fontSize = 11.sp,
                color = PureWhite.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(28.dp))

            CircularProgressIndicator(
                color = AccentLime,
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
