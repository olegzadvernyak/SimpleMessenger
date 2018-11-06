package com.oz.simplemessenger.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [
    User::class
])
abstract class CommonDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

}