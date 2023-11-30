package com.saganize.solwave.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.saganize.solwave.core.util.BackendEndpoints.BASE_URL
import com.saganize.solwave.data.local.DataStoreRepositoryImpl
import com.saganize.solwave.data.local.DatabaseRepositoryImpl
import com.saganize.solwave.data.local.SolwaveDatabase
import com.saganize.solwave.data.remote.ApiRepositoryImpl
import com.saganize.solwave.data.remote.SolwaveAPI
import com.saganize.solwave.domain.repository.ApiRepository
import com.saganize.solwave.domain.repository.DataStoreRepository
import com.saganize.solwave.domain.repository.DatabaseRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


const val TAG = "SOLWAVEAPPTAG"

interface SolwaveAppModule {
    val database: SolwaveDatabase
    val databaseRepository: DatabaseRepository
    val api: SolwaveAPI
    val apiRepo: ApiRepository
    val datastoreRepository: DataStoreRepository
//val usecases: UseCases
}

class SolwaveAppModuleImpl(
    private val appContext: Context,
    private val apiKey: String,
) : SolwaveAppModule {

    // database
    override val database: SolwaveDatabase by lazy {
        Room.databaseBuilder(
            appContext,
            SolwaveDatabase::class.java,
            SolwaveDatabase.Database_Name
        ).build()
    }
    override val databaseRepository: DatabaseRepository by lazy {
        DatabaseRepositoryImpl(database.SolwaveDao)
    }

    override val api: SolwaveAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SolwaveAPI::class.java)
    }

    override val apiRepo: ApiRepository by lazy {
        ApiRepositoryImpl(api, apiKey)
    }


    override val datastoreRepository: DataStoreRepository by lazy {
        DataStoreRepositoryImpl(appContext)
    }

}
