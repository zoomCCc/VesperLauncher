package com.mclauncher.data.models

import com.google.gson.annotations.SerializedName

data class MinecraftVersion(
    val id: String,
    val type: String,
    @SerializedName("releaseTime") val releaseTime: String,
    val url: String? = null,
    var isDownloaded: Boolean = false,
    var downloadProgress: Float = 0f
)

data class VersionManifest(
    val latest: LatestVersion,
    val versions: List<MinecraftVersion>
)

data class LatestVersion(
    val release: String,
    val snapshot: String
)

data class VersionDetail(
    val id: String,
    val assets: String,
    @SerializedName("assetIndex") val assetIndex: AssetIndex?,
    val downloads: VersionDownloads?,
    val libraries: List<Library>?,
    @SerializedName("mainClass") val mainClass: String?,
    @SerializedName("minecraftArguments") val minecraftArguments: String?,
    val arguments: Arguments?,
    @SerializedName("javaVersion") val javaVersion: JavaVersionInfo?
)

data class AssetIndex(
    val id: String,
    val sha1: String,
    val size: Long,
    @SerializedName("totalSize") val totalSize: Long,
    val url: String
)

data class VersionDownloads(
    val client: DownloadInfo?,
    @SerializedName("client_mappings") val clientMappings: DownloadInfo?,
    val server: DownloadInfo?,
    @SerializedName("server_mappings") val serverMappings: DownloadInfo?
)

data class DownloadInfo(
    val sha1: String,
    val size: Long,
    val url: String
)

data class Library(
    val name: String,
    val downloads: LibraryDownloads?,
    val rules: List<Rule>?,
    val natives: Map<String, String>?
)

data class LibraryDownloads(
    val artifact: ArtifactInfo?,
    val classifiers: Map<String, ArtifactInfo>?
)

data class ArtifactInfo(
    val path: String?,
    val sha1: String,
    val size: Long,
    val url: String
)

data class Rule(
    val action: String,
    val os: OsRule?
)

data class OsRule(
    val name: String?,
    val arch: String?,
    val version: String?
)

data class Arguments(
    val game: List<Any>?,
    val jvm: List<Any>?
)

data class JavaVersionInfo(
    val component: String,
    @SerializedName("majorVersion") val majorVersion: Int
)

data class AssetObject(
    val hash: String,
    val size: Long
)

data class AssetIndexData(
    val objects: Map<String, AssetObject>
)
