package com.oz.simplemessenger

import android.app.Application
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.room.Room
import com.oz.simplemessenger.db.CommonDatabase
import com.oz.simplemessenger.ui.LoginActivity
import com.oz.simplemessenger.viewmodel.LoginViewModel
import org.koin.android.ext.android.startKoin
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.koin.standalone.KoinComponent
import org.koin.standalone.getKoin
import org.koin.standalone.inject

const val SCOPE_USER = "SCOPE_USER"

class SMApplication : Application(), LifecycleObserver, KoinComponent {

    private val applicationModule = module {
        viewModel { LoginViewModel(get(), get()) }
        single {
            Room.databaseBuilder(
                androidContext(),
                CommonDatabase::class.java,
                "common_db"
            ).build()
        }
        single { get<CommonDatabase>().userDao() }
        single { SMPreferences(androidContext()) }
    }

    private val prefs: SMPreferences by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(applicationModule))

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onApplicationInForeground() {
        if (prefs.activeUserId == null) {
            startActivity(
                Intent(
                    this, LoginActivity::class.java
                ).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                )
            )
        } else {
            getKoin().createScope(SCOPE_USER)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onApplicationBackground() {
        getKoin().getOrCreateScope(SCOPE_USER).close()
    }

}