package com.mclauncher.data.models

import com.google.gson.annotations.SerializedName

data class Mod(
    val id: Long,
    val name: String,
    val summary: String = "",
    val description: String = "",
    val logo: ModLogo? = null,
    @SerializedName("download_count") val downloadCount: Long = 0,
    val slug: String = "",
    @SerializedName("date_created") val dateCreated: String? = null,
    @SerializedName("date_modified") val dateModified: String? = null,
    @SerializedName("latest_files") val latestFiles: List<ModFile>? = null,
    val authors: List<ModAuthor>? = null,
    val categories: List<ModCategory>? = null,
    val links: ModLinks? = null,
    val platform: String = "curseforge",
    var installed: Boolean = false
)

data class ModLogo(
    @SerializedName("thumbnail_url") val thumbnailUrl: String?,
    val url: String?
)

data class ModFile(
    val id: Long,
    @SerializedName("display_name") val displayName: String,
    @SerializedName("file_name") val fileName: String,
    @SerializedName("download_url") val downloadUrl: String?,
    @SerializedName("file_length") val fileLength: Long = 0,
    @SerializedName("game_versions") val gameVersions: List<String>? = null,
    @SerializedName("is_available") val isAvailable: Boolean = true
)

data class ModAuthor(
    val id: Long,
    val name: String,
    val url: String?
)

data class ModCategory(
    val id: Long,
    val name: String,
    val slug: String?
)

data class ModLinks(
    val websiteUrl: String?,
    val wikiUrl: String?,
    val issuesUrl: String?,
    val sourceUrl: String?
)

data class CurseForgeResponse(
    val data: List<Mod>,
    val pagination: PaginationInfo?
)

data class PaginationInfo(
    val index: Int,
    val pageSize: Int,
    val resultCount: Int,
    val totalCount: Long
)

data class ModrinthProject(
    @SerializedName("project_id") val projectId: String,
    val title: String,
    val description: String,
    @SerializedName("icon_url") val iconUrl: String?,
    val slug: String,
    @SerializedName("downloads") val downloads: Long = 0,
    val categories: List<String>? = null,
    @SerializedName("client_side") val clientSide: String = "",
    @SerializedName("server_side") val serverSide: String = "",
    @SerializedName("project_type") val projectType: String = "mod",
    val versions: List<String>? = null,
    @SerializedName("date_created") val dateCreated: String? = null,
    @SerializedName("date_modified") val dateModified: String? = null
)

data class ModrinthSearchResponse(
    val hits: List<ModrinthProject>,
    val offset: Int,
    val limit: Int,
    @SerializedName("total_hits") val totalHits: Long
)

data class ModrinthVersion(
    val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("version_number") val versionNumber: String,
    val files: List<ModrinthFile>?,
    val dependencies: List<ModrinthDependency>?,
    @SerializedName("game_versions") val gameVersions: List<String>?,
    @SerializedName("loaders") val loaders: List<String>?
)

data class ModrinthFile(
    val url: String,
    val filename: String,
    val size: Long,
    val primary: Boolean = false
)

data class ModrinthDependency(
    @SerializedName("version_id") val versionId: String?,
    @SerializedName("project_id") val projectId: String?,
    @SerializedName("dependency_type") val dependencyType: String?
)
