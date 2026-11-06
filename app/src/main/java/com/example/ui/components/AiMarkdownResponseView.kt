package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GreenAiColors
import com.example.ui.theme.GreenAiTheme

/**
 * Robust, highly readable Markdown renderer designed specifically for Green AI research responses.
 * Guarantees crisp contrast and legible typography in both Light and Dark themes, avoiding
 * color-inheritance bugs or low-contrast text.
 */
@Composable
fun AiMarkdownResponseView(
    markdownText: String,
    modifier: Modifier = Modifier,
    colors: GreenAiColors = GreenAiTheme.colors,
    onCitationClick: ((String) -> Unit)? = null
) {
    val blocks = remember(markdownText) { parseMarkdownBlocks(markdownText) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for ((idx, block) in blocks.withIndex()) {
            when (block) {
                is MarkdownBlock.Heading -> {
                    val fontSize = when (block.level) {
                        1 -> 16.sp
                        2 -> 14.5.sp
                        3 -> 13.5.sp
                        else -> 12.5.sp
                    }
                    val headingColor = if (block.level <= 2) colors.aiTextHeading else colors.aiTextSubheading
                    Text(
                        text = formatInlineMarkdown(block.text, colors),
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold,
                        color = headingColor,
                        lineHeight = (fontSize.value + 6).sp,
                        modifier = Modifier
                            .padding(top = if (idx > 0) 6.dp else 0.dp, bottom = 2.dp)
                            .testTag("ai_heading_${block.level}")
                    )
                }

                is MarkdownBlock.Paragraph -> {
                    Text(
                        text = formatInlineMarkdown(block.text, colors),
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = colors.aiTextParagraph,
                        modifier = Modifier.testTag("ai_paragraph")
                    )
                }

                is MarkdownBlock.BulletItem -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = (block.indent * 12).dp, top = 2.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 7.dp, end = 8.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(colors.aiBulletMarker)
                        )
                        Text(
                            text = formatInlineMarkdown(block.text, colors),
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = colors.aiTextParagraph,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                is MarkdownBlock.NumberedItem -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = (block.indent * 12).dp, top = 2.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            color = colors.aiNumberMarker.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = block.number,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.aiNumberMarker,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Text(
                            text = formatInlineMarkdown(block.text, colors),
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = colors.aiTextParagraph,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                is MarkdownBlock.Blockquote -> {
                    Surface(
                        color = colors.aiBlockquoteBg,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.5.dp, colors.borderSubtle, RoundedCornerShape(4.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(colors.aiBlockquoteBorder)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = formatInlineMarkdown(block.text, colors),
                                fontSize = 12.5.sp,
                                fontStyle = FontStyle.Italic,
                                lineHeight = 18.sp,
                                color = colors.aiBlockquoteText,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                is MarkdownBlock.CodeBlock -> {
                    val clipboard = LocalClipboardManager.current
                    val context = LocalContext.current
                    Surface(
                        color = colors.aiCodeBlockBg,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, colors.aiCodeBlockBorder, RoundedCornerShape(8.dp))
                            .testTag("ai_code_block")
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = block.language.ifBlank { "scientific_formula" }.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.brandAccent,
                                    letterSpacing = 0.5.sp
                                )
                                IconButton(
                                    onClick = {
                                        clipboard.setText(AnnotatedString(block.code))
                                        Toast.makeText(context, "Formula copied to clipboard", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy code block",
                                        tint = colors.brandAccent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = block.code,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                fontFamily = FontFamily.Monospace,
                                color = colors.aiCodeBlockText,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                            )
                        }
                    }
                }

                is MarkdownBlock.Table -> {
                    val tableScroll = rememberScrollState()
                    Surface(
                        color = colors.surfaceCard,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.7.dp, colors.aiTableBorder, RoundedCornerShape(6.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(tableScroll)
                                .padding(4.dp)
                        ) {
                            // Header Row
                            if (block.headers.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .background(colors.aiTableHeaderBg, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    for (cell in block.headers) {
                                        Text(
                                            text = cell.trim(),
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.aiTableHeaderText,
                                            modifier = Modifier
                                                .width(140.dp)
                                                .padding(horizontal = 4.dp)
                                        )
                                    }
                                }
                            }

                            // Rows
                            for ((rowIdx, row) in block.rows.withIndex()) {
                                val rowBg = if (rowIdx % 2 == 0) colors.aiTableRowBg else colors.aiTableRowAltBg
                                Row(
                                    modifier = Modifier
                                        .background(rowBg)
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    for (cell in row) {
                                        Text(
                                            text = formatInlineMarkdown(cell.trim(), colors),
                                            fontSize = 11.5.sp,
                                            color = colors.aiTableCellText,
                                            modifier = Modifier
                                                .width(140.dp)
                                                .padding(horizontal = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Formats inline Markdown (**bold**, *italic*, `code`, [citations] and links) with explicit colors.
 */
@Composable
fun formatInlineMarkdown(text: String, colors: GreenAiColors): AnnotatedString {
    return remember(text, colors) {
        buildAnnotatedString {
            var i = 0
            val len = text.length

            while (i < len) {
                // Bold & Italic ***text***
                if (i + 2 < len && text[i] == '*' && text[i + 1] == '*' && text[i + 2] == '*') {
                    val end = text.indexOf("***", i + 3)
                    if (end != -1) {
                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic, color = colors.aiTextHeading))
                        append(text.substring(i + 3, end))
                        pop()
                        i = end + 3
                        continue
                    }
                }

                // Bold **text**
                if (i + 1 < len && text[i] == '*' && text[i + 1] == '*') {
                    val end = text.indexOf("**", i + 2)
                    if (end != -1) {
                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = colors.aiTextHeading))
                        append(text.substring(i + 2, end))
                        pop()
                        i = end + 2
                        continue
                    }
                }

                // Italic *text*
                if (text[i] == '*' && (i == 0 || text[i - 1] != '*') && (i + 1 < len && text[i + 1] != '*')) {
                    val end = text.indexOf('*', i + 1)
                    if (end != -1 && end + 1 <= len) {
                        pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                        append(text.substring(i + 1, end))
                        pop()
                        i = end + 1
                        continue
                    }
                }

                // Inline code `code`
                if (text[i] == '`') {
                    val end = text.indexOf('`', i + 1)
                    if (end != -1) {
                        pushStyle(
                            SpanStyle(
                                fontFamily = FontFamily.Monospace,
                                background = colors.aiInlineCodeBg,
                                color = colors.aiInlineCodeText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        append(" ${text.substring(i + 1, end)} ")
                        pop()
                        i = end + 1
                        continue
                    }
                }

                // Citation reference like [1], [2], [Source]
                if (text[i] == '[') {
                    val end = text.indexOf(']', i + 1)
                    if (end != -1 && end - i <= 15) {
                        val citationContent = text.substring(i, end + 1)
                        pushStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = colors.aiCitationText,
                                textDecoration = TextDecoration.Underline,
                                background = colors.aiCitationBg
                            )
                        )
                        append(citationContent)
                        pop()
                        i = end + 1
                        continue
                    }
                }

                append(text[i])
                i++
            }
        }
    }
}

sealed class MarkdownBlock {
    data class Heading(val level: Int, val text: String) : MarkdownBlock()
    data class Paragraph(val text: String) : MarkdownBlock()
    data class BulletItem(val text: String, val indent: Int = 0) : MarkdownBlock()
    data class NumberedItem(val number: String, val text: String, val indent: Int = 0) : MarkdownBlock()
    data class Blockquote(val text: String) : MarkdownBlock()
    data class CodeBlock(val language: String, val code: String) : MarkdownBlock()
    data class Table(val headers: List<String>, val rows: List<List<String>>) : MarkdownBlock()
}

/**
 * Parses markdown text into high-level structural blocks.
 */
fun parseMarkdownBlocks(rawMarkdown: String): List<MarkdownBlock> {
    val lines = rawMarkdown.lines()
    val blocks = mutableListOf<MarkdownBlock>()
    var i = 0

    while (i < lines.size) {
        val line = lines[i]
        val trimmed = line.trim()

        if (trimmed.isEmpty()) {
            i++
            continue
        }

        // Fenced Code Block
        if (trimmed.startsWith("```")) {
            val lang = trimmed.removePrefix("```").trim()
            val codeLines = mutableListOf<String>()
            i++
            while (i < lines.size && !lines[i].trim().startsWith("```")) {
                codeLines.add(lines[i])
                i++
            }
            if (i < lines.size) i++ // Skip closing ```
            blocks.add(MarkdownBlock.CodeBlock(lang, codeLines.joinToString("\n")))
            continue
        }

        // Markdown Table
        if (trimmed.startsWith("|") && trimmed.endsWith("|")) {
            val tableLines = mutableListOf<String>()
            while (i < lines.size && lines[i].trim().startsWith("|") && lines[i].trim().endsWith("|")) {
                tableLines.add(lines[i].trim())
                i++
            }
            if (tableLines.size >= 2) {
                val headerRow = tableLines[0].split("|").filter { it.isNotBlank() }
                val dataRows = mutableListOf<List<String>>()
                for (tIdx in 1 until tableLines.size) {
                    val row = tableLines[tIdx]
                    // Skip divider rows like |---|---|
                    if (row.replace("-", "").replace("|", "").replace(":", "").isBlank()) {
                        continue
                    }
                    val cells = row.split("|").filter { it.isNotBlank() }
                    dataRows.add(cells)
                }
                blocks.add(MarkdownBlock.Table(headerRow, dataRows))
                continue
            }
        }

        // Headings
        if (trimmed.startsWith("#")) {
            var level = 0
            while (level < trimmed.length && trimmed[level] == '#') {
                level++
            }
            val headingText = trimmed.substring(level).trim()
            blocks.add(MarkdownBlock.Heading(level.coerceIn(1, 4), headingText))
            i++
            continue
        }

        // Blockquotes
        if (trimmed.startsWith(">")) {
            val quoteLines = mutableListOf<String>()
            while (i < lines.size && lines[i].trim().startsWith(">")) {
                quoteLines.add(lines[i].trim().removePrefix(">").trim())
                i++
            }
            blocks.add(MarkdownBlock.Blockquote(quoteLines.joinToString(" ")))
            continue
        }

        // Bullet lists (* or -)
        if (trimmed.startsWith("* ") || trimmed.startsWith("- ") || trimmed.startsWith("• ")) {
            val prefix = if (trimmed.startsWith("* ") || trimmed.startsWith("- ")) 2 else 1
            val indent = (line.indexOfFirst { !it.isWhitespace() } / 2).coerceAtLeast(0)
            blocks.add(MarkdownBlock.BulletItem(trimmed.substring(prefix).trim(), indent))
            i++
            continue
        }

        // Numbered lists (e.g. "1. ")
        val numberRegex = Regex("""^(\d+[\.\)])\s+(.*)""")
        val numberMatch = numberRegex.find(trimmed)
        if (numberMatch != null) {
            val num = numberMatch.groupValues[1]
            val rest = numberMatch.groupValues[2]
            val indent = (line.indexOfFirst { !it.isWhitespace() } / 2).coerceAtLeast(0)
            blocks.add(MarkdownBlock.NumberedItem(num, rest, indent))
            i++
            continue
        }

        // Regular paragraph (accumulate consecutive non-empty lines)
        val paraLines = mutableListOf<String>()
        while (i < lines.size) {
            val nextTrimmed = lines[i].trim()
            if (nextTrimmed.isEmpty() ||
                nextTrimmed.startsWith("#") ||
                nextTrimmed.startsWith("```") ||
                nextTrimmed.startsWith(">") ||
                nextTrimmed.startsWith("* ") ||
                nextTrimmed.startsWith("- ") ||
                nextTrimmed.startsWith("• ") ||
                numberRegex.matches(nextTrimmed) ||
                (nextTrimmed.startsWith("|") && nextTrimmed.endsWith("|"))
            ) {
                break
            }
            paraLines.add(nextTrimmed)
            i++
        }
        if (paraLines.isNotEmpty()) {
            blocks.add(MarkdownBlock.Paragraph(paraLines.joinToString(" ")))
        }
    }

    return blocks
}
