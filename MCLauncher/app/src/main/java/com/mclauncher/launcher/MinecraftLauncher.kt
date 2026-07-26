package com.mclauncher.launcher

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.content.FileProvider
import com.mclauncher.data.models.VersionDetail
import com.google.gson.Gson
import java.io.File

class MinecraftLauncher(private val context: Context) {

    private val gson = Gson()
    private val minecraftDir: File
        get() = File(context.getExternalFilesDir(null), "minecraft")

    fun launchGame(versionId: String, maxRam: Int = 2048): Boolean {
        return try {
            val versionDir = File(minecraftDir, "versions/$versionId")
            val jsonFile = File(versionDir, "$versionId.json")
            if (!jsonFile.exists()) return false

            val detail = gson.fromJson(jsonFile.readText(), VersionDetail::class.java)

            val launchArgs = buildLaunchArgs(detail, versionId, maxRam)

            val process = ProcessBuilder(launchArgs)
                .directory(minecraftDir)
                .environment().apply {
                    put("HOME", context.getExternalFilesDir(null)?.absolutePath ?: "")
                }
                .let {
                    ProcessBuilder(launchArgs)
                        .directory(minecraftDir)
                        .redirectErrorStream(true)
                        .start()
                }

            Thread {
                process.inputStream.bufferedReader().use { reader ->
                    reader.lines().forEach { line ->
                        android.util.Log.d("Minecraft", line)
                    }
                }
            }.start()

            Toast.makeText(context, "正在启动 Minecraft $versionId...", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            android.util.Log.e("MCLauncher", "启动失败", e)
            Toast.makeText(context, "启动失败: ${e.message}", Toast.LENGTH_LONG).show()
            false
        }
    }

    private fun buildLaunchArgs(
        detail: VersionDetail,
        versionId: String,
        maxRam: Int
    ): List<String> {
        val args = mutableListOf<String>()

        args.add("java")

        args.add("-Xmx${maxRam}M")
        args.add("-Xms256M")

        args.add("-Djava.library.path=${minecraftDir.absolutePath}/versions/$versionId/natives")

        val classpath = buildClasspath(detail, versionId)
        args.add("-cp")
        args.add(classpath)

        args.add(detail.mainClass ?: "net.minecraft.client.main.Main")

        val gameArgs = buildGameArgs(detail, versionId)
        args.addAll(gameArgs)

        return args
    }

    private fun buildClasspath(detail: VersionDetail, versionId: String): String {
        val cp = mutableListOf<String>()

        cp.add("versions/$versionId/$versionId.jar")

        detail.libraries?.forEach { lib ->
            lib.downloads?.artifact?.path?.let { path ->
                cp.add("libraries/$path")
            }
        }

        return cp.joinToString(File.pathSeparator)
    }

    private fun buildGameArgs(detail: VersionDetail, versionId: String): List<String> {
        val args = mutableListOf<String>()

        args.add("--username")
        args.add("Player")

        args.add("--version")
        args.add(versionId)

        args.add("--gameDir")
        args.add(minecraftDir.absolutePath)

        args.add("--assetsDir")
        args.add(File(minecraftDir, "assets").absolutePath)

        args.add("--assetIndex")
        args.add(detail.assets)

        args.add("--accessToken")
        args.add("0")

        args.add("--userType")
        args.add("mojang")

        args.add("--versionType")
        args.add("release")

        return args
    }

    fun getJavaPath(): String = "java"
}
