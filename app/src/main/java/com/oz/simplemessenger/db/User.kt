package com.oz.simplemessenger.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val host: String,
    val port: Int,
    val domain: String,
    val username: String,
    val password: String
)