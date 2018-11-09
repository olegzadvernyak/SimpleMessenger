package com.oz.simplemessenger

import android.app.Service
import android.content.Intent
import androidx.lifecycle.*
import com.oz.simplemessenger.db.User
import com.oz.simplemessenger.db.UserDao
import com.oz.simplemessenger.ui.LoginActivity
import kotlinx.coroutines.*
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.SmackConfiguration
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.koin.android.ext.android.inject

private const val STOP_DELAY = 30_000L

class MessagingService : LifecycleService(), LifecycleObserver {

    init {
        SmackConfiguration.DEBUG = BuildConfig.DEBUG
    }

    private val userDao by inject<UserDao>()

    private var delayedStopJob: Job? = null

    private var connection: XMPPTCPConnection? = null

    private val activeUserObserver = Observer<User?> { user ->
        connection?.disconnect()
        connection = null
        if(user == null) {
            startActivity(
                Intent(
                    this@MessagingService, LoginActivity::class.java
                ).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                )
            )
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                connection = XMPPTCPConnection(
                    XMPPTCPConnectionConfiguration.builder()
                        .setHost(user.host)
                        .setPort(user.port)
                        .setXmppDomain(user.domain)
                        .setUsernameAndPassword(user.username, user.password)
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setSendPresence(false)
                        .build()
                ).apply {
                    connect().login()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        userDao.getActiveUser().observe(this, activeUserObserver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        delayedStopJob?.cancel()
        return Service.START_NOT_STICKY
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onApplicationBackground() {
        delayedStopJob = CoroutineScope(Dispatchers.IO).launch {
            delay(STOP_DELAY)
            connection?.disconnect()
            connection = null
            ProcessLifecycleOwner.get().lifecycle.removeObserver(this@MessagingService)
            stopSelf()
        }
    }

}