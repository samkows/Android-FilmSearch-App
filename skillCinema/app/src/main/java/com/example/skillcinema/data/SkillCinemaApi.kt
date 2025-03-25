package com.example.skillcinema.data

import com.example.skillcinema.models.FullFilmDataDto
import com.example.skillcinema.models.GalleryData
import com.example.skillcinema.models.GenresAndCountriesData
import com.example.skillcinema.models.PersonData
import com.example.skillcinema.models.SeasonsData
import com.example.skillcinema.models.ShortFilmDataListDto
import com.example.skillcinema.models.StaffData
import okhttp3.internal.parseCookie
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query


private const val BASE_URL = "https://kinopoiskapiunofficial.tech"

class SkillCinemaApi {

    object RetrofitInstance {
        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val networkingApi: NetworkingApi = retrofit.create(NetworkingApi::class.java)
    }

    interface NetworkingApi {

        @Headers("X-API-KEY: $API_KEY")
        @GET("/api/v2.2/films/premieres")
        suspend fun getPremieres(
            @Query("year") year: Int,
            @Query("month") month: String
        ): ShortFilmDataListDto

        @Headers("X-API-KEY: $API_KEY")
        @GET("/api/v2.2/films/collections")
        suspend fun getPopularFilms(@Query("page") page: Int): ShortFilmDataListDto

        @Headers("X-API-KEY: $API_KEY")
        @GET("/api/v2.2/films/collections?type=TOP_250_MOVIES")
        suspend fun getTopMovies(@Query("page") page: Int): ShortFilmDataListDto

        @Headers("X-API-KEY: $API_KEY")
        @GET("/api/v2.2/films?type=TV_SERIES")
        suspend fun getSerials(@Query("page") page: Int): ShortFilmDataListDto

        @Headers("X-API-KEY: $API_KEY")
        @GET("/api/v2.2/films?type=FILM")
        suspend fun getDynamicList(
            @Query("countries") country: Long,
            @Query("genres") genre: Long,
            @Query("page") page: Int
        ): ShortFilmDataListDto

        @Headers("X-API-KEY: $API_KEY")
        @GET("/api/v2.2/films/{id}")
        suspend fun getFilmById(@Path("id") id: Long): FullFilmDataDto

        @Headers("X-API-KEY: $API_KEY")
        @GET("/api/v1/staff")
        suspend fun getStaffsFromFilm(@Query("filmId") filmId: Long): List<StaffData>

        @Headers("X-API-KEY: $API_KEY")
        @GET("/api/v2.2/films/{id}/images")
        suspend fun getGalleryData(@Path("id") id: Long): GalleryData

        @Headers("X-API-KEY: $API_KEY")
        @GET("/api/v2.2/films/{id}/images")
        suspend fun getGalleryDataByType(
            @Path("id") id: Long,
            @Query("type") type: String,
            @Query("page") page: Int
        ): GalleryData

        @Headers("X-API-KEY: $API_KEY")
        @GET("/api/v2.2/films/{id}/similars")
        suspend fun getSimilarFilms(@Path("id") id: Long): ShortFilmDataListDto

        @Headers("X-API-KEY: $API_KEY")
        @GET("/api/v2.2/films/filters")
        suspend fun getGenresAndCountries(): GenresAndCountriesData

        @Headers("X-API-KEY: $API_KEY")
        @GET("/api/v1/staff/{id}")
        suspend fun getPersonData(@Path("id") id: Long): PersonData

        @Headers("X-API-KEY: $API_KEY")
        @GET("/api/v2.2/films/{id}/seasons")
        suspend fun getSeasonsData(@Path("id") id: Long): SeasonsData

        @Headers("X-API-KEY: $API_KEY")
        @GET("/api/v2.2/films")
        suspend fun getSearchList(
            @Query("countries") country: Long,
            @Query("genres") genre: Long,
            @Query("order") order: String,
            @Query("type") type: String,
            @Query("ratingFrom") ratingFrom: Int,
            @Query("ratingTo") ratingTo: Int,
            @Query("yearFrom") yearFrom: Int,
            @Query("yearTo") yearTo: Int,
            @Query("keyword") keyword: String,
            @Query("page") page: Int
        ): ShortFilmDataListDto

        private companion object {
            private const val FIRST_KEY = "6cad969e-44ae-497c-995e-5a273a3d593c"
            private const val SECOND_KEY = "d7aefb7c-dfc6-475d-962e-0e599f50bfa6"

            private const val API_KEY = FIRST_KEY
        }
    }
}