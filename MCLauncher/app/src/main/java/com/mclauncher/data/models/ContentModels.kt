package com.mclauncher.data.models

data class Modpack(
    val id: Long,
    val name: String,
    val summary: String = "",
    val description: String = "",
    val logo: ModLogo? = null,
    val downloadCount: Long = 0,
    val slug: String = "",
    val dateModified: String? = null,
    val authors: List<ModAuthor>? = null,
    val platform: String = "curseforge",
    var installed: Boolean = false,
    var lastVersionId: Long = 0
)

data class ResourcePack(
    val id: Long,
    val name: String,
    val summary: String = "",
    val description: String = "",
    val logo: ModLogo? = null,
    val downloadCount: Long = 0,
    val slug: String = "",
    val dateModified: String? = null,
    val authors: List<ModAuthor>? = null,
    val platform: String = "curseforge",
    var installed: Boolean = false
)

data class Shader(
    val id: Long,
    val name: String,
    val summary: String = "",
    val description: String = "",
    val logo: ModLogo? = null,
    val downloadCount: Long = 0,
    val slug: String = "",
    val dateModified: String? = null,
    val authors: List<ModAuthor>? = null,
    val platform: String = "curseforge",
    var installed: Boolean = false
)

data class World(
    val id: Long,
    val name: String,
    val summary: String = "",
    val description: String = "",
    val logo: ModLogo? = null,
    val downloadCount: Long = 0,
    val slug: String = "",
    val dateModified: String? = null,
    val authors: List<ModAuthor>? = null,
    val platform: String = "curseforge",
    var installed: Boolean = false
)
