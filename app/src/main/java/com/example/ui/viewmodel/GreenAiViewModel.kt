package com.example.ui.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CuratedKnowledgeBase
import com.example.data.api.ExternalBotanicalPortal
import com.example.data.api.PlantScannerService
import com.example.data.api.ScanServiceStatus
import android.net.Uri
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSender
import com.example.data.model.DocumentChunk
import com.example.data.model.EvidenceGraphData
import com.example.data.model.EvidenceLevel
import com.example.data.model.GraphNode
import com.example.data.model.GraphNodeType
import com.example.data.model.IdentificationLabel
import com.example.data.model.MedicinalPlant
import com.example.data.model.NotebookSource
import com.example.data.model.PlantOrgan
import com.example.data.model.PlantScanResult
import com.example.data.model.ResearchMode
import com.example.data.model.SavedBookmark
import com.example.data.model.SourceReference
import com.example.data.repository.PlantRepository
import com.example.domain.DocumentChunker
import com.example.domain.EvidenceGraphBuilder
import com.example.ui.theme.AppThemeMode
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppNavTab(val title: String, val subtitle: String) {
    ATLAS("Botanical Atlas", "African Medicinal Taxonomy & Herbarium"),
    SCANNER("Plant Scanner", "Optical Botanical Identification & POWO Verification"),
    RESEARCH("Research Lab", "Evidence-Grounded AI Pharmacognosy"),
    EVIDENCE_GRAPH("Evidence Graph", "Relational Phytochemical & Clinical Map"),
    CONTRIBUTE("Contribute", "Submit Ethnobotanical Field Data"),
    SAVED("Saved Research", "Bookmarked Dossiers & Citations"),
    ABOUT("About Green AI", "MeNI Research Charter & Methodologies"),

    // Aliases to maintain complete backwards compatibility
    HOME("Botanical Atlas", "African Medicinal Taxonomy & Herbarium"),
    LIBRARY("Botanical Atlas", "African Medicinal Taxonomy & Herbarium"),
    EVIDENCE("Evidence Graph", "Relational Phytochemical & Clinical Map")
}

