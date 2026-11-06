package com.example.data.model

enum class GraphNodeType(val title: String, val badgeColorHex: Long) {
    PLANT("Medicinal Plant", 0xFF00DD88),
    PHYTOCHEMICAL("Phytochemical Compound", 0xFFABFE3E),
    INDICATION("Therapeutic Indication", 0xFF38BDF8),
    STUDY("Peer-Reviewed Study", 0xFFF59E0B)
}

data class GraphNode(
    val id: String,
    val label: String,
    val sublabel: String,
    val type: GraphNodeType,
    val evidenceLevel: EvidenceLevel? = null,
    val associatedPlantId: String? = null,
    val details: String,
    val citationRef: String? = null,
    val x: Float, // Normalized 0.0f..1.0f on Canvas
    val y: Float
)

data class GraphEdge(
    val fromNodeId: String,
    val toNodeId: String,
    val relationLabel: String,
    val evidenceLevel: EvidenceLevel
)

data class EvidenceGraphData(
    val nodes: List<GraphNode>,
    val edges: List<GraphEdge>
)
