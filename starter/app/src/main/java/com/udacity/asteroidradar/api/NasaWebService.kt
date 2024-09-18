package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private val moshi = Moshi
    .Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val retrofitNwo = Retrofit
    .Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(Constants.BASE_URL)
    .build()

private val retrofitApod = Retrofit
    .Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(Constants.BASE_URL)
    .build()

interface NwoWebService {
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): String
}

interface ApodWebService {
    @GET("planetary/apod")
    suspend fun getPicOfTheDay(@Query("api_key") apiKey: String = Constants.API_KEY): Response<PictureOfDay>

}

object ApodApi {
    val retrofitService: ApodWebService by lazy {
        retrofitApod.create(ApodWebService::class.java)
    }
}

object NwoApi {
    val retrofitService: NwoWebService by lazy {
        retrofitNwo.create(NwoWebService::class.java)
    }
}