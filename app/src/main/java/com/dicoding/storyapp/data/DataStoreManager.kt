package com.dicoding.storyapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dicoding.storyapp.utility.dataStore
import kotlinx.coroutines.flow.first

class DataStoreManager(context: Context) {
    private val dataStore: DataStore<Preferences> = context.dataStore

    private val STRING_KEY = stringPreferencesKey("token_key")

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[STRING_KEY] = token
        }
    }

    suspend fun getToken(): String? {
        val preferences = dataStore.data.first()
        return preferences[STRING_KEY]
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(STRING_KEY)
        }
    }
}
