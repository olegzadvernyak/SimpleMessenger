package com.oz.simplemessenger.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.oz.simplemessenger.ConnectionTestResult
import com.oz.simplemessenger.db.User
import com.oz.simplemessenger.db.UserDao
import com.oz.simplemessenger.testConnection
import kotlinx.coroutines.*

class LoginViewModel(
    private val userDao: UserDao
) : ViewModel() {

    private val _userCreated = MutableLiveData<Boolean>()
    private val testUser = MutableLiveData<User>()

    val userCount = userDao.getUserCount()

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
        CoroutineScope(Dispatchers.IO).launch {
            userDao.setActiveUser(userDao.insert(user))
            _userCreated.postValue(true)
        }
    }

}