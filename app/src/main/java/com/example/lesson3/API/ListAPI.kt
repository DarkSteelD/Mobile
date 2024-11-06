package com.example.lesson3.API

import com.example.lesson3.data.OlympicGame
import com.example.lesson3.data.OlympicPlayer
import com.example.lesson3.data.CompetitionType
import com.example.lesson3.data.User
import com.example.lesson3.data.UserLogin
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ListAPI{
    @GET("olympic/games")
    fun getDepots(): Call<List<OlympicGame>>

    @Headers("Content-Type: application/json")
    @POST("olympic/games")
    fun postDepot(@Body requestData: OlympicGame): Call<PostResult>

    @Headers("Content-Type: application/json")
    @PUT("olympic/games")
    fun updateDepot(@Body requestData: OlympicGame): Call<PostResult>

    @DELETE("olympic/games/{game_id}")
    fun deleteDepot(@Path("game_id") gameId: String): Call<PostResult>

    @GET("olympic/{game_id}/competitions")
    fun getRoutes(@Path("game_id") gameId: String): Call<List<CompetitionType>>

    @Headers("Content-Type: application/json")
    @POST("olympic/competitions")
    fun postRoute(@Body requestData: CompetitionType): Call<PostResult>

    @Headers("Content-Type: application/json")
    @PUT("olympic/competitions")
    fun updateRoute(
        @Body requestData: CompetitionType
    ): Call<PostResult>

    @DELETE("olympic/competitions/{competition_id}")
    fun deleteRoute(@Path("competition_id") competitionId: String): Call<PostResult>
    
    @GET("olympic/{game_id}/{competition_id}")
    fun getTrams(
        @Path("game_id") gameId: String,
        @Path("competition_id") competitionId: String
    ): Call<List<OlympicPlayer>>

    @Headers("Content-Type: application/json")
    @POST("olympic/competitions/{competition_id}")
    fun postTram(
        @Path("competition_id") competitionId: String,
        @Body olympicPlayer: OlympicPlayer
    ): Call<PostResult>

    @Headers("Content-Type: application/json")
    @PUT("olympic/competitions/{competition_id}")
    fun updateTram(
        @Path("competition_id") competitionId: String,
        @Body olympicPlayer: OlympicPlayer
    ): Call<PostResult>

    @DELETE("olympic/competitions/{competition_id}/{player_id}")
    fun deleteTram(
        @Path("competition_id") competitionId: String,
        @Path("player_id") playerId: String
    ): Call<PostResult>


    @Headers("Content-Type: application/json")
    @POST("auth/registration")
    fun registration(@Body user: User): Call<PostResult>

    @Headers("Content-Type: application/json")
    @POST("auth/login")
    fun login(@Body user: UserLogin): Call<PostResult>
}