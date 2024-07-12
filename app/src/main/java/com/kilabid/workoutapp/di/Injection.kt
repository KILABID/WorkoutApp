package com.kilabid.workoutapp.di

import android.content.Context
import com.kilabid.workoutapp.data.Repository
import com.kilabid.workoutapp.data.UserPreferences
import com.kilabid.workoutapp.data.datastore

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreferences.getInstance(context.datastore)

        return Repository.getInstance(pref)
    }
}