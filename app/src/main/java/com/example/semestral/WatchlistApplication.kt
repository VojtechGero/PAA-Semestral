package com.example.semestral

import android.app.Application
import androidx.room.Room
import com.example.semestral.data.ShowRepository
import com.example.semestral.data.TvMazeApi
import com.example.semestral.data.WatchlistDatabase
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class WatchlistApplication : Application() {
    // One shared database/repository for both activities, without a DI framework.
    private val database by lazy {
        Room.databaseBuilder(this, WatchlistDatabase::class.java, "watchlist.db").build()
    }

    val repository by lazy {
        val client = OkHttpClient.Builder()
            .callTimeout(20, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder()
                    .header("User-Agent", "NextUp-Android-Semestral/1.0")
                    .build())
            }
            .build()
        val api = Retrofit.Builder()
            .baseUrl("https://api.tvmaze.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TvMazeApi::class.java)
        ShowRepository(database.showDao(), api)
    }

    val preferences by lazy { getSharedPreferences("watchlist_settings", MODE_PRIVATE) }
}
