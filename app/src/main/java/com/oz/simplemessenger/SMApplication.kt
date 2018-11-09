package com.oz.simplemessenger

import android.app.Application
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.room.Room
import com.oz.simplemessenger.db.CommonDatabase
import com.oz.simplemessenger.viewmodel.LoginViewModel
import com.oz.simplemessenger.viewmodel.UserListViewModel
import org.koin.android.ext.android.startKoin
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

class SMApplication : Application(), LifecycleObserver {

    private val applicationModule = module {
        viewModel { LoginViewModel(get()) }
        viewModel { UserListViewModel(get()) }
        single {
            Room.databaseBuilder(
                androidContext(),
                CommonDatabase::class.java,
                "common_db"
            ).build()
        }
        single { get<CommonDatabase>().userDao() }
    }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        startKoin(this, listOf(applicationModule))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onApplicationForeground() {
        startService(Intent(this, MessagingService::class.java))
    }

}