package com.oz.simplemessenger.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.oz.simplemessenger.ConnectionTestResult
import com.oz.simplemessenger.testConnection

class LoginViewModel : ViewModel() {

    private val testParametersData = MutableLiveData<TestParameters>()

    val testResult: LiveData<ConnectionTestResult> =
        Transformations.switchMap(testParametersData) {
            with(it) {
                testConnection(
                    host,
                    port,
                    domain,
                    username,
                    password
                )
            }
        }

    fun submit(testParameters: TestParameters) {
        testParametersData.value = testParameters
    }

    data class TestParameters(
        val host: String,
        val port: Int,
        val domain: String,
        val username: String,
        val password: String
    )

}