# Green AI 1.0 — Architecture and Functionality Specification Document
**Platform:** Green AI (Research Edition 1.0)  
**Engineering Team:** Team Message ATM (MeNI — Message Nexus International)  
**Target Environment:** Android (Jetpack Compose, Kotlin DSL, Room SQLite, OkHttp, Gemini 3.5 Flash, Pl@ntNet API, Kew POWO / GBIF)  
**Document Revision:** 1.0.0  

---

## 1. Executive Summary & Purpose

**Green AI** is an evidence-aware African Traditional Medicine (ATM) research, ethnobotanical documentation, and botanical vision platform. 

### Core Purpose
Africa is home to tens of thousands of medicinal plant species and rich traditional healing knowledge cultivated across generations. However, this knowledge is frequently fragmented, undocumented, or unverified against modern pharmacognosy. Conversely, general-purpose conversational AI models frequently hallucinate clinical trials, fabricated authors, non-existent chemical constituents, and misleading safety claims.

Green AI addresses this gap through:
1. **Zero-Hallucination Retrieval-Augmented Grounding (RAG)**: Answers are constrained strictly to indexed scientific passages, peer-reviewed monographs, and documented ethnobotanical records.
2. **Standardized Evidence Integrity Hierarchy**: Clear differentiation between *Traditional Use Reported*, *Laboratory Evidence*, *Animal Research*, *Human Research Available*, and *Insufficient or Conflicting Evidence*.
3. **Multi-Organ AI Plant Scanner**: Camera- and gallery-based botanical identification using a dual-engine architecture (Pl@ntNet Botanical API + Gemini 3.5 Flash Multimodal Vision) coupled with taxonomic cross-referencing against Kew Gardens Plants of the World Online (POWO) and the GBIF Backbone Taxonomy.
4. **Offline Resilience & Local Knowledge Persistence**: Full local SQLite database via Room preserving curated monographs, document chunks, and saved user bookmarks even without internet connectivity.
5. **High-Contrast Dual-Theme Visual System**: Fully accessible Light and Dark mode engine featuring Green AI's Deep Teal (`#0B2E34`) and Lime (`#ABFE3E`) brand palette with structured Markdown rendering.

---

## 2. High-Level System Architecture

The application adopts the Android **MVVM (Model-View-ViewModel)** and **Clean Architecture** patterns:

```
┌────────────────────────────────────────────────────────────────────────┐
│                          PRESENTATION LAYER                            │
│  MainActivity (Single-Activity Navigation, Edge-to-Edge, Drawer, Bars) │
│  Jetpack Compose Screens:                                              │
│   ├── HomeScreen (Dashboard, Search, Spotlight, Integrity Warning)     │
│   ├── ResearchScreen (Grounded AI Chat, Citation Chips, Inspector)    │
│   ├── PlantScannerScreen (Multi-Organ Vision, POWO/GBIF Verification)  │
│   ├── PlantLibraryScreen (Search, Evidence Filter, Monograph Cards)    │
│   ├── PlantDetailScreen (Phytochemicals, Regional Names, Evidence)    │
│   ├── EvidenceSourcesScreen (Cochrane/PubMed Bibliography & DOIs)      │
│   ├── IngestionScreen (Community / Researcher Record Contribution)     │
│   ├── SavedResearchScreen (Bookmarked Plants & Research Syntheses)     │
│   └── AboutScreen (Mission, RAG Architecture, MeNI Team)               │
│                                                                        │
│  UI Subsystems:                                                        │
│   ├── AiMarkdownResponseView (High-Contrast Markdown Renderer)         │
│   └── Theme & Tokens (LightGreenAiTokens, DarkGreenAiTokens)           │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ UI StateFlow & User Events
┌───────────────────────────────────▼────────────────────────────────────┐
│                            VIEWMODEL LAYER                             │
│  GreenAiViewModel:                                                     │
│   ├── Centralized UI State (Tab, Search, Filters, Messages, Photos)   │
│   ├── Coroutine Dispatchers & ViewModelScope Execution                 │
│   ├── Theme Mode Persistence (Light / Dark / System SharedPreferences) │
│   └── Factory Injection (Application Context + PlantRepository)        │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Repositories & Domain Engine
┌───────────────────────────────────▼────────────────────────────────────┐
│                        DOMAIN & RETRIEVAL LAYER                        │
│  RetrievalEngine:                                                      │
│   ├── Query Normalization, Stopword Filtering & Tokenization           │
│   ├── Multi-Tier Scoring (Plant Name: 10-12, Intent/Safety: 15, Kwd: 3)│
│   ├── Evidence Level Determination (Human > Animal > Lab > Traditional)│
│   └── Safety Warning Extraction (Pregnancy, Toxicity, Contraindication)│
└──────────────────┬─────────────────────────────────┬───────────────────┘
                   │ Local Persistence               │ Remote Services
┌──────────────────▼─────────────┐ ┌─────────────────▼───────────────────┐
│          DATA LAYER:           │ │            DATA LAYER:              │
│       LOCAL PERSISTENCE        │ │       REMOTE & BOTANICAL APIS       │
│                                │ │                                     │
│  Room Database (SQLite):       │ │  GeminiService:                     │
│   ├── PlantDao                 │ │   ├── Gemini 3.5 Flash              │
│   ├── ChunkDao                 │ │   ├── System Prompt Grounding       │
│   └── BookmarkDao              │ │   └── Fallback Synthesizer          │
│                                │ │                                     │
│  CuratedKnowledgeBase:         │ │  PlantScannerService:               │
│   ├── 5 Benchmark Monographs   │ │   ├── Pl@ntNet Form-Data Engine     │
│   ├── 30 Document Chunks       │ │   ├── Gemini Multimodal Vision      │
│   └── Peer-Reviewed References │ │   ├── GBIF Taxonomic Match API      │
│                                │ │   └── Kew POWO / WFO Deep Links     │
└────────────────────────────────┘ └─────────────────────────────────────┘
```

---

## 3. Detailed Component Breakdown

### 3.1. Data Models (`Models.kt`)

1. **`EvidenceLevel` (Enum)**:
   - `TRADITIONAL_USE_REPORTED`: Historical and ethnobotanical community preparations.
   - `LABORATORY_EVIDENCE`: In vitro cellular, microbial, and phytochemical assays.
   - `ANIMAL_RESEARCH`: In vivo physiological, metabolic, or rodent models.
   - `HUMAN_RESEARCH_AVAILABLE`: Controlled trials or systematic clinical reviews.
   - `INSUFFICIENT_OR_CONFLICTING`: Inconclusive, unverified, or contradictory reports.

2. **`MedicinalPlant` (Room Entity: `medicinal_plants`)**:
   - Primary key: `id` (e.g. `PLANT-001`).
   - Fields: `scientificName`, `commonNamesList`, `family`, `localNamesJson`, `plantPartsList`, `geographicalContext`, `traditionalUsesList`, `preparationMethod`, `phytochemicalsList`, `laboratoryEvidence`, `clinicalEvidence`, `safetyInformation`, `limitations`, `referencesJson`, `reviewStatus`, `isUserCreated`, `imageDrawableName`.

3. **`DocumentChunk` (Room Entity: `document_chunks`)**:
   - Discrete, addressable evidentiary passages.
   - Fields: `id`, `plantId`, `plantScientificName`, `sourceId`, `sourceTitle`, `authors`, `year`, `citation`, `section`, `text`, `evidenceTier`, `keywords`.

4. **`SavedBookmark` (Room Entity: `saved_bookmarks`)**:
   - Offline bookmarks for either plant monographs or synthesized research answers.

5. **`ChatMessage`**:
   - Represents dialogue state in the AI Research Workspace.
   - Contains: `id`, `sender` (`USER`/`ASSISTANT`), `text`, `timestamp`, `retrievedChunks`, `evidenceStatus`, `safetyWarning`, `isEvidenceGrounded`, `externalNotice`, `isBookmarked`.

