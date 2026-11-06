package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Toc
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Toc
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EvidenceGraphData
import com.example.data.model.EvidenceLevel
import com.example.data.model.GraphEdge
import com.example.data.model.GraphNode
import com.example.data.model.GraphNodeType
import com.example.data.model.MedicinalPlant
import com.example.ui.components.EvidenceBadge
import com.example.ui.theme.AccentLime
import com.example.ui.theme.BotanicalDisplayFontFamily
import com.example.ui.theme.BotanicalEditorialFontFamily
import com.example.ui.theme.DeepTealDark
import com.example.ui.theme.GreenAiColors
import com.example.ui.theme.GreenAiTheme
import com.example.ui.theme.PrimaryDeepTeal
import com.example.ui.theme.PureWhite

enum class GraphViewMode {
    CANVAS,
    MATRIX
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EvidenceGraphScreen(
    graphData: EvidenceGraphData,
    selectedNode: GraphNode?,
    selectedTypeFilter: GraphNodeType?,
    selectedEvidenceFilter: EvidenceLevel?,
    plants: List<MedicinalPlant>,
    onSelectNode: (GraphNode?) -> Unit,
    onFilterType: (GraphNodeType?) -> Unit,
    onFilterEvidence: (EvidenceLevel?) -> Unit,
    onOpenPlantDossier: (MedicinalPlant) -> Unit,
    onInvestigateInLab: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GreenAiTheme.colors
    var viewMode by remember { mutableStateOf(GraphViewMode.CANVAS) }

    val filteredNodes = remember(graphData.nodes, selectedTypeFilter, selectedEvidenceFilter) {
        graphData.nodes.filter { node ->
            val matchesType = selectedTypeFilter == null || node.type == selectedTypeFilter
            val matchesEvidence = selectedEvidenceFilter == null || node.evidenceLevel == selectedEvidenceFilter
            matchesType && matchesEvidence
        }
    }

    val connectedEdges = remember(graphData.edges, selectedNode) {
        if (selectedNode == null) emptyList()
        else graphData.edges.filter { it.fromNodeId == selectedNode.id || it.toNodeId == selectedNode.id }
    }

    val connectedNodeIds = remember(connectedEdges, selectedNode) {
        if (selectedNode == null) emptySet()
        else {
            val ids = mutableSetOf(selectedNode.id)
            connectedEdges.forEach {
                ids.add(it.fromNodeId)
                ids.add(it.toNodeId)
            }
            ids
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.screenBackground)
    ) {
        // Editorial Graph Header
        Surface(
            color = if (colors.isDark) colors.surfaceCard else PureWhite,
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, colors.borderSubtle)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (colors.isDark) colors.surfaceElevated else PrimaryDeepTeal)
                                .border(0.6.dp, AccentLime.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.green_ai_logo),
                                contentDescription = "Official Green AI Logo",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "RELATIONAL EVIDENCE GRAPH",
                                fontFamily = BotanicalDisplayFontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                            )
                            Text(
                                text = "Topological mapping of plants, compounds & trials",
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                        }
                    }

                    // View Mode Switcher
                    Surface(
                        color = colors.surfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(8.dp))
                            .padding(2.dp)
                    ) {
                        Row {
                            Surface(
                                color = if (viewMode == GraphViewMode.CANVAS) {
                                    if (colors.isDark) colors.brandAccent else colors.brandPrimary
                                } else Color.Transparent,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .clickable { viewMode = GraphViewMode.CANVAS }
                                    .testTag("graph_canvas_toggle")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Hub,
                                        contentDescription = "Canvas View",
                                        tint = if (viewMode == GraphViewMode.CANVAS) {
                                            if (colors.isDark) PrimaryDeepTeal else PureWhite
                                        } else colors.textSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Canvas",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (viewMode == GraphViewMode.CANVAS) {
                                            if (colors.isDark) PrimaryDeepTeal else PureWhite
                                        } else colors.textSecondary
                                    )
                                }
                            }

                            Surface(
                                color = if (viewMode == GraphViewMode.MATRIX) {
                                    if (colors.isDark) colors.brandAccent else colors.brandPrimary
                                } else Color.Transparent,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .clickable { viewMode = GraphViewMode.MATRIX }
                                    .testTag("graph_matrix_toggle")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Toc,
                                        contentDescription = "Matrix View",
                                        tint = if (viewMode == GraphViewMode.MATRIX) {
                                            if (colors.isDark) PrimaryDeepTeal else PureWhite
                                        } else colors.textSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Matrix",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (viewMode == GraphViewMode.MATRIX) {
                                            if (colors.isDark) PrimaryDeepTeal else PureWhite
                                        } else colors.textSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedTypeFilter == null,
                            onClick = { onFilterType(null) },
                            label = { Text("All Nodes (${graphData.nodes.size})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                selectedLabelColor = if (colors.isDark) PrimaryDeepTeal else PureWhite,
                                containerColor = colors.surfaceElevated,
                                labelColor = colors.textPrimary
                            ),
                            modifier = Modifier.testTag("filter_all_nodes")
                        )
                    }

                    items(GraphNodeType.entries) { type ->
                        val count = graphData.nodes.count { it.type == type }
                        FilterChip(
                            selected = selectedTypeFilter == type,
                            onClick = { onFilterType(if (selectedTypeFilter == type) null else type) },
                            label = { Text("${type.title} ($count)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                                selectedLabelColor = if (colors.isDark) PrimaryDeepTeal else PureWhite,
                                containerColor = colors.surfaceElevated,
                                labelColor = colors.textPrimary
                            ),
                            modifier = Modifier.testTag("filter_node_${type.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // Main Graph Presentation (Canvas vs Matrix)
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (viewMode == GraphViewMode.CANVAS) {
                BotanicalGraphCanvas(
                    nodes = filteredNodes,
                    edges = graphData.edges,
                    selectedNode = selectedNode,
                    connectedNodeIds = connectedNodeIds,
                    onSelectNode = onSelectNode,
                    colors = colors,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                GraphRelationalMatrix(
                    nodes = filteredNodes,
                    edges = graphData.edges,
                    selectedNode = selectedNode,
                    onSelectNode = onSelectNode,
                    colors = colors,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Reset Selection Action Floating Button
            if (selectedNode != null) {
                Surface(
                    color = colors.surfaceCard,
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .border(0.8.dp, colors.borderSubtle, CircleShape)
                        .clickable { onSelectNode(null) }
                        .testTag("reset_graph_selection_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear selection",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear Focus", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    }
                }
            }
        }

        // Interactive Node Inspector Card / Drawer (Reveals when a node is selected)
        AnimatedVisibility(
            visible = selectedNode != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            selectedNode?.let { node ->
                val connectedNodes = remember(node, graphData) {
                    val neighborIds = graphData.edges
                        .filter { it.fromNodeId == node.id || it.toNodeId == node.id }
                        .map { if (it.fromNodeId == node.id) it.toNodeId else it.fromNodeId }
                        .toSet()
                    graphData.nodes.filter { neighborIds.contains(it.id) }
                }

                val matchedPlant = plants.firstOrNull { it.id == node.associatedPlantId }

                NodeInspectorCard(
                    node = node,
                    connectedNodes = connectedNodes,
                    matchedPlant = matchedPlant,
                    onSelectNeighbor = { onSelectNode(it) },
                    onOpenDossier = { matchedPlant?.let { onOpenPlantDossier(it) } },
                    onInvestigate = { query -> onInvestigateInLab(query) },
                    onClose = { onSelectNode(null) },
                    colors = colors
                )
            }
        }
    }
}

/**
 * Botanical Vector Canvas rendering nodes, paths, and interactive touch points
 */
@Composable
fun BotanicalGraphCanvas(
    nodes: List<GraphNode>,
    edges: List<GraphEdge>,
    selectedNode: GraphNode?,
    connectedNodeIds: Set<String>,
    onSelectNode: (GraphNode?) -> Unit,
    colors: GreenAiColors,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(nodes, widthPx, heightPx) {
                    detectTapGestures { tapOffset ->
                        val hitNode = nodes.firstOrNull { node ->
                            val nx = node.x * widthPx
                            val ny = node.y * heightPx
                            val dist = kotlin.math.sqrt((tapOffset.x - nx) * (tapOffset.x - nx) + (tapOffset.y - ny) * (tapOffset.y - ny))
                            dist <= 36.dp.toPx()
                        }
                        onSelectNode(hitNode)
                    }
                }
                .testTag("botanical_graph_canvas")
        ) {
            // Draw subtle background botanical coordinate grid
            val gridColor = if (colors.isDark) Color(0xFF143B42).copy(alpha = 0.4f) else Color(0xFFE2E0D4).copy(alpha = 0.5f)
            val step = 80.dp.toPx()
            var x = 0f
            while (x < size.width) {
                drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 0.5.dp.toPx())
                x += step
            }
            var y = 0f
            while (y < size.height) {
                drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 0.5.dp.toPx())
                y += step
            }

            // Draw Edges
            val nodeMap = nodes.associateBy { it.id }
            for (edge in edges) {
                val fromNode = nodeMap[edge.fromNodeId]
                val toNode = nodeMap[edge.toNodeId]
                if (fromNode != null && toNode != null) {
                    val p1 = Offset(fromNode.x * size.width, fromNode.y * size.height)
                    val p2 = Offset(toNode.x * size.width, toNode.y * size.height)

                    val isConnectedToSelected = selectedNode != null &&
                            (fromNode.id == selectedNode.id || toNode.id == selectedNode.id)

                    val edgeColor = when {
                        selectedNode == null -> if (colors.isDark) Color(0xFF2E636D).copy(alpha = 0.5f) else Color(0xFFB5CFD4).copy(alpha = 0.7f)
                        isConnectedToSelected -> if (colors.isDark) AccentLime else PrimaryDeepTeal
                        else -> if (colors.isDark) Color(0xFF163238).copy(alpha = 0.2f) else Color(0xFFDFE6E8).copy(alpha = 0.3f)
                    }

                    val strokeWidth = if (isConnectedToSelected) 2.5.dp.toPx() else 1.2.dp.toPx()
                    val pathEffect = if (isConnectedToSelected) null else PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)

                    drawLine(
                        color = edgeColor,
                        start = p1,
                        end = p2,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round,
                        pathEffect = pathEffect
                    )
                }
            }

            // Draw Nodes
            for (node in nodes) {
                val cx = node.x * size.width
                val cy = node.y * size.height
                val center = Offset(cx, cy)

                val isSelected = selectedNode?.id == node.id
                val isConnected = connectedNodeIds.contains(node.id)

                val nodeRadius = when {
                    isSelected -> 22.dp.toPx()
                    node.type == GraphNodeType.PLANT -> 18.dp.toPx()
                    else -> 14.dp.toPx()
                }

                val baseColor = when (node.type) {
                    GraphNodeType.PLANT -> Color(0xFF00DD88)
                    GraphNodeType.PHYTOCHEMICAL -> AccentLime
                    GraphNodeType.INDICATION -> Color(0xFF38BDF8)
                    GraphNodeType.STUDY -> Color(0xFFF59E0B)
                }

                val fillAlpha = when {
                    selectedNode == null -> 0.9f
                    isSelected -> 1.0f
                    isConnected -> 0.85f
                    else -> 0.18f
                }

                // Outer Halo if selected or connected
                if (isSelected) {
                    drawCircle(
                        color = baseColor.copy(alpha = 0.25f),
                        radius = nodeRadius + 14.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = baseColor.copy(alpha = 0.5f),
                        radius = nodeRadius + 6.dp.toPx(),
                        center = center
                    )
                } else if (isConnected) {
                    drawCircle(
                        color = baseColor.copy(alpha = 0.2f),
                        radius = nodeRadius + 6.dp.toPx(),
                        center = center
                    )
                }

                // Node Center
                drawCircle(
                    color = baseColor.copy(alpha = fillAlpha),
                    radius = nodeRadius,
                    center = center
                )

                // White/Dark Rim
                drawCircle(
                    color = if (colors.isDark) Color(0xFF061B1F) else PureWhite,
                    radius = nodeRadius - 3.dp.toPx(),
                    center = center
                )

                // Inner core dot
                drawCircle(
                    color = baseColor.copy(alpha = fillAlpha),
                    radius = 4.dp.toPx(),
                    center = center
                )
            }
        }
    }
}

