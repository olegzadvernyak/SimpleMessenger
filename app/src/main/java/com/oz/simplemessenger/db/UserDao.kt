package com.oz.simplemessenger.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*

@Dao
abstract class UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(user: User): Long

    @Query("SELECT * FROM Users WHERE isActive = 1 LIMIT 1")
    abstract fun getActiveUser(): LiveData<User?>

    @Query("UPDATE Users SET isActive = 0 WHERE isActive = 1")
    abstract fun deactivateUser()

    @Query("UPDATE Users SET isActive = 1 WHERE id = :userId")
    protected abstract fun activateUser(userId: Long)

    @Transaction
    open fun setActiveUser(userId: Long) {
        deactivateUser()
        activateUser(userId)
    }

    @Query("SELECT * FROM Users")
    abstract fun getUsers(): DataSource.Factory<Int, User>

    @Query("SELECT COUNT() FROM Users")
    abstract fun getUserCount(): LiveData<Int>

}