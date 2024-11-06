package com.example.lesson3.fragments

import androidx.lifecycle.ViewModel
import com.example.lesson3.data.User
import com.example.lesson3.data.UserLogin
import com.example.lesson3.repository.AppRepository

class AuthViewModel: ViewModel() {
    fun registration(user: User) {
        AppRepository.getInstance().registration(user)
    }

    fun login(user: UserLogin) {
        AppRepository.getInstance().login(user)
    }
}