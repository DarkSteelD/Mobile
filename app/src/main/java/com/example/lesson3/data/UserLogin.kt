package com.example.lesson3.data

import com.google.gson.annotations.SerializedName

data class UserLogin(
    @SerializedName("login") var login: String = "",
    @SerializedName("password") var password: String = ""
)
