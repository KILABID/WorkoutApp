package com.kilabid.workoutapp.ui.LoginPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kilabid.workoutapp.data.Repository
import com.kilabid.workoutapp.data.UserModel

class LoginViewModel(private val repository: Repository) : ViewModel() {
    suspend fun saveSession(user: UserModel) {
        repository.saveSession(user)
    }
    fun getSession() : LiveData<UserModel>{
        return repository.getSession().asLiveData()
    }

    suspend fun logout(){
        repository.logout()
    }
}