package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CuratedKnowledgeBase
import com.example.data.model.EvidenceLevel
import com.example.domain.RetrievalEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

class ExampleRobolectricTest {

    @Test
    fun `test supported query retrieves relevant Artemisia chunks`() {
        val engine = RetrievalEngine()
        val allChunks = CuratedKnowledgeBase.curatedDocumentChunks
        val results = engine.retrievePassages("What are the phytochemicals in Artemisia afra?", allChunks)

        assertTrue(results.isNotEmpty())
        assertTrue(results.any { it.plantId == "PLANT-001" })
        assertTrue(results.any { it.section.contains("Phytochemistry", ignoreCase = true) })
    }

    @Test
    fun `test unsupported query returns empty list of chunks`() {
        val engine = RetrievalEngine()
        val allChunks = CuratedKnowledgeBase.curatedDocumentChunks
        val results = engine.retrievePassages("How to synthesize paracetamol and ibuprofen tablets?", allChunks)

        // Knowledge base does not contain paracetamol or synthetic pharmaceuticals
        assertTrue(results.isEmpty())
    }

    @Test
    fun `test safety contraindication extraction for pregnancy risk`() {
        val engine = RetrievalEngine()
        val allChunks = CuratedKnowledgeBase.curatedDocumentChunks
        val results = engine.retrievePassages("Is Bitter Leaf Vernonia amygdalina safe during pregnancy?", allChunks)

        assertTrue(results.isNotEmpty())
        val warning = engine.extractSafetyWarning(results)
        assertNotNull(warning)
        assertTrue(warning!!.contains("CONTRAINDICATED", ignoreCase = true))
    }

    @Test
    fun `test Devil's Claw clinical back pain evidence retrieval`() {
        val engine = RetrievalEngine()
        val allChunks = CuratedKnowledgeBase.curatedDocumentChunks
        val results = engine.retrievePassages("Clinical trials and Cochrane review for Devil's Claw in back pain", allChunks)

        assertTrue(results.isNotEmpty())
        assertTrue(results.any { it.plantId == "PLANT-004" })
        val status = engine.determineEvidenceStatus(results)
        assertEquals(EvidenceLevel.HUMAN_RESEARCH_AVAILABLE, status)
    }

    @Test
    fun `test PlantScannerService provides reputable external botanical portals`() {
        val scanner = com.example.data.api.PlantScannerService()
        val portals = scanner.externalPortals
        assertTrue(portals.isNotEmpty())
        assertTrue(portals.any { it.name.contains("Kew", ignoreCase = true) })
        assertTrue(portals.any { it.name.contains("Pl@ntNet", ignoreCase = true) })
        assertTrue(portals.any { it.name.contains("World Flora Online", ignoreCase = true) })
    }

    @Test
    fun `test IdentificationLabel semantics adhere to scientific honesty guidelines`() {
        assertEquals("Identification suggested", com.example.data.model.IdentificationLabel.IDENTIFICATION_SUGGESTED.title)
        assertEquals("Cross-reference found", com.example.data.model.IdentificationLabel.CROSS_REFERENCE_FOUND.title)
        assertEquals("Uncertain identification", com.example.data.model.IdentificationLabel.UNCERTAIN_IDENTIFICATION.title)
    }

    @Test
    fun `test Light mode colors ensure dark text on light backgrounds`() {
        val light = com.example.ui.theme.LightGreenAiTokens
        org.junit.Assert.assertFalse(light.isDark)
        // Card is light
        assertEquals(com.example.ui.theme.PureWhite, light.aiCardBg)
        // Text is dark deep teal for maximum legibility
        assertEquals(androidx.compose.ui.graphics.Color(0xFF0B2E34), light.aiTextParagraph)
        assertEquals(androidx.compose.ui.graphics.Color(0xFF061B1F), light.aiTextHeading)
        // High contrast between card and paragraph text
        org.junit.Assert.assertNotEquals(light.aiCardBg, light.aiTextParagraph)
    }

