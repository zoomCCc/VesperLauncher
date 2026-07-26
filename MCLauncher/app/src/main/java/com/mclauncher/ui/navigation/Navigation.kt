package com.mclauncher.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "主页", Icons.Filled.Home)
    object Versions : Screen("versions", "版本", Icons.Filled.Download)
    object Mods : Screen("mods", "模组", Icons.Filled.Extension)
    object Modpacks : Screen("modpacks", "整合包", Icons.Filled.Widgets)
    object ResourcePacks : Screen("resource_packs", "资源包", Icons.Filled.Palette)
    object Shaders : Screen("shaders", "光影", Icons.Filled.WbSunny)
    object Worlds : Screen("worlds", "存档", Icons.Filled.Public)
    object Settings : Screen("settings", "设置", Icons.Filled.Settings)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Versions,
    Screen.Mods,
    Screen.Modpacks,
    Screen.ResourcePacks,
    Screen.Shaders,
    Screen.Worlds,
    Screen.Settings
)
