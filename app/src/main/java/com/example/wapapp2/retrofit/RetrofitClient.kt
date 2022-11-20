package com.example.wapapp2.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import java.util.concurrent.Executors

class RetrofitClient {
    companion object {
        var retrofit: Retrofit? = null
        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()

        private const val FCM_BASE_URL = "https://fcm.googleapis.com/"
        const val FCM_SERVER_KEY =
                "AAAAsKrkbSQ:APA91bE4ZSncVirIXX1c1gmKT03Qm4zUYPpcbykR7nA1LebDzfEfLQknVu0BR-Ot6VhcaJOLsQXVvDsjqVCp9PHwpaP_clstgz0GqSRryVoDgCPyNxbTwpHkbMC2gL50vJgZzH71cKW5"
        const val FCM_CONTENT_TYPE = "application/json"

        fun getClients(): RetrofitQueries {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            clientBuilder.addInterceptor(loggingInterceptor)

            if (retrofit == null)
                retrofit = Retrofit.Builder().baseUrl(FCM_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(clientBuilder.build()).build()


            return retrofit!!.create(RetrofitQueries::class.java)
        }
    }
}