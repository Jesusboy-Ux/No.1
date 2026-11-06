package com.example.domain

import com.example.data.CuratedKnowledgeBase
import com.example.data.model.EvidenceGraphData
import com.example.data.model.EvidenceLevel
import com.example.data.model.GraphEdge
import com.example.data.model.GraphNode
import com.example.data.model.GraphNodeType
import com.example.data.model.MedicinalPlant

object EvidenceGraphBuilder {

    fun buildGraph(additionalPlants: List<MedicinalPlant> = emptyList()): EvidenceGraphData {
        val nodes = mutableListOf<GraphNode>()
        val edges = mutableListOf<GraphEdge>()

        // 1. Plant Nodes (Core Botanical Hubs)
        nodes.add(
            GraphNode(
                id = "NODE-P-001",
                label = "Artemisia afra",
                sublabel = "African Wormwood • Asteraceae",
                type = GraphNodeType.PLANT,
                evidenceLevel = EvidenceLevel.LABORATORY_EVIDENCE,
                associatedPlantId = "PLANT-001",
                details = "Indigenous southern & eastern African shrub. High volatile monoterpene content utilized for respiratory vapor inhalation and microbial inhibition.",
                citationRef = "Liu et al. (2009) S. Afr. J. Bot.",
                x = 0.22f,
                y = 0.22f
            )
        )
        nodes.add(
            GraphNode(
                id = "NODE-P-002",
                label = "Moringa oleifera",
                sublabel = "Miracle Tree • Moringaceae",
                type = GraphNodeType.PLANT,
                evidenceLevel = EvidenceLevel.HUMAN_RESEARCH_AVAILABLE,
                associatedPlantId = "PLANT-002",
                details = "Widely cultivated across sub-Saharan Africa. Leaf extract demonstrates postprandial glycemic moderation and high micronutrient density.",
                citationRef = "Leone et al. (2015) Int. J. Mol. Sci.",
                x = 0.78f,
                y = 0.22f
            )
        )
        nodes.add(
            GraphNode(
                id = "NODE-P-003",
                label = "Vernonia amygdalina",
                sublabel = "Bitter Leaf • Asteraceae",
                type = GraphNodeType.PLANT,
                evidenceLevel = EvidenceLevel.ANIMAL_RESEARCH,
                associatedPlantId = "PLANT-003",
                details = "West & Central African medicinal shrub. Sesquiterpene lactones exhibit in vitro antiplasmodial and in vivo hepatoprotective activities.",
                citationRef = "Yeap et al. (2010) J. Med. Plants Res.",
                x = 0.22f,
                y = 0.78f
            )
        )
        nodes.add(
            GraphNode(
                id = "NODE-P-004",
                label = "Harpagophytum procumbens",
                sublabel = "Devil's Claw • Pedaliaceae",
                type = GraphNodeType.PLANT,
                evidenceLevel = EvidenceLevel.HUMAN_RESEARCH_AVAILABLE,
                associatedPlantId = "PLANT-004",
                details = "Kalahari desert tuberous plant. Harpagoside active principle confirmed by Cochrane systematic review for lower back pain and osteoarthritis.",
                citationRef = "Gagnier et al. (2007) Cochrane Review",
                x = 0.78f,
                y = 0.78f
            )
        )
        nodes.add(
            GraphNode(
                id = "NODE-P-005",
                label = "Catharanthus roseus",
                sublabel = "Madagascar Periwinkle • Apocynaceae",
                type = GraphNodeType.PLANT,
                evidenceLevel = EvidenceLevel.HUMAN_RESEARCH_AVAILABLE,
                associatedPlantId = "PLANT-005",
                details = "Indigenous to Madagascar. Source of pharmaceutical vinca alkaloids vinblastine and vincristine; highly cytotoxic mitotic spindle poisons.",
                citationRef = "Noble (1990) Biochem. Cell Biol.",
                x = 0.50f,
                y = 0.50f
            )
        )

        // 2. Phytochemical Nodes
        nodes.add(
            GraphNode(
                id = "NODE-C-THUJONE",
                label = "Thujone",
                sublabel = "Monoterpene Ketone",
                type = GraphNodeType.PHYTOCHEMICAL,
                evidenceLevel = EvidenceLevel.LABORATORY_EVIDENCE,
                associatedPlantId = "PLANT-001",
                details = "Major volatile component in Artemisia afra. Shows potent antimicrobial activity; high concentrations exhibit neurotoxic GABA-A antagonistic properties.",
                citationRef = "Van Wyk (2008)",
                x = 0.12f,
                y = 0.35f
            )
        )
        nodes.add(
            GraphNode(
                id = "NODE-C-CINEOLE",
                label = "1,8-Cineole (Eucalyptol)",
                sublabel = "Monoterpenoid Oxide",
                type = GraphNodeType.PHYTOCHEMICAL,
                evidenceLevel = EvidenceLevel.LABORATORY_EVIDENCE,
                associatedPlantId = "PLANT-001",
                details = "Lipophilic volatile oxide providing characteristic medicinal aroma. Demonstrates mucolytic and anti-inflammatory bronchial relaxation.",
                citationRef = "Liu et al. (2009)",
                x = 0.32f,
                y = 0.12f
            )
        )
        nodes.add(
            GraphNode(
                id = "NODE-C-GLUCO",
                label = "Glucomoringin",
                sublabel = "Glucosinolate Precursor",
                type = GraphNodeType.PHYTOCHEMICAL,
                evidenceLevel = EvidenceLevel.LABORATORY_EVIDENCE,
                associatedPlantId = "PLANT-002",
                details = "Hydrolyzed by myrosinase into moringin (isothiocyanate), modulating phase II antioxidant enzymes and anti-inflammatory cellular pathways.",
                citationRef = "Leone et al. (2015)",
                x = 0.88f,
                y = 0.35f
            )
        )
        nodes.add(
            GraphNode(
                id = "NODE-C-QUERCETIN",
                label = "Quercetin",
                sublabel = "Flavonol Glycoside",
                type = GraphNodeType.PHYTOCHEMICAL,
                evidenceLevel = EvidenceLevel.HUMAN_RESEARCH_AVAILABLE,
                associatedPlantId = "PLANT-002",
                details = "Abundant dietary flavonoid in Moringa leaves. Scavenges reactive oxygen species and contributes to postprandial glucose regulation.",
                citationRef = "Stohs & Hartman (2015)",
                x = 0.68f,
                y = 0.12f
            )
        )
        nodes.add(
            GraphNode(
                id = "NODE-C-VERNODALIN",
                label = "Vernodalin",
                sublabel = "Sesquiterpene Lactone",
                type = GraphNodeType.PHYTOCHEMICAL,
                evidenceLevel = EvidenceLevel.LABORATORY_EVIDENCE,
                associatedPlantId = "PLANT-003",
                details = "Bitter principle from Vernonia amygdalina. Shows in vitro antiplasmodial efficacy against chloroquine-sensitive Plasmodium falciparum.",
                citationRef = "Farombi & Owoeye (2011)",
                x = 0.12f,
                y = 0.65f
            )
        )
        nodes.add(
            GraphNode(
                id = "NODE-C-HARPAGOSIDE",
                label = "Harpagoside",
                sublabel = "Iridoid Glucoside",
                type = GraphNodeType.PHYTOCHEMICAL,
                evidenceLevel = EvidenceLevel.HUMAN_RESEARCH_AVAILABLE,
                associatedPlantId = "PLANT-004",
                details = "Active phytochemical marker in Devil's Claw root tuber. Inhibits pro-inflammatory cytokines and biosynthesis of leukotrienes in musculoskeletal tissue.",
                citationRef = "Gagnier et al. (2007)",
                x = 0.88f,
                y = 0.65f
            )
        )
        nodes.add(
            GraphNode(
                id = "NODE-C-VINCRISTINE",
                label = "Vincristine & Vinblastine",
                sublabel = "Dimeric Indole-Indoline Alkaloids",
                type = GraphNodeType.PHYTOCHEMICAL,
                evidenceLevel = EvidenceLevel.HUMAN_RESEARCH_AVAILABLE,
                associatedPlantId = "PLANT-005",
                details = "Binds to tubulin dimers inhibiting cellular microtubule assembly during mitosis. Essential oncology chemotherapeutic agent.",
                citationRef = "Noble (1990)",
                x = 0.50f,
                y = 0.66f
            )
        )

        // 3. Therapeutic Indications & Biological Actions
        nodes.add(
            GraphNode(
                id = "NODE-I-RESP",
                label = "Respiratory Inhalation",
                sublabel = "Bronchial & Microbial Target",
                type = GraphNodeType.INDICATION,
                evidenceLevel = EvidenceLevel.LABORATORY_EVIDENCE,
                details = "Traditional steam inhalation protocol. Corroborated by in vitro antibacterial assays against common respiratory pathogens.",
                citationRef = "Liu et al. (2009)",
                x = 0.28f,
                y = 0.35f
            )
        )
        nodes.add(
            GraphNode(
                id = "NODE-I-GLYCEMIC",
                label = "Glycemic Moderation",
                sublabel = "Metabolic Regulation",
                type = GraphNodeType.INDICATION,
                evidenceLevel = EvidenceLevel.HUMAN_RESEARCH_AVAILABLE,
                details = "Moderate reduction in postprandial blood glucose documented in preliminary human trials of Moringa leaf powder.",
                citationRef = "Leone et al. (2015)",
                x = 0.72f,
                y = 0.35f
            )
        )
        nodes.add(
            GraphNode(
                id = "NODE-I-BACKPAIN",
                label = "Lower Back Pain & Arthritis",
                sublabel = "Musculoskeletal Analgesia",
                type = GraphNodeType.INDICATION,
                evidenceLevel = EvidenceLevel.HUMAN_RESEARCH_AVAILABLE,
                details = "Standardized Harpagophytum root extract (50-100mg harpagoside daily) proven superior to placebo in multiple randomized controlled trials.",
                citationRef = "Cochrane Systematic Review (2007)",
                x = 0.72f,
                y = 0.90f
            )
        )
        nodes.add(
            GraphNode(
                id = "NODE-I-ONCOLOGY",
                label = "Oncology & Antimitotic Action",
                sublabel = "Hematologic Malignancies",
                type = GraphNodeType.INDICATION,
                evidenceLevel = EvidenceLevel.HUMAN_RESEARCH_AVAILABLE,
                details = "Standard of care chemotherapeutic component for acute lymphoblastic leukemia and Hodgkin's lymphoma derived from Catharanthus.",
                citationRef = "Noble (1990)",
                x = 0.50f,
                y = 0.35f
            )
        )
        nodes.add(
            GraphNode(
                id = "NODE-I-ANTIPLASMODIAL",
                label = "Antiplasmodial Assays",
                sublabel = "Parasitological Inhibition",
                type = GraphNodeType.INDICATION,
                evidenceLevel = EvidenceLevel.LABORATORY_EVIDENCE,
                details = "Ethanolic and aqueous extracts demonstrate selective in vitro inhibition of Plasmodium falciparum asexual blood stages.",
                citationRef = "Yeap et al. (2010)",
                x = 0.28f,
                y = 0.90f
            )
        )
        nodes.add(
            GraphNode(
                id = "NODE-I-PREGNANCY",
                label = "Uterine Stimulation Alert",
                sublabel = "Pharmacological Contraindication",
                type = GraphNodeType.INDICATION,
                evidenceLevel = EvidenceLevel.ANIMAL_RESEARCH,
                details = "Documented oxytocic activity inducing uterine smooth muscle contraction; strictly contraindicated during pregnancy.",
                citationRef = "Yeap et al. (2010)",
                x = 0.12f,
                y = 0.90f
            )
        )

        // 4. Curated Scientific Study Nodes
        nodes.add(
            GraphNode(
                id = "NODE-S-COCH",
                label = "Cochrane Back Pain Review",
                sublabel = "Spine 32(1):82-92 (2007)",
                type = GraphNodeType.STUDY,
                evidenceLevel = EvidenceLevel.HUMAN_RESEARCH_AVAILABLE,
                details = "Systematic review and meta-analysis confirming Harpagophytum procumbens reduces acute exacerbations of chronic low back pain.",
                citationRef = "https://doi.org/10.1097/01.brs.0000252097.42823.50",
                x = 0.90f,
                y = 0.80f
            )
        )
        nodes.add(
            GraphNode(
                id = "NODE-S-LIU",
                label = "Liu et al. Afra Review",
                sublabel = "S. Afr. J. Bot. 75:185 (2009)",
                type = GraphNodeType.STUDY,
                evidenceLevel = EvidenceLevel.LABORATORY_EVIDENCE,
                details = "Monographic overview detailing essential oil variation, traditional remedies, and bioassays of African Wormwood.",
                citationRef = "https://doi.org/10.1016/j.sajb.2008.11.009",
                x = 0.10f,
                y = 0.18f
            )
        )
        nodes.add(
            GraphNode(
                id = "NODE-S-NOBLE",
                label = "Noble Vinca Discovery",
                sublabel = "Biochem. Cell Biol. 68 (1990)",
                type = GraphNodeType.STUDY,
                evidenceLevel = EvidenceLevel.HUMAN_RESEARCH_AVAILABLE,
                details = "Historical account of the extraction of vinblastine and vincristine from Catharanthus roseus and its oncological applications.",
                citationRef = "https://doi.org/10.1139/o90-197",
                x = 0.50f,
                y = 0.82f
            )
        )

        // 5. Connective Relational Edges
        // Plant -> Compound edges
        edges.add(GraphEdge("NODE-P-001", "NODE-C-THUJONE", "Biosynthesizes", EvidenceLevel.LABORATORY_EVIDENCE))
        edges.add(GraphEdge("NODE-P-001", "NODE-C-CINEOLE", "Biosynthesizes", EvidenceLevel.LABORATORY_EVIDENCE))
        edges.add(GraphEdge("NODE-P-002", "NODE-C-GLUCO", "Biosynthesizes", EvidenceLevel.LABORATORY_EVIDENCE))
        edges.add(GraphEdge("NODE-P-002", "NODE-C-QUERCETIN", "Accumulates", EvidenceLevel.HUMAN_RESEARCH_AVAILABLE))
        edges.add(GraphEdge("NODE-P-003", "NODE-C-VERNODALIN", "Biosynthesizes", EvidenceLevel.LABORATORY_EVIDENCE))
        edges.add(GraphEdge("NODE-P-004", "NODE-C-HARPAGOSIDE", "Biosynthesizes", EvidenceLevel.HUMAN_RESEARCH_AVAILABLE))
        edges.add(GraphEdge("NODE-P-005", "NODE-C-VINCRISTINE", "Biosynthesizes", EvidenceLevel.HUMAN_RESEARCH_AVAILABLE))

        // Compound -> Indication edges
        edges.add(GraphEdge("NODE-C-CINEOLE", "NODE-I-RESP", "Promotes Mucolysis", EvidenceLevel.LABORATORY_EVIDENCE))
        edges.add(GraphEdge("NODE-C-THUJONE", "NODE-I-RESP", "Antimicrobial Action", EvidenceLevel.LABORATORY_EVIDENCE))
        edges.add(GraphEdge("NODE-C-QUERCETIN", "NODE-I-GLYCEMIC", "Modulates Glucose", EvidenceLevel.HUMAN_RESEARCH_AVAILABLE))
        edges.add(GraphEdge("NODE-C-GLUCO", "NODE-I-GLYCEMIC", "Isothiocyanate Hydrolysis", EvidenceLevel.LABORATORY_EVIDENCE))
        edges.add(GraphEdge("NODE-C-HARPAGOSIDE", "NODE-I-BACKPAIN", "Inhibits COX-2 / LOX", EvidenceLevel.HUMAN_RESEARCH_AVAILABLE))
        edges.add(GraphEdge("NODE-C-VINCRISTINE", "NODE-I-ONCOLOGY", "Depolymerizes Tubulin", EvidenceLevel.HUMAN_RESEARCH_AVAILABLE))
        edges.add(GraphEdge("NODE-C-VERNODALIN", "NODE-I-ANTIPLASMODIAL", "Antiparasitic Assays", EvidenceLevel.LABORATORY_EVIDENCE))
        edges.add(GraphEdge("NODE-P-003", "NODE-I-PREGNANCY", "Oxytocic Contraindication", EvidenceLevel.ANIMAL_RESEARCH))

        // Plant -> Indication direct ethnobotanical edges
        edges.add(GraphEdge("NODE-P-001", "NODE-I-RESP", "Traditional Inhalation", EvidenceLevel.TRADITIONAL_USE_REPORTED))
        edges.add(GraphEdge("NODE-P-004", "NODE-I-BACKPAIN", "Decoction for Joint Pain", EvidenceLevel.HUMAN_RESEARCH_AVAILABLE))
        edges.add(GraphEdge("NODE-P-003", "NODE-I-ANTIPLASMODIAL", "Traditional Bitter Tonic", EvidenceLevel.TRADITIONAL_USE_REPORTED))

        // Study -> Findings edges
        edges.add(GraphEdge("NODE-S-COCH", "NODE-C-HARPAGOSIDE", "Quantifies Efficacy", EvidenceLevel.HUMAN_RESEARCH_AVAILABLE))
        edges.add(GraphEdge("NODE-S-COCH", "NODE-I-BACKPAIN", "Validates Analgesia", EvidenceLevel.HUMAN_RESEARCH_AVAILABLE))
        edges.add(GraphEdge("NODE-S-LIU", "NODE-P-001", "Reviews Monograph", EvidenceLevel.LABORATORY_EVIDENCE))
        edges.add(GraphEdge("NODE-S-LIU", "NODE-C-THUJONE", "Documents Toxicity", EvidenceLevel.LABORATORY_EVIDENCE))
        edges.add(GraphEdge("NODE-S-NOBLE", "NODE-C-VINCRISTINE", "Details Isolation", EvidenceLevel.HUMAN_RESEARCH_AVAILABLE))
        edges.add(GraphEdge("NODE-S-NOBLE", "NODE-I-ONCOLOGY", "Clinical Oncogenesis", EvidenceLevel.HUMAN_RESEARCH_AVAILABLE))

        return EvidenceGraphData(nodes = nodes, edges = edges)
    }
}
