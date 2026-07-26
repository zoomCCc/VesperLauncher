package com.mclauncher.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val CURSEFORGE_BASE_URL = "https://api.curseforge.com/"
    private const val MODRINTH_BASE_URL = "https://api.modrinth.com/"
    private const val MINECRAFT_BASE_URL = "https://launchermeta.mojang.com/"
    private const val RESOURCES_BASE_URL = "https://resources.download.minecraft.net/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("User-Agent", "MCLauncher/1.0.0")
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val downloadOkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("User-Agent", "MCLauncher/1.0.0")
                .build()
            chain.proceed(request)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .writeTimeout(300, TimeUnit.SECONDS)
        .build()

    val curseForgeApi: CurseForgeApi by lazy {
        Retrofit.Builder()
            .baseUrl(CURSEFORGE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurseForgeApi::class.java)
    }

    val modrinthApi: ModrinthApi by lazy {
        Retrofit.Builder()
            .baseUrl(MODRINTH_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ModrinthApi::class.java)
    }

    val minecraftApi: MinecraftApi by lazy {
        Retrofit.Builder()
            .baseUrl(MINECRAFT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MinecraftApi::class.java)
    }

    val resourcesApi: MinecraftApi by lazy {
        Retrofit.Builder()
            .baseUrl(RESOURCES_BASE_URL)
            .client(downloadOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MinecraftApi::class.java)
    }

    val downloadApi: MinecraftApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://example.com/")
            .client(downloadOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MinecraftApi::class.java)
    }
}
