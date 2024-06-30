package com.kilabid.workoutapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name="login")
class UserPreferences private constructor(private val datastore: DataStore<Preferences>) {
    suspend fun saveSession(user: UserModel){
        datastore.edit { preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[USERNAME_KEY] = user.username
            preferences[ISLOGIN_KEY] = true
        }
    }

    fun getSession(): Flow<UserModel>{
        return datastore.data.map { preferences ->
            UserModel(
                preferences[EMAIL_KEY] ?: "",
                preferences[USERNAME_KEY] ?: "",
                preferences[ISLOGIN_KEY] ?: false
            )
        }.also{userModel ->
            println("User session retrieved: email=${userModel}")
        }

    }

    suspend fun logout() {
        datastore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object{
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val ISLOGIN_KEY = booleanPreferencesKey("isLogin")

        private var INSTANCE: UserPreferences? = null
        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}