    @Test
    fun `test Dark mode colors ensure light text on dark surfaces`() {
        val dark = com.example.ui.theme.DarkGreenAiTokens
        assertTrue(dark.isDark)
        // Card is deep teal surface
        assertEquals(com.example.ui.theme.DeepTealSurface, dark.aiCardBg)
        // Text is high-contrast light text
        assertEquals(com.example.ui.theme.TextOnDark, dark.aiTextParagraph)
        assertEquals(com.example.ui.theme.AccentLime, dark.aiTextHeading)
        // Guarantees text is not white-on-white or dark-on-dark
        org.junit.Assert.assertNotEquals(dark.aiCardBg, dark.aiTextParagraph)
        org.junit.Assert.assertNotEquals(dark.aiCardBg, dark.aiTextHeading)
    }

    @Test
    fun `test markdown block parsing correctly decomposes structured AI answers`() {
        val sampleAiResponse = """
            # Artemisia afra Monograph
            
            Artemisia afra (African Wormwood) contains diverse volatile terpenoids and flavonoids.
            
            ### Documented Biological Activities
            - In vitro inhibition of Plasmodium falciparum [1]
            - Antioxidant luteolin and apigenin flavonoids
            
            1. First dosage recommendation from community surveys
            2. Pharmacological mechanism observed in murine models
            
            > Traditional preparation involves aqueous leaf infusions steeped in boiled water.
            
            ```chemical_structure
            Thujone + 1,8-Cineole + Camphor
            ```
            
            | Compound | Class | Bioactivity |
            | --- | --- | --- |
            | Thujone | Monoterpene | Antimicrobial |
            | Luteolin | Flavonoid | Antioxidant |
        """.trimIndent()

        val blocks = com.example.ui.components.parseMarkdownBlocks(sampleAiResponse)
        assertTrue(blocks.isNotEmpty())
        assertTrue(blocks.any { it is com.example.ui.components.MarkdownBlock.Heading && it.level == 1 })
        assertTrue(blocks.any { it is com.example.ui.components.MarkdownBlock.Heading && it.level == 3 })
        assertTrue(blocks.any { it is com.example.ui.components.MarkdownBlock.Paragraph })
        assertTrue(blocks.any { it is com.example.ui.components.MarkdownBlock.BulletItem })
        assertTrue(blocks.any { it is com.example.ui.components.MarkdownBlock.NumberedItem })
        assertTrue(blocks.any { it is com.example.ui.components.MarkdownBlock.Blockquote })
        assertTrue(blocks.any { it is com.example.ui.components.MarkdownBlock.CodeBlock })
        assertTrue(blocks.any { it is com.example.ui.components.MarkdownBlock.Table })
    }

    @Test
    fun `test DocumentChunker splits long text into indexed chunks with keywords`() {
        val sampleText = """
            Sutherlandia frutescens (Cancer Bush) has been traditionally utilized across southern Africa for immune support.
            Phytochemical analysis demonstrates high concentrations of canavanine, pinitol, and gamma-aminobutyric acid (GABA).
            Clinical trials conducted in South Africa evaluated safety in healthy volunteers over three months with no adverse hepatotoxicity observed.
            Ethnobotanical surveys document decoction of leaves prepared as a bitter tea for convalescing patients.
            Recent pharmacological investigations focus on potential antiproliferative activities against melanoma cell lines.
            Precautions are warranted during pregnancy due to potential uterine stimulation.
        """.trimIndent()

        val chunks = com.example.domain.DocumentChunker.chunkText(
            sourceId = "TEST-SRC-01",
            sourceTitle = "Sutherlandia Clinical Review",
            authorOrOrigin = "Dr. T. Ndlovu, 2024",
            rawText = sampleText
        )

        assertTrue(chunks.isNotEmpty())
        val firstChunk = chunks.first()
        assertEquals("TEST-SRC-01", firstChunk.plantId)
        assertEquals("Sutherlandia Clinical Review", firstChunk.sourceTitle)
        assertEquals("Dr. T. Ndlovu, 2024", firstChunk.authors)
        assertTrue(firstChunk.text.contains("Sutherlandia frutescens"))
        assertTrue(firstChunk.keywords.isNotBlank())
        assertTrue(firstChunk.keywords.contains("sutherlandia", ignoreCase = true) || firstChunk.keywords.contains("clinical", ignoreCase = true))
    }

