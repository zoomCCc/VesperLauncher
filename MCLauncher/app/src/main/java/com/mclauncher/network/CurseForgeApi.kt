package com.mclauncher.network

import com.mclauncher.data.models.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CurseForgeApi {

    @GET("v1/mods/search")
    suspend fun searchMods(
        @Query("gameId") gameId: Int = 432,
        @Query("classId") classId: Int? = null,
        @Query("categoryId") categoryId: Int? = null,
        @Query("searchFilter") query: String? = null,
        @Query("sortField") sortField: Int = 2,
        @Query("sortOrder") sortOrder: String = "desc",
        @Query("index") index: Int = 0,
        @Query("pageSize") pageSize: Int = 30,
        @Query("gameVersion") gameVersion: String? = null
    ): CurseForgeResponse

    @GET("v1/mods/{modId}")
    suspend fun getMod(
        @Path("modId") modId: Long
    ): CurseForgeModDetailResponse

    @GET("v1/mods/{modId}/files")
    suspend fun getModFiles(
        @Path("modId") modId: Long,
        @Query("index") index: Int = 0,
        @Query("pageSize") pageSize: Int = 50
    ): CurseForgeFilesResponse

    @GET("v1/mods/search")
    suspend fun searchModpacks(
        @Query("gameId") gameId: Int = 432,
        @Query("classId") classId: Int = 4471,
        @Query("searchFilter") query: String? = null,
        @Query("sortField") sortField: Int = 2,
        @Query("sortOrder") sortOrder: String = "desc",
        @Query("index") index: Int = 0,
        @Query("pageSize") pageSize: Int = 30
    ): CurseForgeResponse

    @GET("v1/mods/search")
    suspend fun searchResourcePacks(
        @Query("gameId") gameId: Int = 432,
        @Query("classId") classId: Int = 12,
        @Query("searchFilter") query: String? = null,
        @Query("sortField") sortField: Int = 2,
        @Query("sortOrder") sortOrder: String = "desc",
        @Query("index") index: Int = 0,
        @Query("pageSize") pageSize: Int = 30
    ): CurseForgeResponse

    @GET("v1/mods/search")
    suspend fun searchWorlds(
        @Query("gameId") gameId: Int = 432,
        @Query("classId") classId: Int = 17,
        @Query("searchFilter") query: String? = null,
        @Query("sortField") sortField: Int = 2,
        @Query("sortOrder") sortOrder: String = "desc",
        @Query("index") index: Int = 0,
        @Query("pageSize") pageSize: Int = 30
    ): CurseForgeResponse

    @GET("v1/mods/search")
    suspend fun searchShaders(
        @Query("gameId") gameId: Int = 432,
        @Query("classId") classId: Int = 6552,
        @Query("searchFilter") query: String? = null,
        @Query("sortField") sortField: Int = 2,
        @Query("sortOrder") sortOrder: String = "desc",
        @Query("index") index: Int = 0,
        @Query("pageSize") pageSize: Int = 30
    ): CurseForgeResponse

    @GET("v1/mods/{modId}/files/{fileId}/download-url")
    suspend fun getDownloadUrl(
        @Path("modId") modId: Long,
        @Path("fileId") fileId: Long
    ): CurseForgeDownloadUrlResponse
}

data class CurseForgeModDetailResponse(
    val data: Mod
)

data class CurseForgeFilesResponse(
    val data: List<ModFile>,
    val pagination: PaginationInfo?
)

data class CurseForgeDownloadUrlResponse(
    val data: String
)
