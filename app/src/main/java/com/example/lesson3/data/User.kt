package com.example.lesson3.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.UUID


@Entity(
    tableName = "users",
    indices = [Index("id")]
)
data class User(
    @SerializedName("id") @PrimaryKey val id: UUID = UUID.randomUUID(),
    @SerializedName("name") var name: String = "",
    @SerializedName("surname") var surname: String? = null,
    @SerializedName("login") var login: String = "",
    @SerializedName("password") var password: String = ""
)
