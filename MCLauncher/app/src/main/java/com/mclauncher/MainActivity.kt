package com.mclauncher

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mclauncher.data.models.*
import com.mclauncher.launcher.MinecraftLauncher
import com.mclauncher.launcher.VersionDownloader
import com.mclauncher.network.ApiClient
import com.mclauncher.ui.navigation.Screen
import com.mclauncher.ui.screens.*
import com.mclauncher.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : ComponentActivity() {

    private lateinit var versionDownloader: VersionDownloader
    private lateinit var minecraftLauncher: MinecraftLauncher

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            Toast.makeText(this, "需要存储权限才能下载 Minecraft 文件", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        versionDownloader = VersionDownloader(this)
        minecraftLauncher = MinecraftLauncher(this)

        requestStoragePermission()

        setContent {
            MCLauncherApp(
                versionDownloader = versionDownloader,
                minecraftLauncher = minecraftLauncher
            )
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Toast.makeText(
                    this,
                    "请授予存储权限以管理 Minecraft 文件",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            val permissions = mutableListOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val ungranted = permissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }
            if (ungranted.isNotEmpty()) {
                requestPermissionLauncher.launch(ungranted.toTypedArray())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MCLauncherApp(
    versionDownloader: VersionDownloader,
    minecraftLauncher: MinecraftLauncher
) {
    var isDarkTheme by remember { mutableStateOf(ThemeManager.isDarkTheme) }
    var versions by remember { mutableStateOf<List<MinecraftVersion>>(emptyList()) }
    var versionSearchQuery by remember { mutableStateOf("") }
    var isLoadingVersions by remember { mutableStateOf(false) }

    var mainSearchQuery by remember { mutableStateOf("") }
    var selectedPlatform by remember { mutableStateOf("CurseForge") }
    var contentItems by remember { mutableStateOf<List<ContentItemData>>(emptyList()) }

    var ramAllocation by remember { mutableStateOf(2048) }
    var username by remember { mutableStateOf("Player") }
    var javaPath by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    MCLauncherTheme(darkTheme = isDarkTheme) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val screens = listOf(
                        Screen.Home to "主页",
                        Screen.Versions to "版本",
                        Screen.Mods to "模组",
                        Screen.Modpacks to "整合包",
                        Screen.ResourcePacks to "资源",
                        Screen.Shaders to "光影",
                        Screen.Worlds to "存档",
                        Screen.Settings to "设置"
                    )
                    screens.forEach { (screen, label) ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == screen.route
                        } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    screen.icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    label,
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = if (isDarkTheme) Green60 else Green40,
                                selectedTextColor = if (isDarkTheme) Green60 else Green40,
                                indicatorColor = if (isDarkTheme)
                                    Green60.copy(alpha = 0.2f)
                                else
                                    Green40.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        versions = versions,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onLaunch = { versionId ->
                            coroutineScope.launch {
                                if (versionDownloader.isVersionDownloaded(versionId)) {
                                    minecraftLauncher.launchGame(
                                        versionId = versionId,
                                        maxRam = ramAllocation
                                    )
                                } else {
                                    Toast.makeText(
                                        MCApplication.instance,
                                        "请先下载版本 $versionId",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    )
                }

                composable(Screen.Versions.route) {
                    if (isLoadingVersions) {
                        VersionManagerScreen(
                            versions = versions,
                            onDownload = { versionId ->
                                coroutineScope.launch {
                                    val idx = versions.indexOfFirst { it.id == versionId }
                                    if (idx >= 0) {
                                        val updated = versions.toMutableList()
                                        updated[idx] = updated[idx].copy(downloadProgress = 0f)
                                        versions = updated
                                    }

                                    val success = versionDownloader.downloadVersion(
                                        versionId = versionId
                                    ) { progress ->
                                        val i = versions.indexOfFirst { it.id == versionId }
                                        if (i >= 0) {
                                            val updated = versions.toMutableList()
                                            updated[i] = updated[i].copy(downloadProgress = progress)
                                            versions = updated
                                        }
                                    }

                                    if (success) {
                                        val i = versions.indexOfFirst { it.id == versionId }
                                        if (i >= 0) {
                                            val updated = versions.toMutableList()
                                            updated[i] = updated[i].copy(
                                                isDownloaded = true,
                                                downloadProgress = 1f
                                            )
                                            versions = updated
                                        }
                                        Toast.makeText(
                                            MCApplication.instance,
                                            "Minecraft $versionId 下载完成",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            MCApplication.instance,
                                            "下载失败",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            onDelete = { versionId ->
                                if (versionDownloader.deleteVersion(versionId)) {
                                    val i = versions.indexOfFirst { it.id == versionId }
                                    if (i >= 0) {
                                        val updated = versions.toMutableList()
                                        updated[i] = updated[i].copy(
                                            isDownloaded = false,
                                            downloadProgress = 0f
                                        )
                                        versions = updated
                                    }
                                }
                            },
                            onRefresh = {
                                isLoadingVersions = true
                                coroutineScope.launch {
                                    loadVersions(versionDownloader) { result ->
                                        versions = result.map { v ->
                                            v.copy(
                                                isDownloaded = versionDownloader.isVersionDownloaded(
                                                    v.id
                                                )
                                            )
                                        }
                                        isLoadingVersions = false
                                    }
                                }
                            },
                            searchQuery = versionSearchQuery,
                            onSearchQueryChange = { versionSearchQuery = it }
                        )
                    } else {
                        LaunchedEffect(Unit) {
                            isLoadingVersions = true
                            loadVersions(versionDownloader) { result ->
                                versions = result.map { v ->
                                    v.copy(
                                        isDownloaded = versionDownloader.isVersionDownloaded(v.id)
                                    )
                                }
                                isLoadingVersions = false
                            }
                        }
                    }
                }

                composable(Screen.Mods.route) {
                    ContentListScreen(
                        title = "模组",
                        icon = { Icon(Icons.Filled.Extension, null, tint = Orange40) },
                        items = contentItems,
                        platforms = listOf("CurseForge", "Modrinth", "Planet Minecraft"),
                        selectedPlatform = selectedPlatform,
                        onPlatformChange = { selectedPlatform = it },
                        searchQuery = mainSearchQuery,
                        onSearchQueryChange = { mainSearchQuery = it },
                        onRefresh = {
                            coroutineScope.launch {
                                loadContent("mods", mainSearchQuery, selectedPlatform) {
                                    contentItems = it
                                }
                            }
                        },
                        onDownload = { },
                        onInstall = { item ->
                            coroutineScope.launch {
                                downloadContent(item, versionDownloader)
                            }
                        }
                    )
                    LaunchedEffect(selectedPlatform, mainSearchQuery) {
                        loadContent("mods", mainSearchQuery, selectedPlatform) {
                            contentItems = it
                        }
                    }
                }

                composable(Screen.Modpacks.route) {
                    ContentListScreen(
                        title = "整合包",
                        icon = { Icon(Icons.Filled.Widgets, null, tint = Blue40) },
                        items = contentItems,
                        platforms = listOf("CurseForge", "Modrinth", "Planet Minecraft"),
                        selectedPlatform = selectedPlatform,
                        onPlatformChange = { selectedPlatform = it },
                        searchQuery = mainSearchQuery,
                        onSearchQueryChange = { mainSearchQuery = it },
                        onRefresh = {
                            coroutineScope.launch {
                                loadContent("modpacks", mainSearchQuery, selectedPlatform) {
                                    contentItems = it
                                }
                            }
                        },
                        onDownload = { },
                        onInstall = { item ->
                            coroutineScope.launch {
                                downloadContent(item, versionDownloader)
                            }
                        }
                    )
                    LaunchedEffect(selectedPlatform, mainSearchQuery) {
                        loadContent("modpacks", mainSearchQuery, selectedPlatform) {
                            contentItems = it
                        }
                    }
                }

                composable(Screen.ResourcePacks.route) {
                    ContentListScreen(
                        title = "资源包",
                        icon = { Icon(Icons.Filled.Palette, null, tint = Color(0xFF9C27B0)) },
                        items = contentItems,
                        platforms = listOf("CurseForge", "Modrinth", "Planet Minecraft"),
                        selectedPlatform = selectedPlatform,
                        onPlatformChange = { selectedPlatform = it },
                        searchQuery = mainSearchQuery,
                        onSearchQueryChange = { mainSearchQuery = it },
                        onRefresh = {
                            coroutineScope.launch {
                                loadContent("resourcepacks", mainSearchQuery, selectedPlatform) {
                                    contentItems = it
                                }
                            }
                        },
                        onDownload = { },
                        onInstall = { item ->
                            coroutineScope.launch {
                                downloadContent(item, versionDownloader)
                            }
                        }
                    )
                    LaunchedEffect(selectedPlatform, mainSearchQuery) {
                        loadContent("resourcepacks", mainSearchQuery, selectedPlatform) {
                            contentItems = it
                        }
                    }
                }

                composable(Screen.Shaders.route) {
                    ContentListScreen(
                        title = "光影",
                        icon = { Icon(Icons.Filled.WbSunny, null, tint = Color(0xFFFF9800)) },
                        items = contentItems,
                        platforms = listOf("CurseForge", "Modrinth", "Planet Minecraft"),
                        selectedPlatform = selectedPlatform,
                        onPlatformChange = { selectedPlatform = it },
                        searchQuery = mainSearchQuery,
                        onSearchQueryChange = { mainSearchQuery = it },
                        onRefresh = {
                            coroutineScope.launch {
                                loadContent("shaders", mainSearchQuery, selectedPlatform) {
                                    contentItems = it
                                }
                            }
                        },
                        onDownload = { },
                        onInstall = { item ->
                            coroutineScope.launch {
                                downloadContent(item, versionDownloader)
                            }
                        }
                    )
                    LaunchedEffect(selectedPlatform, mainSearchQuery) {
                        loadContent("shaders", mainSearchQuery, selectedPlatform) {
                            contentItems = it
                        }
                    }
                }

                composable(Screen.Worlds.route) {
                    ContentListScreen(
                        title = "存档",
                        icon = { Icon(Icons.Filled.Public, null, tint = Brown40) },
                        items = contentItems,
                        platforms = listOf("CurseForge", "Modrinth", "Planet Minecraft"),
                        selectedPlatform = selectedPlatform,
                        onPlatformChange = { selectedPlatform = it },
                        searchQuery = mainSearchQuery,
                        onSearchQueryChange = { mainSearchQuery = it },
                        onRefresh = {
                            coroutineScope.launch {
                                loadContent("worlds", mainSearchQuery, selectedPlatform) {
                                    contentItems = it
                                }
                            }
                        },
                        onDownload = { },
                        onInstall = { item ->
                            coroutineScope.launch {
                                downloadContent(item, versionDownloader)
                            }
                        }
                    )
                    LaunchedEffect(selectedPlatform, mainSearchQuery) {
                        loadContent("worlds", mainSearchQuery, selectedPlatform) {
                            contentItems = it
                        }
                    }
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { dark ->
                            isDarkTheme = dark
                            ThemeManager.isDarkTheme = dark
                        },
                        javaPath = javaPath,
                        onJavaPathChange = { javaPath = it },
                        ramAllocation = ramAllocation,
                        onRamChange = { ramAllocation = it },
                        gameDir = versionDownloader.getMinecraftDir().absolutePath,
                        username = username,
                        onUsernameChange = { username = it }
                    )
                }
            }
        }
    }
}

private suspend fun loadVersions(
    downloader: VersionDownloader,
    onResult: (List<MinecraftVersion>) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            val manifest = downloader.fetchVersionManifest()
            onResult(manifest.versions.filter {
                it.type == "release"
            }.take(50))
        } catch (e: Exception) {
            e.printStackTrace()
            onResult(emptyList())
        }
    }
}

private suspend fun loadContent(
    type: String,
    query: String,
    platform: String,
    onResult: (List<ContentItemData>) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            val items = mutableListOf<ContentItemData>()

            when (platform) {
                "CurseForge" -> {
                    val classId = when (type) {
                        "mods" -> 6
                        "modpacks" -> 4471
                        "resourcepacks" -> 12
                        "shaders" -> 6552
                        "worlds" -> 17
                        else -> 6
                    }
                    val response = ApiClient.curseForgeApi.searchMods(
                        gameId = 432,
                        classId = classId,
                        query = query.ifEmpty { null }
                    )
                    response.data.forEach { mod ->
                        items.add(
                            ContentItemData(
                                id = mod.id.toString(),
                                name = mod.name,
                                summary = mod.summary,
                                downloadCount = mod.downloadCount,
                                iconUrl = mod.logo?.thumbnailUrl ?: mod.logo?.url,
                                category = mod.categories?.firstOrNull()?.name,
                                installed = false,
                                platform = platform
                            )
                        )
                    }
                }

                "Modrinth" -> {
                    val facets = when (type) {
                        "mods" -> "[[\"project_type:mod\"]]"
                        "modpacks" -> "[[\"project_type:modpack\"]]"
                        "resourcepacks" -> "[[\"project_type:resourcepack\"]]"
                        "shaders" -> "[[\"categories:shader\"]]"
                        "worlds" -> "[[\"categories:world\"]]"
                        else -> "[[\"project_type:mod\"]]"
                    }
                    val response = ApiClient.modrinthApi.search(
                        query = query.ifEmpty { null },
                        facets = facets
                    )
                    response.hits.forEach { project ->
                        items.add(
                            ContentItemData(
                                id = project.projectId,
                                name = project.title,
                                summary = project.description,
                                downloadCount = project.downloads,
                                iconUrl = project.iconUrl,
                                category = project.categories?.firstOrNull(),
                                installed = false,
                                platform = platform
                            )
                        )
                    }
                }

                "Planet Minecraft" -> {
                    items.add(
                        ContentItemData(
                            id = "pmc_$type",
                            name = "Planet Minecraft 内容",
                            summary = "浏览 Planet Minecraft 上的 $type 内容",
                            downloadCount = 0,
                            iconUrl = null,
                            category = type,
                            installed = false,
                            platform = platform
                        )
                    )
                }
            }

            onResult(items)
        } catch (e: Exception) {
            e.printStackTrace()
            onResult(emptyList())
        }
    }
}

private suspend fun downloadContent(
    item: ContentItemData,
    downloader: VersionDownloader
) {
    withContext(Dispatchers.IO) {
        try {
            Toast.makeText(
                MCApplication.instance,
                "正在下载 ${item.name}...",
                Toast.LENGTH_SHORT
            ).show()
        } catch (_: Exception) {}
    }
}
