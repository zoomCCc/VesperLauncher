package com.mclauncher.network

import com.mclauncher.data.models.AssetIndexData
import com.mclauncher.data.models.VersionDetail
import com.mclauncher.data.models.VersionManifest
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming
import retrofit2.http.Url

interface MinecraftApi {

    @GET("mc/game/version_manifest.json")
    suspend fun getVersionManifest(): VersionManifest

    @GET("v2/versions/{versionId}")
    suspend fun getVersionDetail(
        @Path("versionId") versionId: String
    ): VersionDetail

    @GET
    suspend fun getAssetIndex(@Url url: String): AssetIndexData

    @Streaming
    @GET
    suspend fun downloadFile(@Url url: String): ResponseBody
}