/**
 * Structured Relational Matrix listing all nodes with grouped scientific relationships
 */
@Composable
fun GraphRelationalMatrix(
    nodes: List<GraphNode>,
    edges: List<GraphEdge>,
    selectedNode: GraphNode?,
    onSelectNode: (GraphNode) -> Unit,
    colors: GreenAiColors,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surfaceElevated),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.7.dp, colors.borderSubtle, RoundedCornerShape(8.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "The Relational Matrix organizes evidence across 4 verified dimensions: Indigenous Plants, Active Phytochemicals, Therapeutic Indications, and Systematic Reviews.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = colors.textSecondary
                    )
                }
            }
        }

        items(nodes, key = { it.id }) { node ->
            val isSelected = selectedNode?.id == node.id
            val incidentCount = edges.count { it.fromNodeId == node.id || it.toNodeId == node.id }

            Surface(
                color = if (isSelected) (if (colors.isDark) colors.surfaceElevated else Color(0xFFEAF5F8)) else colors.surfaceCard,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        if (isSelected) 1.2.dp else 0.6.dp,
                        if (isSelected) (if (colors.isDark) colors.brandAccent else colors.brandPrimary) else colors.borderSubtle,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelectNode(node) }
                    .testTag("matrix_node_${node.id}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                when (node.type) {
                                    GraphNodeType.PLANT -> Color(0xFF00DD88)
                                    GraphNodeType.PHYTOCHEMICAL -> AccentLime
                                    GraphNodeType.INDICATION -> Color(0xFF38BDF8)
                                    GraphNodeType.STUDY -> Color(0xFFF59E0B)
                                }
                            )
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = node.label,
                                fontWeight = FontWeight.Bold,
                                fontFamily = if (node.type == GraphNodeType.PLANT) BotanicalEditorialFontFamily else BotanicalDisplayFontFamily,
                                fontSize = 13.sp,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = if (colors.isDark) colors.surfaceElevated else Color(0xFFE8ECEF),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = node.type.title.uppercase(),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textSecondary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = node.sublabel,
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = node.details,
                            fontSize = 10.sp,
                            color = colors.textMuted,
                            maxLines = 2
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = if (colors.isDark) colors.surfaceCard else colors.surfaceElevated,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.border(0.5.dp, colors.borderSubtle, RoundedCornerShape(4.dp))
                    ) {
                        Text(
                            text = "$incidentCount links",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Slide-up scientific inspector examining a focused node
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NodeInspectorCard(
    node: GraphNode,
    connectedNodes: List<GraphNode>,
    matchedPlant: MedicinalPlant?,
    onSelectNeighbor: (GraphNode) -> Unit,
    onOpenDossier: () -> Unit,
    onInvestigate: (String) -> Unit,
    onClose: () -> Unit,
    colors: GreenAiColors
) {
    Surface(
        color = colors.surfaceCard,
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.borderSubtle)
            .testTag("node_inspector_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                when (node.type) {
                                    GraphNodeType.PLANT -> Color(0xFF00DD88)
                                    GraphNodeType.PHYTOCHEMICAL -> AccentLime
                                    GraphNodeType.INDICATION -> Color(0xFF38BDF8)
                                    GraphNodeType.STUDY -> Color(0xFFF59E0B)
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = node.type.title.uppercase(),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp,
                        color = if (colors.isDark) colors.brandAccent else colors.brandPrimary
                    )
                    node.evidenceLevel?.let { level ->
                        Spacer(modifier = Modifier.width(8.dp))
                        EvidenceBadge(level = level)
                    }
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Inspector",
                        tint = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = node.label,
                fontFamily = if (node.type == GraphNodeType.PLANT) BotanicalEditorialFontFamily else BotanicalDisplayFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = colors.textPrimary
            )

            Text(
                text = node.sublabel,
                fontSize = 12.sp,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = node.details,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = colors.textPrimary
            )

            node.citationRef?.let { cit ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Authoritative Source: $cit",
                    fontSize = 10.sp,
                    fontFamily = BotanicalDisplayFontFamily,
                    color = colors.textMuted
                )
            }

            // Connected Entities Flow Row
            if (connectedNodes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Connected Topological Entities (${connectedNodes.size}):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (neighbor in connectedNodes) {
                        Surface(
                            color = colors.surfaceElevated,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .border(0.6.dp, colors.borderSubtle, RoundedCornerShape(4.dp))
                                .clickable { onSelectNeighbor(neighbor) }
                        ) {
                            Text(
                                text = neighbor.label,
                                fontSize = 10.sp,
                                color = colors.textPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (matchedPlant != null) {
                    Button(
                        onClick = onOpenDossier,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                            contentColor = if (colors.isDark) PrimaryDeepTeal else PureWhite
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f).testTag("inspector_open_dossier_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open Full Dossier", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = {
                        val query = when (node.type) {
                            GraphNodeType.PLANT -> "What are the primary active phytochemicals and therapeutic evidence for ${node.label}?"
                            GraphNodeType.PHYTOCHEMICAL -> "What is the pharmacological mechanism and safety profile of ${node.label} in African medicinal plants?"
                            GraphNodeType.INDICATION -> "Which African medicinal plants have documented clinical evidence for ${node.label}?"
                            GraphNodeType.STUDY -> "Summarize the methodology, dosage, and findings of ${node.label}."
                        }
                        onInvestigate(query)
                    },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f).testTag("inspector_query_lab_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = if (colors.isDark) colors.brandAccent else colors.brandPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Investigate in Lab", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
