package com.example.semestral.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvMazeApi {
    @GET("search/shows")
    suspend fun search(@Query("q") query: String): List<SearchResultDto>

    @GET("shows/{id}")
    suspend fun getShow(@Path("id") id: Int): ShowDto
}

data class SearchResultDto(val show: ShowDto)
data class ImageDto(val medium: String?, val original: String?)
data class RatingDto(val average: Double?)

// DTO = the shape of JSON returned by TVmaze. Optional fields can really be null.
data class ShowDto(
    val id: Int,
    val name: String,
    val summary: String?,
    val image: ImageDto?,
    val genres: List<String>?,
    val premiered: String?,
    val rating: RatingDto?,
    val url: String?
) {
    fun toSavedShow() = SavedShow(
        id = id,
        name = name,
        summary = summary.orEmpty(),
        imageUrl = image?.medium ?: image?.original,
        genres = genres.orEmpty().joinToString(" · "),
        premiered = premiered,
        rating = rating?.average,
        tvmazeUrl = url ?: "https://www.tvmaze.com/shows/$id"
    )
}