6. **Botanical Identification Models**:
   - `PlantOrgan`: `LEAF`, `FLOWER`, `FRUIT`, `BARK`, `HABIT`.
   - `IdentificationLabel`: `IDENTIFICATION_SUGGESTED`, `CROSS_REFERENCE_FOUND`, `UNCERTAIN_IDENTIFICATION`.
   - `TaxonomicCrossReference`: Canonical name, taxonomic status (`ACCEPTED`, `SYNONYM`), family, genus, source name, Kew POWO / WFO / GBIF URLs.
   - `AlternativeSpeciesCandidate`: Lookalikes and morphological alternatives with match percentages.
   - `PlantScanResult`: Comprehensive result bundle including organ list, service name, confidence index, documentation, morphological description, cross-reference, alternatives, and prominent medical disclaimers.

---

### 3.2. Knowledge Base & Domain Retrieval Engine (`RetrievalEngine.kt`)

The Retrieval Engine operates on local or remote curated document chunks:
1. **Tokenization & Stopword Removal**: Cleans punctuation, removes 40+ grammatical stopwords, and splits into lowercase tokens.
2. **Heuristic Scoring Model**:
   - Direct scientific/common plant name match: **+10.0 to +12.0 points**.
   - Safety/Contraindication query intent matching toxicity sections: **+15.0 points**.
   - Phytochemical query intent matching chemical isolation sections: **+10.0 points**.
   - Traditional use intent matching preparation sections: **+8.0 points**.
   - Clinical trial intent matching human pharmacology sections: **+8.0 points**.
   - Keyword, title, and body token hits: **+1.5 to +3.0 points** each.
3. **Threshold Filtering**: Only chunks scoring **≥ 4.0** are retained (up to a maximum of 4 chunks). If no chunks pass the threshold, the system triggers the zero-hallucination fallback.
4. **Evidence Tier Aggregation**: Evaluates the retrieved set and assigns the highest confirmed tier (`HUMAN` > `ANIMAL` > `LAB` > `TRADITIONAL`).
5. **Safety Extraction**: Scans retrieved passages for contraindications (e.g. pregnancy, neurotoxic thujone, lethal cytotoxins) and extracts high-priority warning strings.

---

### 3.3. Grounded AI Generation (`GeminiService.kt`)

When a user asks a research question:
1. **API Mode (Live Gemini 3.5 Flash)**:
   - System instruction strictly prohibits inventing authors, citations, dosages, or claims.
   - Forces structured citation tags (e.g. `[1]`, `[2]`) linked directly to the retrieved passages.
   - Instructs the model to output: *"I could not find sufficient supporting evidence in the available sources"* if the evidence corpus is insufficient.
2. **Local Fallback Synthesis Engine**:
   - If the API key is not yet configured or the device is offline, the service falls back to a deterministic, zero-hallucination local synthesizer.
   - Assembles an evidence summary directly from the retrieved Room passages with bracketed citations and explicit disclaimers.

---

### 3.4. AI Plant Scanner (`PlantScannerService.kt` & `PlantScannerScreen.kt`)

1. **Specimen Image Acquisition**:
   - **Camera Preview Capture**: Android system camera intent returning bitmap thumbnails with zero runtime permission friction.
   - **Modern Photo Picker**: Android zero-permission Photo Picker (`ActivityResultContracts.PickVisualMedia`) for high-resolution images.
   - **Organ Tagging**: Each photo is tagged with an anatomical organ (`Leaf`, `Flower`, `Fruit`, `Bark`, `Whole Plant`).
   - **Multi-Organ Stacking**: Users can upload up to 5 photos to enhance identification reliability.
2. **Dual-Engine Online Identification**:
   - **Engine A — Pl@ntNet Botanical API**: Sends multi-part form requests with image binaries and organ parameters.
   - **Engine B — Gemini 3.5 Flash Botanical Vision**: Formats images into base64 visual parts with botanical taxonomy prompts requesting JSON outputs.
