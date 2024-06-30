package com.kilabid.workoutapp.data

class Repository private constructor(
    private val userPreferences: UserPreferences,
) {
    suspend fun saveSession(user: UserModel) {
        userPreferences.saveSession(user)
    }

    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun clearInstance() {
            INSTANCE = null
        }

        fun getInstance(
            userPreferences: UserPreferences,
        ): Repository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Repository(userPreferences)
            }.also { INSTANCE = it }
    }
}