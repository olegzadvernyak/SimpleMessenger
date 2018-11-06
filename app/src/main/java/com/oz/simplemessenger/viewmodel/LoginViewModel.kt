package com.oz.simplemessenger.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.oz.simplemessenger.ConnectionTestResult
import com.oz.simplemessenger.SMPreferences
import com.oz.simplemessenger.db.User
import com.oz.simplemessenger.db.UserDao
import com.oz.simplemessenger.testConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class LoginViewModel(
    private val prefs: SMPreferences,
    private val userDao: UserDao
) : ViewModel() {

    private val _userCreated = MutableLiveData<Boolean>()
    private val testUser = MutableLiveData<User>()

    val testResult: LiveData<ConnectionTestResult> =
        Transformations.switchMap(testUser) { user ->
            testConnection(user)
        }

    val userCreated: LiveData<Boolean>
        get() = _userCreated

    fun submit(user: User) {
        testUser.value = user
    }

    fun createUser(user: User) {
        runBlocking {
            val userId = async(Dispatchers.IO) { userDao.insert(user) }
            prefs.activeUserId = userId.await()
            _userCreated.value = true
        }
    }

}