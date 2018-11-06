package com.oz.simplemessenger

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.oz.simplemessenger.db.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jivesoftware.smack.*
import org.jivesoftware.smack.packet.StreamError
import org.jivesoftware.smack.sasl.SASLError
import org.jivesoftware.smack.sasl.SASLErrorException
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import java.lang.Exception

fun testConnection(user: User): LiveData<ConnectionTestResult> {
    val resultData = MutableLiveData<ConnectionTestResult>()
    resultData.postValue(ConnectionTestResult.inProgress())
    CoroutineScope(Dispatchers.IO).launch {
        XMPPTCPConnection(
            XMPPTCPConnectionConfiguration.builder()
                .setHost(user.host)
                .setPort(user.port)
                .setXmppDomain(user.domain)
                .setUsernameAndPassword(user.username, user.password)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setSendPresence(false)
                .build()
        ).apply {
            addConnectionListener(object : ConnectionListener {
                override fun connected(connection: XMPPConnection?) {
                }

                override fun connectionClosed() {
                }

                override fun connectionClosedOnError(e: Exception?) {
                }

                override fun authenticated(connection: XMPPConnection?, resumed: Boolean) {
                    disconnect()
                    resultData.postValue(ConnectionTestResult.success(user))
                }
            })
            try {
                connect().login()
            } catch (e: Exception) {
                when(e) {
                    is SmackException.ConnectionException -> resultData.postValue(ConnectionTestResult.connectionFailed())
                    is XMPPException.StreamErrorException -> when(e.streamError.condition) {
                        StreamError.Condition.host_unknown -> resultData.postValue(ConnectionTestResult.hostUnknown())
                        else -> resultData.postValue(ConnectionTestResult.undefinedError())
                    }
                    is SASLErrorException -> when(e.saslFailure.saslError) {
                        SASLError.not_authorized -> resultData.postValue(ConnectionTestResult.notAuthorized())
                        else -> resultData.postValue(ConnectionTestResult.undefinedError())
                    }
                    is SmackException.NotConnectedException -> {}
                    else -> resultData.postValue(ConnectionTestResult.undefinedError())
                }
            }
        }
    }
    return resultData
}

data class ConnectionTestResult(
    val status: Status,
    val user: User? = null
) {
    enum class Status {
        IN_PROGRESS, SUCCESS, CONNECTION_FAILED, HOST_UNKNOWN, NOT_AUTHORIZED, UNDEFINED_ERROR
    }

    companion object {
        fun inProgress() = ConnectionTestResult(status = Status.IN_PROGRESS)
        fun success(user: User) = ConnectionTestResult(status = Status.SUCCESS, user = user)
        fun connectionFailed() = ConnectionTestResult(status = Status.CONNECTION_FAILED)
        fun hostUnknown() = ConnectionTestResult(status = Status.HOST_UNKNOWN)
        fun notAuthorized() = ConnectionTestResult(status = Status.NOT_AUTHORIZED)
        fun undefinedError() = ConnectionTestResult(status = Status.UNDEFINED_ERROR)
    }
}