data class IngestionFormState(
    val scientificName: String = "",
    val commonNames: String = "",
    val family: String = "",
    val localNames: String = "",
    val plantParts: String = "",
    val traditionalUses: String = "",
    val preparation: String = "",
    val phytochemicals: String = "",
    val laboratoryEvidence: String = "",
    val clinicalEvidence: String = "",
    val safetyInformation: String = "",
    val limitations: String = "",
    val sourceAuthor: String = "",
    val sourceYear: String = "2024",
    val sourceTitle: String = "",
    val sourceJournal: String = "",
    val sourceDoiOrUrl: String = "",
    val excerptChunkText: String = "",
    val evidenceTier: EvidenceLevel = EvidenceLevel.TRADITIONAL_USE_REPORTED,
    val isSubmitting: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class GreenAiViewModel(
    private val repository: PlantRepository,
    context: Context? = null
) : ViewModel() {

    private val prefs = context?.getSharedPreferences("green_ai_user_preferences", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(
        prefs?.getString("green_ai_theme_mode", AppThemeMode.LIGHT.name)?.let {
            try { AppThemeMode.valueOf(it) } catch (e: Exception) { AppThemeMode.LIGHT }
        } ?: AppThemeMode.LIGHT
    )
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        prefs?.edit()?.putString("green_ai_theme_mode", mode.name)?.apply()
    }

    fun toggleTheme() {
        val next = when (_themeMode.value) {
            AppThemeMode.LIGHT -> AppThemeMode.DARK
            AppThemeMode.DARK -> AppThemeMode.LIGHT
            AppThemeMode.SYSTEM -> AppThemeMode.DARK
        }
        setThemeMode(next)
    }

    private val _currentTab = MutableStateFlow(AppNavTab.HOME)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    private val _selectedPlant = MutableStateFlow<MedicinalPlant?>(null)
    val selectedPlant: StateFlow<MedicinalPlant?> = _selectedPlant.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterEvidence = MutableStateFlow<EvidenceLevel?>(null)
    val filterEvidence: StateFlow<EvidenceLevel?> = _filterEvidence.asStateFlow()

    private val _researchMode = MutableStateFlow(ResearchMode.LOCAL_NOTEBOOK_RAG)
    val researchMode: StateFlow<ResearchMode> = _researchMode.asStateFlow()

    fun setResearchMode(mode: ResearchMode) {
        _researchMode.value = mode
    }

    // Notebook Sources (NotebookLM personal materials)
    val notebookSources: StateFlow<List<NotebookSource>> = repository.getAllNotebookSources()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _activeSourceIds = MutableStateFlow<Set<String>>(setOf("CURATED_CORPUS"))
    val activeSourceIds: StateFlow<Set<String>> = _activeSourceIds.asStateFlow()

    fun toggleSourceActive(sourceId: String) {
        val current = _activeSourceIds.value.toMutableSet()
        if (current.contains(sourceId)) {
            current.remove(sourceId)
        } else {
            current.add(sourceId)
        }
        _activeSourceIds.value = current
    }

    fun setAllSourcesActive(active: Boolean) {
        if (active) {
            val allIds = notebookSources.value.map { it.id }.toMutableSet()
            allIds.add("CURATED_CORPUS")
            _activeSourceIds.value = allIds
        } else {
            _activeSourceIds.value = emptySet()
        }
    }

    fun importPastedNote(title: String, content: String, author: String) {
        val cleanTitle = title.trim().ifBlank { "Untitled Research Note" }
        val cleanContent = content.trim()
        if (cleanContent.isBlank()) return

        val sourceId = "NOTE-" + UUID.randomUUID().toString().take(8)
        val chunks = DocumentChunker.chunkText(
            sourceId = sourceId,
            sourceTitle = cleanTitle,
            authorOrOrigin = author.trim().ifBlank { "Pasted Research Note" },
            rawText = cleanContent
        )
        val wordCount = cleanContent.split(Regex("\\s+")).filter { it.isNotEmpty() }.size

        val source = NotebookSource(
            id = sourceId,
            title = cleanTitle,
            authorOrOrigin = author.trim().ifBlank { "Pasted Research Note" },
            sourceType = "PASTED_NOTE",
            rawContent = cleanContent,
            chunkCount = chunks.size,
            wordCount = wordCount,
            isActive = true
        )

        viewModelScope.launch {
            repository.insertNotebookSource(source, chunks)
            val updated = _activeSourceIds.value.toMutableSet()
            updated.add(sourceId)
            _activeSourceIds.value = updated
        }
    }

    fun importDocumentFile(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                var fileName = "Imported Document"
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        fileName = cursor.getString(nameIndex)
                    }
                }

                val content = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).readText()
                } ?: return@launch

                val cleanContent = content.trim()
                if (cleanContent.isBlank()) return@launch

                val sourceId = "DOC-" + UUID.randomUUID().toString().take(8)
                val chunks = DocumentChunker.chunkText(
                    sourceId = sourceId,
                    sourceTitle = fileName,
                    authorOrOrigin = "Document File Upload",
                    rawText = cleanContent
                )
                val wordCount = cleanContent.split(Regex("\\s+")).filter { it.isNotEmpty() }.size

                val source = NotebookSource(
                    id = sourceId,
                    title = fileName,
                    authorOrOrigin = "File: $fileName",
                    sourceType = "FILE_UPLOAD",
                    rawContent = cleanContent,
                    chunkCount = chunks.size,
                    wordCount = wordCount,
                    isActive = true
                )

                repository.insertNotebookSource(source, chunks)
                val updated = _activeSourceIds.value.toMutableSet()
                updated.add(sourceId)
                _activeSourceIds.value = updated
                Toast.makeText(context, "Added $fileName (${chunks.size} chunks) to Notebook Sources", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to read document: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteNotebookSource(sourceId: String) {
        viewModelScope.launch {
            repository.deleteNotebookSource(sourceId)
            val updated = _activeSourceIds.value.toMutableSet()
            updated.remove(sourceId)
            _activeSourceIds.value = updated
        }
    }

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _selectedCitationChunk = MutableStateFlow<DocumentChunk?>(null)
    val selectedCitationChunk: StateFlow<DocumentChunk?> = _selectedCitationChunk.asStateFlow()

    private val _ingestionForm = MutableStateFlow(IngestionFormState())
    val ingestionForm: StateFlow<IngestionFormState> = _ingestionForm.asStateFlow()

    // Plant Scanner integration
    private val plantScannerService = PlantScannerService()

    private val _scannedPhotos = MutableStateFlow<List<Pair<Bitmap, PlantOrgan>>>(emptyList())
    val scannedPhotos: StateFlow<List<Pair<Bitmap, PlantOrgan>>> = _scannedPhotos.asStateFlow()

    private val _selectedOrgan = MutableStateFlow(PlantOrgan.LEAF)
    val selectedOrgan: StateFlow<PlantOrgan> = _selectedOrgan.asStateFlow()

    private val _scannerStatus = MutableStateFlow<ScanServiceStatus>(ScanServiceStatus.Idle)
    val scannerStatus: StateFlow<ScanServiceStatus> = _scannerStatus.asStateFlow()

    val externalBotanicalPortals: List<ExternalBotanicalPortal> = plantScannerService.externalPortals

    val allReferences: List<SourceReference> = CuratedKnowledgeBase.curatedReferences

    // Saved bookmarks from Room
    val savedBookmarks: StateFlow<List<SavedBookmark>> = repository.getAllBookmarks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filtered plant list
    val plants: StateFlow<List<MedicinalPlant>> = combine(
        repository.getAllPlants(),
        _searchQuery,
        _filterEvidence
    ) { allPlants, query, evidenceFilter ->
        allPlants.filter { plant ->
            val matchesQuery = query.isBlank() ||
                    plant.scientificName.contains(query, ignoreCase = true) ||
                    plant.commonNamesList.contains(query, ignoreCase = true) ||
                    plant.family.contains(query, ignoreCase = true) ||
                    plant.localNamesJson.contains(query, ignoreCase = true) ||
                    plant.traditionalUsesList.contains(query, ignoreCase = true) ||
                    plant.phytochemicalsList.contains(query, ignoreCase = true)

            val matchesEvidence = evidenceFilter == null || plant.reviewStatus == evidenceFilter

            matchesQuery && matchesEvidence
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CuratedKnowledgeBase.curatedPlants
    )

    init {
        viewModelScope.launch {
            repository.initializeDatabase()
        }
        _chatMessages.value = listOf(
            ChatMessage(
                id = "welcome-01",
                sender = ChatSender.ASSISTANT,
                text = "Welcome to Green AI Research Workspace — developed by Team Message ATM (MeNI).\n\n" +
                        "This platform bridges centuries-old African traditional botanical knowledge with verified modern laboratory, animal, and clinical research. Every inquiry retrieves identifiable document passages from our curated database before generating evidence-grounded answers.\n\n" +
                        "Explore medicinal plant indications, active phytochemicals, preparation methods, and documented safety contraindications.",
                timestamp = System.currentTimeMillis(),
                isEvidenceGrounded = true
            )
        )
    }

    fun setTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun selectPlant(plant: MedicinalPlant?) {
        _selectedPlant.value = plant
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterEvidence(level: EvidenceLevel?) {
        _filterEvidence.value = level
    }

    fun selectCitationChunk(chunk: DocumentChunk?) {
        _selectedCitationChunk.value = chunk
    }

    fun startResearchWithQuery(query: String) {
        _selectedPlant.value = null
        _currentTab.value = AppNavTab.RESEARCH
        askQuestion(query)
    }

    // Evidence Graph & Botanical Canvas states
    private val _evidenceGraphData = MutableStateFlow(EvidenceGraphBuilder.buildGraph())
    val evidenceGraphData: StateFlow<EvidenceGraphData> = _evidenceGraphData.asStateFlow()

    private val _selectedGraphNode = MutableStateFlow<GraphNode?>(null)
    val selectedGraphNode: StateFlow<GraphNode?> = _selectedGraphNode.asStateFlow()

    private val _graphFilterType = MutableStateFlow<GraphNodeType?>(null)
    val graphFilterType: StateFlow<GraphNodeType?> = _graphFilterType.asStateFlow()

    private val _graphFilterEvidence = MutableStateFlow<EvidenceLevel?>(null)
    val graphFilterEvidence: StateFlow<EvidenceLevel?> = _graphFilterEvidence.asStateFlow()

    fun selectGraphNode(node: GraphNode?) {
        _selectedGraphNode.value = node
    }

    fun setGraphFilterType(type: GraphNodeType?) {
        _graphFilterType.value = type
    }

    fun setGraphFilterEvidence(level: EvidenceLevel?) {
        _graphFilterEvidence.value = level
    }

    fun navigateToGraphWithPlant(plantId: String) {
        val node = _evidenceGraphData.value.nodes.firstOrNull { it.associatedPlantId == plantId }
        _selectedGraphNode.value = node
        _selectedPlant.value = null
        _currentTab.value = AppNavTab.EVIDENCE_GRAPH
    }

    fun askQuestion(questionText: String) {
        val trimmed = questionText.trim()
        if (trimmed.isBlank() || _isAiThinking.value) return

        val currentMode = _researchMode.value
        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = ChatSender.USER,
            text = trimmed,
            timestamp = System.currentTimeMillis(),
            researchMode = currentMode
        )

        _chatMessages.value = _chatMessages.value + userMessage
        _isAiThinking.value = true

        viewModelScope.launch {
            try {
                val assistantResponse = repository.answerResearchQuestion(
                    question = trimmed,
                    mode = currentMode,
                    activeSourceIds = _activeSourceIds.value
                )
                _chatMessages.value = _chatMessages.value + assistantResponse
            } catch (e: Exception) {
                val errorResponse = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = ChatSender.ASSISTANT,
                    text = "Encountered an error evaluating the inquiry: ${e.localizedMessage ?: "Unknown error"}. Please check your connection or query phrasing.",
                    timestamp = System.currentTimeMillis(),
                    evidenceStatus = EvidenceLevel.INSUFFICIENT_OR_CONFLICTING,
                    researchMode = currentMode
                )
                _chatMessages.value = _chatMessages.value + errorResponse
            } finally {
                _isAiThinking.value = false
            }
        }
    }

    fun toggleBookmark(
        targetId: String,
        type: String,
        title: String,
        subtitle: String,
        snippet: String,
        evidence: EvidenceLevel?
    ) {
        viewModelScope.launch {
            val bookmark = SavedBookmark(
                id = "BM-" + UUID.randomUUID().toString().take(8),
                targetId = targetId,
                itemType = type,
                title = title,
                subtitle = subtitle,
                snippet = snippet,
                evidenceLevel = evidence
            )
            repository.toggleBookmark(bookmark)
        }
    }

    fun deleteBookmark(id: String) {
        viewModelScope.launch {
            repository.deleteBookmark(id)
        }
    }

    fun copyTextToClipboard(context: Context, text: String, toastMessage: String = "Research text copied to clipboard") {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Green AI Research", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {}
    }

    // Ingestion form state updates
    fun updateIngestionField(update: (IngestionFormState) -> IngestionFormState) {
        _ingestionForm.value = update(_ingestionForm.value)
    }

    fun submitIngestion() {
        val form = _ingestionForm.value
        if (form.scientificName.isBlank() || form.commonNames.isBlank()) {
            _ingestionForm.value = form.copy(errorMessage = "Scientific name and common names are required.")
            return
        }

        viewModelScope.launch {
            _ingestionForm.value = form.copy(isSubmitting = true, errorMessage = null, successMessage = null)
            try {
                val plantId = "CUSTOM-" + UUID.randomUUID().toString().take(6)
                val newPlant = MedicinalPlant(
                    id = plantId,
                    scientificName = form.scientificName.trim(),
                    commonNamesList = form.commonNames.trim(),
                    family = form.family.ifBlank { "Unspecified" }.trim(),
                    localNamesJson = if (form.localNames.isNotBlank()) "{\"Documented\": \"${form.localNames.trim()}\"}" else "{}",
                    plantPartsList = form.plantParts.ifBlank { "Unspecified" }.trim(),
                    geographicalContext = "User contributed record for verified African Traditional Medicine research.",
                    traditionalUsesList = form.traditionalUses.ifBlank { "Not documented" }.trim(),
                    preparationMethod = form.preparation.ifBlank { "Not documented" }.trim(),
                    phytochemicalsList = form.phytochemicals.ifBlank { "Under investigation" }.trim(),
                    laboratoryEvidence = form.laboratoryEvidence.ifBlank { "No laboratory data entered." }.trim(),
                    clinicalEvidence = form.clinicalEvidence.ifBlank { "No human clinical trials registered." }.trim(),
                    safetyInformation = form.safetyInformation.ifBlank { "Safety data not yet documented; exercise caution." }.trim(),
                    limitations = form.limitations.ifBlank { "Preliminary contributed monograph; pending peer review." }.trim(),
                    referencesJson = "[]",
                    reviewStatus = form.evidenceTier,
                    isUserCreated = true
                )

                val chunkList = mutableListOf<DocumentChunk>()
                if (form.excerptChunkText.isNotBlank()) {
                    chunkList.add(
                        DocumentChunk(
                            id = "CHUNK-" + UUID.randomUUID().toString().take(6),
                            plantId = plantId,
                            plantScientificName = form.scientificName.trim(),
                            sourceId = "REF-" + UUID.randomUUID().toString().take(6),
                            sourceTitle = form.sourceTitle.ifBlank { "Contributed Monograph on ${form.scientificName}" },
                            authors = form.sourceAuthor.ifBlank { "Project Contributor" },
                            year = form.sourceYear.toIntOrNull() ?: 2024,
                            citation = "${form.sourceAuthor.ifBlank { "Contributor" }} (${form.sourceYear}), ${form.sourceJournal.ifBlank { "Monograph" }}",
                            section = "Contributed Research Excerpt",
                            text = form.excerptChunkText.trim(),
                            evidenceTier = form.evidenceTier,
                            keywords = "${form.scientificName}, ${form.commonNames}, ${form.phytochemicals}"
                        )
                    )
                }

                repository.insertCustomPlant(newPlant, chunkList)
                _ingestionForm.value = IngestionFormState(
                    successMessage = "Successfully contributed '${form.scientificName}' into the Green AI knowledge repository!"
                )
            } catch (e: Exception) {
                _ingestionForm.value = form.copy(
                    isSubmitting = false,
                    errorMessage = "Failed to submit record: ${e.localizedMessage}"
                )
            }
        }
    }

    // --- Plant Scanner Operations ---

    fun setScannerOrgan(organ: PlantOrgan) {
        _selectedOrgan.value = organ
    }

    fun addScannedPhoto(bitmap: Bitmap, organ: PlantOrgan = _selectedOrgan.value) {
        if (_scannedPhotos.value.size < 5) {
            _scannedPhotos.value = _scannedPhotos.value + (bitmap to organ)
        }
    }

    fun removeScannedPhoto(index: Int) {
        val current = _scannedPhotos.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _scannedPhotos.value = current
        }
    }

    fun updatePhotoOrgan(index: Int, organ: PlantOrgan) {
        val current = _scannedPhotos.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].first to organ
            _scannedPhotos.value = current
        }
    }

    fun clearScanner() {
        _scannedPhotos.value = emptyList()
        _scannerStatus.value = ScanServiceStatus.Idle
    }

    fun submitPlantForIdentification() {
        val photos = _scannedPhotos.value
        if (photos.isEmpty()) return

        viewModelScope.launch {
            _scannerStatus.value = ScanServiceStatus.Analyzing("Initiating botanical identification pipeline...")
            val result = plantScannerService.identifyPlant(
                photos = photos,
                onProgress = { stepMsg ->
                    _scannerStatus.value = ScanServiceStatus.Analyzing(stepMsg)
                }
            )
            _scannerStatus.value = result
        }
    }

    fun crossCheckScientificName(scientificName: String) {
        val query = scientificName.trim()
        if (query.isBlank()) return

        viewModelScope.launch {
            _scannerStatus.value = ScanServiceStatus.Analyzing("Cross-checking '$query' against Kew Gardens POWO and GBIF...")
            val ref = plantScannerService.crossCheckTaxonomy(query)
            if (ref != null) {
                val mockResult = PlantScanResult(
                    id = UUID.randomUUID().toString(),
                    primaryScientificName = ref.canonicalName,
                    authorship = ref.matchedScientificName.removePrefix(ref.canonicalName).trim().takeIf { it.isNotBlank() },
                    family = ref.family.ifBlank { "Verified Botanical Taxon" },
                    genus = ref.genus.ifBlank { ref.canonicalName.split(" ").firstOrNull() ?: "" },
                    commonNames = emptyList(),
                    observedOrgans = emptyList(),
                    identificationService = "Authoritative Botanical Registry (GBIF / Kew POWO)",
                    confidenceScore = ref.confidence.toDouble() / 100.0,
                    confidenceDocumentation = "Taxonomic cross-reference confidence: ${ref.confidence}%. Validated against international botanical nomenclature index.",
                    primaryLabel = IdentificationLabel.CROSS_REFERENCE_FOUND,
                    morphologicalObservations = "Taxonomic record retrieved directly from international botanical herbarium index.",
                    taxonomicReference = ref,
                    alternatives = emptyList(),
                    curatedMonographMatch = CuratedKnowledgeBase.curatedPlants.firstOrNull {
                        it.scientificName.contains(ref.canonicalName, ignoreCase = true) ||
                                ref.canonicalName.contains(it.scientificName, ignoreCase = true)
                    }
                )
                _scannerStatus.value = ScanServiceStatus.Success(mockResult)
            } else {
                _scannerStatus.value = ScanServiceStatus.Error(
                    message = "Could not find an authoritative botanical record for '$query' in Kew POWO or GBIF. Verify spelling or rank."
                )
            }
        }
    }


    class Factory(
        private val repository: PlantRepository,
        private val context: Context? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GreenAiViewModel::class.java)) {
                return GreenAiViewModel(repository, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
