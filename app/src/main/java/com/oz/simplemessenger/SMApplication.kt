package com.oz.simplemessenger

import android.app.Application
import com.oz.simplemessenger.viewmodel.LoginViewModel
import org.koin.android.ext.android.startKoin
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

class SMApplication : Application() {

    private val applicationModule = module {
        viewModel { LoginViewModel() }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(applicationModule))
    }

}