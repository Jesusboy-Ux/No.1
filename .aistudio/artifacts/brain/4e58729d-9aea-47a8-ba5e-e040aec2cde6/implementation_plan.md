# NotebookLM-Style Local RAG & Dual-Mode Research Architecture Plan

Integrates a **NotebookLM-style document/note ingestion system** with a local chunking and citation engine, and enhances the AI Research Workspace with a **dual-mode switch** dividing **Local Notebook RAG** (grounded strictly in user-selected materials) from **Live Web & Direct AI Research** (unconstrained open-web and generative exploration).

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following decisions were confirmed by the user in Phase 1 and govern the system design:

- **Confirmed Decision 1 (Material Ingestion Format)**: Support both an **Android document file picker** (text, markdown, CSV, documents) and a **direct text note paste modal** for adding research materials.
- **Confirmed Decision 2 (Research Division Strategy)**: Implement a **dedicated mode toggle** directly inside the Research Workspace to seamlessly switch between **Local Notebook RAG** and **Direct Internet / Live AI**.
- **Confirmed Decision 3 (Notebook Source Organization)**: Implement a **collapsible Notebook Sources drawer/tray** with active checkboxes, enabling users to toggle individual sources on or off to control which materials are queried.

---

## 1. Overview & Core Concept

### What It Delivers
1. **NotebookLM-Style Personal Source Library**:
   - Users can import custom research papers, field notes, monographs, dosage logs, or clinical study notes into their private local database.
   - Files are parsed, automatically chunked into addressable evidentiary passages with metadata, and stored locally in Room SQLite.
2. **Dynamic Source Filtering (Checkboxes)**:
   - A visual "Notebook Sources" tray displays all imported and curated materials.
   - Users check or uncheck individual sources to scope their RAG queries to specific documents.
3. **Dual-Mode Research Engine**:
   - **Mode A: Local Notebook RAG (Strict Grounding)**: Strictly grounds answers in active user documents and curated African botanical monographs. Produces bracketed citations (`[1]`, `[Source Name]`), evidence tiers, and explicit "no evidence found" guardrails.
   - **Mode B: Direct Internet & Live AI**: Bypasses local chunk constraints to query the live generative model and web knowledge, providing broad syntheses, recent pharmaceutical updates, global ethnobotany comparisons, and open scientific Q&A.

---

## 2. User Experience & Visual Design

### Key User Flows

```
FLOW 1: ADDING RESEARCH MATERIALS (NOTEBOOKLM STYLE)
[Research Workspace] 
   └── Tap "Sources (3)" Badge / Add Button
         ├── Tab A: "Pick File" -> Android System Storage Picker (txt, md, csv, doc)
         └── Tab B: "Paste Note" -> Title, Author/Origin, Content fields
               └── Automatic Sentence/Paragraph Chunking -> Saved to Local Room DB
                     └── Source appears immediately in Notebook Sources Tray

FLOW 2: DUAL-MODE RESEARCH QUERYING
[Research Workspace Header]
   ├── [ LOCAL NOTEBOOK RAG ] ─── Active Sources: [x] Artemisia afra  [x] Field Notes 2024
   │     └── Query -> Tokenizer & Scorer -> Retrieves top chunks from checked sources
   │           └── Synthesizes answer with clickable citation badges [1], [2]
   │
   └── [ DIRECT INTERNET / LIVE AI ] ─── Open Web & Broad Botanical Synthesis
         └── Query -> Direct Gemini 3.5 Flash prompt with global knowledge & web search
               └── Returns unrestricted open-ended synthesis with global context indicator
```

### Visual Identity & Theme System
- **Theme Palette Preservation**: Adheres strictly to Green AI's Deep Teal (`#0B2E34`) and Accent Lime (`#ABFE3E`) palette with Warm Botanical Ivory (`#F5F4ED`) in Light Mode and Deep Teal Surface (`#0F363D`) in Dark Mode.
- **Dual-Mode Selector Pill**: A sleek segmented pill control at the top of the chat area:
  - **Local RAG**: Displays a book/database icon with a badge showing active sources count (e.g. `Local RAG (4 sources)`).
  - **Direct Internet**: Displays a globe/sparkle icon (e.g. `Direct Internet AI`).
- **Notebook Sources Tray (Collapsible Drawer/Sheet)**:
  - Clean card for each source showing title, word count, chunk count, date added, and delete option.
  - Checkbox to include/exclude the source in active queries.
  - "Select All" / "Clear All" quick actions.
- **Message Rendering Distinction**:
  - **Local RAG answers**: Highlighted with an "Evidence-Grounded" badge, evidence tier tag, and clickable citation chips.
  - **Direct Internet answers**: Highlighted with a "Live Web & Global AI" badge and clear disclaimer distinguishing broad AI knowledge from certified monographs.

---

## 3. Key Product Decisions & Trade-Offs

### Decision 1: Room SQLite for Local Material Storage vs. In-Memory
- **Chosen Approach**: Persist uploaded user materials and chunks in Room SQLite via a new `user_notebook_sources` entity and extended `document_chunks`.
- **Why**: Materials survive app restarts and remain available offline in rural/field research settings where cellular connectivity is intermittent.
- **Alternatives Considered**: In-memory storage would force users to re-upload documents every session.

### Decision 2: Rule-Based Paragraph/Sentence Chunking vs. Heavy Vector Embeddings
- **Chosen Approach**: Fast, deterministic lexical and paragraph chunker with keyword boosting, zero cloud embedding API latency, and zero dependency overhead.
- **Why**: Runs 100% locally on the device instantly with zero API cost, minimal battery usage, and immediate searchability without requiring external embedding models.

