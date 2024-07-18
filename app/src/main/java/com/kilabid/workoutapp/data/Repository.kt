package com.kilabid.workoutapp.data

import kotlinx.coroutines.flow.Flow

class Repository private constructor(
    private val userPreferences: UserPreferences,
) {
    suspend fun saveSession(user: UserModel) {
        userPreferences.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreferences.getSession()
    }

    suspend fun logout() {
        userPreferences.logout()
    }
    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(
            userPreferences: UserPreferences,
        ): Repository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Repository(userPreferences)
            }.also { INSTANCE = it }
    }
}