3. **Taxonomic Verification (POWO & GBIF)**:
   - Queries the open GBIF Species API to verify whether the suggested scientific name is an accepted taxon.
   - Generates verified deep-links to:
     - Plants of the World Online (Kew Gardens)
     - World Flora Online (WFO Consortium)
     - Global Biodiversity Information Facility (GBIF)
4. **Scientific Honesty & Reliability**:
   - Only displays confidence percentages if provided and documented by the service.
   - Flags uncertainty when diagnostic features are ambiguous.
   - Lists plausible lookalikes and morphological alternatives.
   - Auto-checks for matches against Green AI's curated monographs.
   - Displays a mandatory, high-visibility medical safety warning against ingesting or preparing remedies based on automated suggestions.

---

### 3.5. UI/UX Architecture & High-Contrast Theme System

1. **Centralized Tokens (`Theme.kt` & `Color.kt`)**:
   - **Brand Colors**: Primary Deep Teal (`#0B2E34`), Accent Lime (`#ABFE3E`), Botanical Green (`#00DD88`).
   - **Light Mode**: Warm Botanical Ivory (`#F5F4ED`) surfaces, deep teal body text, and subtle neutral borders.
   - **Dark Mode**: Deep Teal Surface (`#0F363D`), elevated containers (`#13424B`), and crisp, high-contrast light text (`#F5F4ED`) with lime headings.
2. **Custom Markdown Renderer (`AiMarkdownResponseView.kt`)**:
   - Resolves all color inheritance conflicts.
   - Explicitly styles Headings (H1–H4), Paragraphs, Bullet Lists, Numbered Badges, Blockquotes, Monospace Code Blocks (with copy-to-clipboard), Scrollable Tables, and Underlined Citation Badges.
3. **Navigation & Controls (`MainActivity.kt`)**:
   - Drawer Navigation with 8 primary research destinations.
   - Bottom Navigation Bar for quick switching between Home, Research, Library, Evidence, and Saved.
   - Top App Bar featuring quick actions: Light/Dark Mode Toggle, Scanner Launcher, and AI Research Launcher.
   - Theme Mode Selector with Light, Dark, and System modes, persisted in `SharedPreferences`.

---

## 4. Curated Monograph Corpus

The built-in SQLite database seeds 5 peer-reviewed benchmark monographs:

| Plant ID | Scientific Name | Common / Regional Names | Family | Review Status | Primary Pharmacological Context |
|---|---|---|---|---|---|
| `PLANT-001` | *Artemisia afra Jacq. ex Willd.* | African Wormwood, Umhlonyane, Lengana | Asteraceae | Laboratory Evidence | Volatile thujone, cineole; respiratory inhalation & antimicrobial; pregnancy contraindication. |
| `PLANT-002` | *Moringa oleifera Lam.* | Moringa, Miracle Tree, Zogale, Ewe Igbale | Moringaceae | Human Research Available | Glucomoringin, quercetin; postprandial glycemic moderation; root/bark toxicity warning. |
| `PLANT-003` | *Vernonia amygdalina Delile* | Bitter Leaf, Ewuro, Onugbu, Shuwaka | Asteraceae | Animal Research | Vernodalin sesquiterpene lactones; antiplasmodial & chemopreventive; oxytocic uterine warning. |
| `PLANT-004` | *Harpagophytum procumbens (Burch.) DC. ex Meisn.* | Devil's Claw, Sengaparile, Duiwelsklou | Pedaliaceae | Human Research Available | Harpagoside iridoid glycosides; Cochrane-reviewed back pain & osteoarthritic relief; peptic ulcer caution. |
| `PLANT-005` | *Catharanthus roseus (L.) G.Don* | Madagascar Periwinkle, Vinca | Apocynaceae | Human Research Available | Vinblastine & vincristine; microtubule inhibition in leukemia/lymphoma; lethal toxicity when ingested raw. |

