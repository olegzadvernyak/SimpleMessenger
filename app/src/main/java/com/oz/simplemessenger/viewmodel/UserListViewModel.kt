package com.oz.simplemessenger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import com.oz.simplemessenger.db.User
import com.oz.simplemessenger.db.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserListViewModel(private val userDao: UserDao) : ViewModel() {

    val users = LivePagedListBuilder(userDao.getUsers(), 20).build()

    fun deactivateUser() {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.deactivateUser()
        }
    }

    fun activateUser(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.setActiveUser(user.id)
        }
    }

}