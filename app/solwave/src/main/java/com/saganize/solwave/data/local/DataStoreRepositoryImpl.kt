package com.saganize.solwave.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.saganize.solwave.core.di.TAG
import com.saganize.solwave.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.first

class DataStoreRepositoryImpl(private val context: Context) : DataStoreRepository {

    companion object{
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "datastore1")
        val KEY_STRING =  stringPreferencesKey("wallet_key")
    }




    override suspend fun saveWallet(value: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_STRING] = value
        }

        Log.d(TAG+"1", value)
    }

    override suspend fun getWallet(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[KEY_STRING]
    }


}