---

## 5. Security, Privacy & Integrity Protocols

1. **Zero Hallucination Protocol**: The system never generates unsourced medical claims or non-existent publications. If evidence is absent, the system explicitly states its limitations.
2. **Secret Management**: API keys (`GEMINI_API_KEY`, `PLANTNET_API_KEY`) are managed via Android `BuildConfig` and the AI Studio Secrets panel; no keys are hardcoded in source repositories.
3. **Zero-Permission Media Access**: Image selection uses the standard Android Photo Picker (`PickVisualMedia`) and Camera Thumbnail intent, avoiding broad external storage permissions (`READ_EXTERNAL_STORAGE`).
4. **Clinical Safety Safeguards**: Automated plant identification is accompanied by clear disclaimers that photographic suggestions do not prove phytochemical safety or medical efficacy.

---

## 6. NotebookLM-Style Local RAG & Dual-Mode Research Workspace

Added in version 1.0 to enable personal research material ingestion and dual-mode exploration:

1. **Document Ingestion (File Picker & Pasted Notes)**:
   - **Document File Picker**: Supports `.txt`, `.md`, `.csv`, `.json`, and text documents via `ActivityResultContracts.OpenDocument()`.
   - **Text Note Paste**: Direct in-app text modal allowing field researchers and ethnobotanists to enter observations, titles, and author attributions.
   - **Deterministic Document Chunker (`DocumentChunker.kt`)**: Automatically partitions ingested text into ~180-word overlapping chunks, extracts prominent keywords, and determines the evidence level.
   - **Local Persistence (`notebook_sources` & `document_chunks`)**: Uploaded materials are stored in Room SQLite and survive app restarts without needing cloud storage.

2. **Dynamic Source Management & Checkbox Scoping**:
   - Researchers can open **Manage Sources** to inspect all active materials.
   - Individual checkboxes permit filtering citations: users can scope queries strictly to specific uploaded field notes, papers, or the built-in African medicinal monographs (`CURATED_CORPUS`).

3. **Dual-Mode Research Architecture**:
   - **Mode A: Local Notebook RAG**: Inquiries are strictly grounded against active notebook materials and peer-reviewed African ethnobotanical monographs. Every assertion is accompanied by bracketed citations (`[1]`, `[2]`), evidence tier badges, and strict non-hallucination guardrails.
   - **Mode B: Direct Internet & Live AI**: Inquiries query Gemini 3.5 Flash directly for unconstrained open-ended pharmacognosy questions, international comparisons, and web knowledge, tagged with a prominent "Live Web & Global AI" badge.