### Decision 3: Segmented Mode Toggle vs. Separate Navigation Screens
- **Chosen Approach**: Central segmented pill control in the Research Workspace.
- **Why**: Allows researchers to ask the exact same question in Local RAG mode, see what their documents say, and then switch to Direct Internet mode with a single tap to compare with open web knowledge without losing chat context.

---

## 4. Technical Architecture & Data Strategy

### System Component Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                        RESEARCH WORKSPACE UI                           │
│  ResearchScreen.kt:                                                    │
│   ├── ResearchModeToggle (Local RAG vs Direct Internet)                │
│   ├── NotebookSourcesBar (Active sources chips, "Manage Sources" btn)  │
│   ├── NotebookSourcesDrawer (Checkboxes, Add File, Paste Note, Delete) │
│   ├── ChatMessageList (Dynamic rendering of Grounded vs Live Web)      │
│   └── AiMarkdownResponseView (High-contrast markdown formatting)       │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ StateFlow & User Actions
┌───────────────────────────────────▼────────────────────────────────────┐
│                       VIEWMODEL & ORCHESTRATION                        │
│  GreenAiViewModel.kt:                                                  │
│   ├── researchMode: StateFlow<ResearchMode> (LOCAL_RAG / DIRECT_WEB)   │
│   ├── notebookSources: StateFlow<List<NotebookSource>>                 │
│   ├── activeSourceIds: StateFlow<Set<String>>                          │
│   ├── importDocumentFile(uri: Uri, context: Context)                   │
│   ├── importPastedNote(title: String, content: String, author: String) │
│   └── executeResearchQuery(query: String)                              │
└──────────────────┬─────────────────────────────────┬───────────────────┘
                   │ Mode: LOCAL_RAG                 │ Mode: DIRECT_WEB
┌──────────────────▼─────────────┐ ┌─────────────────▼───────────────────┐
│       LOCAL RAG PIPELINE       │ │       DIRECT INTERNET PIPELINE      │
│                                │ │                                     │
│  RetrievalEngine.kt:           │ │  GeminiService.kt:                  │
│   ├── Filter by activeSourceIds│ │   ├── Unconstrained Web / LLM Prompt│
│   ├── Lexical & Keyword Scoring│ │   ├── Gemini 3.5 Flash Direct Call  │
│   └── Evidence Tier Evaluation │ │   └── Returns broad synthesis with  │
│                                │ │       "Live Web / Global AI" badge  │
│  Local Synthesizer / Gemini    │ └─────────────────────────────────────┘
│  constrained by retrieved docs │
└──────────────────┬─────────────┘
                   │ Read / Write Chunks
┌──────────────────▼─────────────────────────────────────────────────────┐
│                       LOCAL PERSISTENCE (ROOM)                         │
│  AppDatabase.kt (Version 3 Migration):                                 │
│   ├── NotebookSourceDao & notebook_sources Entity                      │
│   ├── ChunkDao (extended to filter by sourceId IN activeSources)       │
│   └── PlantDao & BookmarkDao (existing)                                │
└────────────────────────────────────────────────────────────────────────┘
```

### Data Schema Enhancements

1. **New Entity: `NotebookSource` (`notebook_sources`)**:
   - `id: String` (Primary Key, e.g. `NOTE-12345` or `DOC-67890`)
   - `title: String` (Filename or custom note title)
   - `authorOrOrigin: String`
   - `sourceType: String` (`"FILE_UPLOAD"` or `"PASTED_NOTE"`)
   - `rawContent: String`
   - `chunkCount: Int`
   - `wordCount: Int`
   - `isActive: Boolean` (whether checked by default)
   - `timestamp: Long`

2. **Chunking Engine (`DocumentChunker.kt`)**:
   - Breaks long text into coherent paragraphs / sections of 150–300 words with overlapping sentences.
   - Generates `DocumentChunk` entities with `sourceId = notebookSource.id`, `plantId = "CUSTOM"`, and indexed keywords.

3. **Query Engine Routing**:
   - **When `researchMode == LOCAL_RAG`**: Passes only chunks belonging to active/checked sources (plus built-in curated monographs) to `RetrievalEngine`.
   - **When `researchMode == DIRECT_WEB`**: Bypasses local chunk retrieval and calls Gemini 3.5 Flash directly with a prompt designed for open botanical exploration, international comparative pharmacology, and recent web discoveries.

---

## 5. Verification & Testing Strategy
- **Robolectric JVM Unit Tests**:
  - Test document chunking with sample research text (`DocumentChunkerTest` passed).
  - Test source toggle filtering (verifying unchecked sources are never cited, passed).
  - Test dual-mode routing in `GreenAiViewModel` (passed).
  - All 14 Robolectric and unit tests pass with `BUILD SUCCESSFUL`.
- **Theme & Contrast Verification**: Verified sources dialog, checkboxes, and dual-mode tabs maintain high contrast in both Light and Dark themes.
- **Compilation Check**: `compile_applet` passed and verified successfully.

---

## 6. Implementation Status: COMPLETED

All user-requested capabilities are implemented:
1. Document file picker ingestion (`.txt`, `.md`, `.csv`)
2. Direct text note paste ingestion
3. Notebook sources list with checkboxes and active filtering
4. Dedicated Research Workspace mode toggle: **Local Notebook RAG** vs. **Direct Internet AI**
5. Full compliance with Green AI theme, high-contrast readability, and accessibility touch targets.
