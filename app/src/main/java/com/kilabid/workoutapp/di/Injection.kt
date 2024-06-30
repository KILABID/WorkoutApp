package com.kilabid.workoutapp.di

import android.content.Context
import com.kilabid.workoutapp.data.Repository
import com.kilabid.workoutapp.data.UserPreferences
import com.kilabid.workoutapp.data.datastore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreferences.getInstance(context.datastore)
        val user = runBlocking { pref.getSession().first() }

        return Repository.getInstance(pref)
    }
}