234: ---
235: 
236: ## 7. Digital Botanical Research Environment Architecture
237: 
238: Green AI has evolved from a standard card-based app into a unified digital research environment fusing four interconnected pillars:
239: 
240: ```
241: ┌────────────────────────────────────────────────────────────────────────┐
242: │                     THE FOUR INTERCONNECTED PILLARS                    │
243: ├───────────────────┬───────────────────┬────────────────────────────────┤
244: │ 1. BOTANICAL ATLAS│ 2. PLANT SCANNER  │ 3. RESEARCH LAB                │
245: │  • Archival Folio │  • Dual-Engine    │  • Local NotebookLM RAG        │
246: │  • Monograph List │  • Multi-Organ    │  • Direct Internet AI          │
247: │  • Evidence Tier  │  • POWO / GBIF    │  • High-Contrast Markdown      │
248: ├───────────────────┴───────────────────┴────────────────────────────────┤
249: │ 4. BOTANICAL EVIDENCE GRAPH & TOPOLOGICAL CANVAS                       │
250: │  • Interactive vector graph of Plants, Phytochemicals, Indications,    │
251: │    and Clinical Trials.                                                │
252: │  • Node hit-testing, dynamic pulse rings, inspection sliding sheet.    │
253: │  • Relational Matrix view & deep navigation into Dossiers & Lab.       │
254: └────────────────────────────────────────────────────────────────────────┘
255: ```
256: 
257: ### 7.1. Pillar 1: The Botanical Atlas (`BotanicalAtlasScreen.kt`)
258: - **Editorial Design & Typography**: Incorporates archival serif headers (Cinzel & Playfair Display) reminiscent of historic natural history folios and modern botanical compendiums.
259: - **Spotlight Monograph**: Highlights an active medicinal taxon with its Latin authority, ethnobotanical context, and Evidence Spectrum rating.
260: - **Interactive Taxonomy Filter & Evidence Spectrum Strip**: Instant live filtering across botanical families (Asteraceae, Moringaceae, Pedaliaceae, Apocynaceae) and evidentiary levels.
261: - **Herbarium Specimen Cards**: Deep-dive entry points leading directly into the scientific dossier or relational evidence graph.
262: 
263: ### 7.2. Scientific Dossier & Monograph Folio (`PlantDossierScreen.kt`)
264: - **Evidence Spectrum 5-Tier Gauge**: Interactive visual gauge distinguishing *Traditional Reports*, *Laboratory Assays*, *Animal Studies*, *Human Clinical Research*, and *Insufficient/Conflicting Evidence*. Tapping any tier displays an explanatory scientific breakdown.
265: - **Comparative Analytical Table**: Side-by-side contrast between traditional ethnobotanical preparations (aqueous decoctions, steeping, hot infusions) and modern pharmacological mechanisms (in vitro kinase inhibition, receptor binding).
266: - **Phytochemical Laboratory Panel**: Detailed chemical classification, active secondary metabolites (thujone, luteolin, glucomoringin, harpagoside, vincristine), and molecular bioactivities.
267: - **Clinical Safety & Contraindications**: Elevated alert panel detailing known toxicities, pregnancy contraindications, and organ limitations.
268: - **Direct Relational Links**: Contextual jump buttons to inspect the plant in the Evidence Graph or interrogate it in the Research Lab.
269: 
270: ### 7.3. Pillar 2: Optical Botanical Scanner (`PlantScannerScreen.kt`)
271: - **High-Precision Specimen Imaging**: Up to 5 multi-organ photographs (Leaf, Flower, Fruit, Bark, Habit).
272: - **Dual-Engine Identification**: Hybrid Pl@ntNet Form API + Gemini 3.5 Flash Vision.
273: - **Taxonomic Cross-Reference**: Automated Kew POWO and GBIF verification with verified status badges and external portal deep-links.
274: 
275: ### 7.4. Pillar 3: AI Pharmacognosy Lab (`ResearchScreen.kt`)
276: - **Dual Workspace**: Smooth toggle between Local NotebookLM RAG (offline, citation-grounded, zero hallucination) and Direct Internet AI (live web exploration).
277: - **Document Ingestion Console**: Open `.txt`, `.md`, `.json` research documents or paste custom field notes.
278: - **High-Contrast Readability**: Engineered Markdown rendering with zero contrast drop in both Light and Dark themes.
279: 
280: ### 7.5. Pillar 4: Interactive Botanical Evidence Graph (`EvidenceGraphScreen.kt` & `EvidenceGraphBuilder.kt`)
281: - **Interactive Canvas**: Custom vector topological graph with physics-inspired positioning connecting 5 medicinal plants, 7 phytochemical constituents, 6 therapeutic indications, and 3 clinical landmark studies.
282: - **Gesture & Hit-Testing**: Touch-to-select nodes with responsive animated halo pulses and glowing connecting edges.
283: - **Multi-Entity Filtering**: Filter pills for Plants, Phytochemicals, Indications, and Clinical Studies, plus Evidence Tier filters.
284: - **Relational Matrix View**: Tabular cross-connection view mapping every active relationship, interaction strength, and evidence tier.
285: - **Sliding Node Inspector**: Deep-inspection sheet revealing connected entities, pharmacological notes, and one-tap actions to open the scientific dossier or open a targeted query in the Research Lab.
