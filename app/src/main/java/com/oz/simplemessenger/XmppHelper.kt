package com.oz.simplemessenger

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

fun testConnection(
    host: String,
    port: Int,
    domain: String,
    username: String,
    password: String
): LiveData<ConnectionTestResult> {
    val resultData = MutableLiveData<ConnectionTestResult>()
    resultData.postValue(ConnectionTestResult.IN_PROGRESS)
    CoroutineScope(Dispatchers.IO).launch {
        XMPPTCPConnection(
            XMPPTCPConnectionConfiguration.builder()
                .setHost(host)
                .setPort(port)
                .setXmppDomain(domain)
                .setUsernameAndPassword(username, password)
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
                    resultData.postValue(ConnectionTestResult.SUCCESS)
                    disconnect()
                }
            })
            try {
                connect().login()
            } catch (e: Exception) {
                when(e) {
                    is SmackException.ConnectionException -> resultData.postValue(ConnectionTestResult.CONNECTION_FAILED)
                    is XMPPException.StreamErrorException -> when(e.streamError.condition) {
                        StreamError.Condition.host_unknown -> resultData.postValue(ConnectionTestResult.HOST_UNKNOWN)
                        else -> resultData.postValue(ConnectionTestResult.UNDEFINED_ERROR)
                    }
                    is SASLErrorException -> when(e.saslFailure.saslError) {
                        SASLError.not_authorized -> resultData.postValue(ConnectionTestResult.NOT_AUTHORIZED)
                        else -> resultData.postValue(ConnectionTestResult.UNDEFINED_ERROR)
                    }
                    is SmackException.NotConnectedException -> {}
                    else -> resultData.postValue(ConnectionTestResult.UNDEFINED_ERROR)
                }
            }
        }
    }
    return resultData
}

enum class ConnectionTestResult {
    IN_PROGRESS, SUCCESS, CONNECTION_FAILED, HOST_UNKNOWN, NOT_AUTHORIZED, UNDEFINED_ERROR
}