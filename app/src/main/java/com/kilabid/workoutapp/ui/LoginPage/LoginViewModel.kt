package com.kilabid.workoutapp.ui.LoginPage

import androidx.lifecycle.ViewModel
import com.kilabid.workoutapp.data.Repository
import com.kilabid.workoutapp.data.UserModel

class LoginViewModel(private val repository: Repository) : ViewModel() {
    suspend fun saveSession(user: UserModel) {
        repository.saveSession(user)
    }
}