package com.mclauncher.launcher

import android.content.Context
import com.mclauncher.data.models.*
import com.mclauncher.network.ApiClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.security.MessageDigest

class VersionDownloader(private val context: Context) {

    private val gson = Gson()
    @get:JvmName("getInternalMinecraftDir")
    private val minecraftDir: File
        get() = File(context.getExternalFilesDir(null), "minecraft")

    suspend fun fetchVersionManifest(): VersionManifest {
        return withContext(Dispatchers.IO) {
            ApiClient.minecraftApi.getVersionManifest()
        }
    }

    suspend fun downloadVersion(versionId: String, onProgress: (Float) -> Unit): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val detail = ApiClient.minecraftApi.getVersionDetail(versionId)
                val versionsDir = File(minecraftDir, "versions/$versionId")
                versionsDir.mkdirs()

                val jsonFile = File(versionsDir, "$versionId.json")
                jsonFile.writeText(gson.toJson(detail))

                val clientJar = File(versionsDir, "$versionId.jar")
                if (!clientJar.exists()) {
                    detail.downloads?.client?.url?.let { url ->
                        downloadFile(url, clientJar) { progress ->
                            onProgress(progress * 0.6f)
                        }
                    }
                }

                downloadAssets(detail) { progress ->
                    onProgress(0.6f + progress * 0.3f)
                }

                downloadLibraries(detail) { progress ->
                    onProgress(0.9f + progress * 0.1f)
                }

                onProgress(1f)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private suspend fun downloadAssets(
        detail: VersionDetail,
        onProgress: (Float) -> Unit
    ) {
        detail.assetIndex?.url?.let { url ->
            val assetsDir = File(minecraftDir, "assets")
            val indexesDir = File(assetsDir, "indexes")
            indexesDir.mkdirs()

            val indexFile = File(indexesDir, "${detail.assets}.json")
            val indexResponse = withContext(Dispatchers.IO) {
                ApiClient.minecraftApi.getAssetIndex(url)
            }
            indexFile.writeText(gson.toJson(indexResponse))

            val objectsDir = File(assetsDir, "objects")
            val objects = indexResponse.objects
            var completed = 0
            val total = objects.size

            objects.entries.forEach { (_, assetObj) ->
                val hash = assetObj.hash
                val subDir = hash.substring(0, 2)
                val assetFile = File(objectsDir, "$subDir/$hash")
                if (!assetFile.exists()) {
                    try {
                        assetFile.parentFile?.mkdirs()
                        downloadFileSync(
                            "https://resources.download.minecraft.net/$subDir/$hash",
                            assetFile
                        )
                    } catch (_: Exception) {}
                }
                completed++
                onProgress(completed.toFloat() / total)
            }
        }
    }

    private suspend fun downloadLibraries(
        detail: VersionDetail,
        onProgress: (Float) -> Unit
    ) {
        val libsDir = File(minecraftDir, "libraries")
        val libraries = detail.libraries ?: return
        var completed = 0

        libraries.forEach { lib ->
            lib.downloads?.artifact?.let { artifact ->
                val libFile = File(libsDir, artifact.path ?: return@forEach)
                if (!libFile.exists()) {
                    try {
                        libFile.parentFile?.mkdirs()
                        downloadFileSync(artifact.url, libFile)
                    } catch (_: Exception) {}
                }
            }
            completed++
            onProgress(completed.toFloat() / libraries.size)
        }
    }

    private suspend fun downloadFile(
        url: String,
        dest: File,
        onProgress: (Float) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            val responseBody: ResponseBody = ApiClient.downloadApi.downloadFile(url)
            val totalBytes = responseBody.contentLength()
            var downloadedBytes = 0L

            responseBody.byteStream().use { input ->
                FileOutputStream(dest).use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead
                        if (totalBytes > 0) {
                            onProgress(downloadedBytes.toFloat() / totalBytes)
                        }
                    }
                }
            }
        }
    }

    private fun downloadFileSync(url: String, dest: File) {
        try {
            val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
            connection.connectTimeout = 30000
            connection.readTimeout = 300000
            connection.connect()

            connection.inputStream.use { input ->
                FileOutputStream(dest).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (_: Exception) {}
    }

    fun isVersionDownloaded(versionId: String): Boolean {
        val jarFile = File(minecraftDir, "versions/$versionId/$versionId.jar")
        val jsonFile = File(minecraftDir, "versions/$versionId/$versionId.json")
        return jarFile.exists() && jsonFile.exists()
    }

    fun getMinecraftDir(): File = minecraftDir

    fun getVersionsDir(): File = File(minecraftDir, "versions")

    fun getModsDir(): File = File(minecraftDir, "mods").also { it.mkdirs() }

    fun getResourcePacksDir(): File =
        File(minecraftDir, "resourcepacks").also { it.mkdirs() }

    fun getShaderPacksDir(): File =
        File(minecraftDir, "shaderpacks").also { it.mkdirs() }

    fun getSavesDir(): File = File(minecraftDir, "saves").also { it.mkdirs() }

    fun deleteVersion(versionId: String): Boolean {
        val versionDir = File(minecraftDir, "versions/$versionId")
        return versionDir.deleteRecursively()
    }
}
