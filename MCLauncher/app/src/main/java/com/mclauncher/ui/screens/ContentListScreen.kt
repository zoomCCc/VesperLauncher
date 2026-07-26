package com.mclauncher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mclauncher.data.models.*
import com.mclauncher.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentListScreen(
    title: String,
    icon: @Composable () -> Unit,
    items: List<ContentItemData>,
    platforms: List<String>,
    selectedPlatform: String,
    onPlatformChange: (String) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onDownload: (ContentItemData) -> Unit,
    onInstall: (ContentItemData) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    icon()
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(title, fontWeight = FontWeight.Bold)
                }
            },
            actions = {
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Filled.Refresh, contentDescription = "刷新")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        ScrollableTabRow(
            selectedTabIndex = platforms.indexOf(selectedPlatform).coerceAtLeast(0),
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            edgePadding = 16.dp
        ) {
            platforms.forEach { platform ->
                Tab(
                    selected = selectedPlatform == platform,
                    onClick = { onPlatformChange(platform) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val color = when (platform) {
                                "CurseForge" -> Orange40
                                "Modrinth" -> Color(0xFF1BD96A)
                                "Planet Minecraft" -> Blue40
                                else -> Color.Gray
                            }
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = platform.replace(" ", "\n"),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selectedPlatform == platform) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 2
                            )
                        }
                    }
                )
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("搜索...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Green60)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("加载中...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { item ->
                    ContentCard(
                        item = item,
                        onDownload = { onInstall(item) }
                    )
                }
            }
        }
    }
}

data class ContentItemData(
    val id: String,
    val name: String,
    val summary: String,
    val downloadCount: Long,
    val iconUrl: String?,
    val category: String?,
    val installed: Boolean,
    val platform: String
)

@Composable
fun ContentCard(
    item: ContentItemData,
    onDownload: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                val icon = when {
                    item.name.contains("OptiFine", ignoreCase = true) -> Icons.Filled.WbSunny
                    item.name.contains("Shader", ignoreCase = true) -> Icons.Filled.WbSunny
                    item.name.contains("Map", ignoreCase = true) -> Icons.Filled.Public
                    item.name.contains("Pack", ignoreCase = true) -> Icons.Filled.Palette
                    else -> Icons.Filled.Extension
                }
                Icon(
                    icon,
                    contentDescription = null,
                    tint = when (item.platform) {
                        "CurseForge" -> Orange40
                        "Modrinth" -> Color(0xFF1BD96A)
                        "Planet Minecraft" -> Blue40
                        else -> Green60
                    },
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Download,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatCount(item.downloadCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    item.category?.let { cat ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "| $cat",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Button(
                onClick = onDownload,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (item.installed) MaterialTheme.colorScheme.secondary else Green60
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    if (item.installed) Icons.Filled.Check else Icons.Filled.Download,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    if (item.installed) "已安装" else "下载",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

fun formatCount(count: Long): String {
    return when {
        count >= 1_000_000 -> "${count / 1_000_000}.${(count % 1_000_000) / 100_000}M"
        count >= 1_000 -> "${count / 1_000}.${(count % 1_000) / 100}K"
        else -> count.toString()
    }
}