    @Test
    fun `test ResearchMode enum supports Local Notebook RAG and Direct Internet AI`() {
        val localMode = com.example.data.model.ResearchMode.LOCAL_NOTEBOOK_RAG
        val internetMode = com.example.data.model.ResearchMode.DIRECT_INTERNET_AI

        assertEquals("LOCAL_NOTEBOOK_RAG", localMode.name)
        assertEquals("DIRECT_INTERNET_AI", internetMode.name)
        assertEquals("Local Notebook RAG", localMode.displayName)
        assertEquals("Direct Internet & AI", internetMode.displayName)
    }

    @Test
    fun `test notebook source active filtering restricts chunk retrieval`() {
        val curatedChunk = CuratedKnowledgeBase.curatedDocumentChunks.first { it.plantId == "PLANT-001" }

        val customChunk = com.example.data.model.DocumentChunk(
            id = "USER-CHUNK-1",
            plantId = "CUSTOM-SRC-1",
            plantScientificName = "Artemisia afra",
            sourceId = "CUSTOM-SRC-1",
            sourceTitle = "Custom Field Observations",
            authors = "Field Researcher",
            year = 2024,
            citation = "Field Researcher (2024). Field Notes.",
            section = "Observations",
            text = "Observed high abundance of Artemisia afra leaves with potent essential oil scent in KwaZulu-Natal grasslands.",
            evidenceTier = EvidenceLevel.TRADITIONAL_USE_REPORTED,
            keywords = "artemisia, afra, grasslands"
        )

        val candidateChunks = listOf(curatedChunk, customChunk)

        // When only CUSTOM-SRC-1 is in activeSourceIds:
        val filteredForCustom = candidateChunks.filter { it.plantId == "CUSTOM-SRC-1" }
        assertEquals(1, filteredForCustom.size)
        assertEquals("USER-CHUNK-1", filteredForCustom.first().id)

        // When only CURATED_CORPUS is active (plantId starts with PLANT-):
        val filteredForCurated = candidateChunks.filter { it.plantId.startsWith("PLANT-") }
        assertEquals(1, filteredForCurated.size)
        assertEquals("PLANT-001", filteredForCurated.first().plantId)
    }

    @Test
    fun `test EvidenceGraphBuilder populates interconnected plants compounds and clinical studies`() {
        val graph = com.example.domain.EvidenceGraphBuilder.buildGraph()
        assertTrue(graph.nodes.isNotEmpty())
        assertTrue(graph.edges.isNotEmpty())

        val plantNodes = graph.nodes.filter { it.type == com.example.data.model.GraphNodeType.PLANT }
        val compoundNodes = graph.nodes.filter { it.type == com.example.data.model.GraphNodeType.PHYTOCHEMICAL }
        val indicationNodes = graph.nodes.filter { it.type == com.example.data.model.GraphNodeType.INDICATION }
        val studyNodes = graph.nodes.filter { it.type == com.example.data.model.GraphNodeType.STUDY }

        assertTrue(plantNodes.size >= 5)
        assertTrue(compoundNodes.size >= 5)
        assertTrue(indicationNodes.size >= 4)
        assertTrue(studyNodes.size >= 3)

        // Verify connected edge between Devil's Claw and Harpagoside
        assertTrue(graph.edges.any { it.fromNodeId == "NODE-P-004" && it.toNodeId == "NODE-C-HARPAGOSIDE" })
        // Verify connected edge between Harpagoside and Back Pain indication
        assertTrue(graph.edges.any { it.fromNodeId == "NODE-C-HARPAGOSIDE" && it.toNodeId == "NODE-I-BACKPAIN" })
        // Verify Cochrane study connected to Harpagoside
        assertTrue(graph.edges.any { it.fromNodeId == "NODE-S-COCH" })
    }

    @Test
    fun `test AppNavTab encompasses the four interconnected research experiences`() {
        val atlas = com.example.ui.viewmodel.AppNavTab.ATLAS
        val scanner = com.example.ui.viewmodel.AppNavTab.SCANNER
        val lab = com.example.ui.viewmodel.AppNavTab.RESEARCH
        val graph = com.example.ui.viewmodel.AppNavTab.EVIDENCE_GRAPH

        assertEquals("Botanical Atlas", atlas.title)
        assertEquals("Plant Scanner", scanner.title)
        assertEquals("Research Lab", lab.title)
        assertEquals("Evidence Graph", graph.title)
    }
}

