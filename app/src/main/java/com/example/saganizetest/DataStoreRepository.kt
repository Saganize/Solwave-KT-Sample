package com.example.saganizetest

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking


class DataStoreRepository(private val context: Context) {

    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "datastore")
        val KEY_STRING = stringPreferencesKey("wallet_key")
    }

    fun savePublicKey(value: String) {
        return runBlocking {
            context.dataStore.edit { preferences ->
                preferences[KEY_STRING] = value
            }
        }
    }

    fun getPublicKey(): String {
        return runBlocking {
            val preferences = context.dataStore.data.firstOrNull()
            preferences?.get(KEY_STRING) ?: ""
        }
    }
}