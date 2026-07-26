package com.mclauncher.network

import com.mclauncher.data.models.ModrinthProject
import com.mclauncher.data.models.ModrinthSearchResponse
import com.mclauncher.data.models.ModrinthVersion
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ModrinthApi {

    @GET("v2/search")
    suspend fun search(
        @Query("query") query: String? = null,
        @Query("facets") facets: String? = null,
        @Query("index") index: String? = null,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 30
    ): ModrinthSearchResponse

    @GET("v2/project/{id}")
    suspend fun getProject(
        @Path("id") id: String
    ): ModrinthProject

    @GET("v2/project/{id}/version")
    suspend fun getProjectVersions(
        @Path("id") id: String,
        @Query("loaders") loaders: String? = null,
        @Query("game_versions") gameVersions: String? = null
    ): List<ModrinthVersion>
}
