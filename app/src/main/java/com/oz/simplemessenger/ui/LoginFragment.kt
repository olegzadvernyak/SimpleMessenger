package com.oz.simplemessenger.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.oz.simplemessenger.ConnectionTestResult
import com.oz.simplemessenger.R
import com.oz.simplemessenger.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.login_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val viewModel by viewModel<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.testResult.observe(this, Observer { result ->
            when(result) {
                ConnectionTestResult.IN_PROGRESS -> showInProgressUI()
                ConnectionTestResult.SUCCESS -> {}
                ConnectionTestResult.CONNECTION_FAILED -> showConnectionFailedUI()
                ConnectionTestResult.HOST_UNKNOWN -> showHostUnknownUI()
                ConnectionTestResult.NOT_AUTHORIZED -> showNotAuthorizedUI()
                ConnectionTestResult.UNDEFINED_ERROR -> showUndefinedErrorUI()
                else -> {}
            }
        })

        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submit()
                true
            } else {
                false
            }
        }
        submitButton.setOnClickListener { submit() }
    }

    private fun showInProgressUI() {}
    private fun showConnectionFailedUI() {}
    private fun showHostUnknownUI() {}
    private fun showNotAuthorizedUI() {}
    private fun showUndefinedErrorUI() {}

    private fun submit() {
        val host = hostEditText.text.toString()
        if (host.isBlank()) {
            hostEditText.error = getString(R.string.login_host_error)
            return
        }
        val port = portEditText.text.toString()
        if (port.isBlank()) {
            portEditText.error = getString(R.string.login_port_error)
            return
        }
        val domain = domainEditText.text.toString()
        if (domain.isBlank()) {
            domainEditText.error = getString(R.string.login_domain_error)
            return
        }
        val username = usernameEditText.text.toString()
        if (username.isBlank()) {
            usernameEditText.error = getString(R.string.login_username_error)
            return
        }
        val password = passwordEditText.text.toString()
        if (password.isBlank()) {
            passwordEditText.error = getString(R.string.login_password_error)
            return
        }
        viewModel.submit(
            LoginViewModel.TestParameters(
                host,
                port.toInt(),
                domain,
                username,
                password
            )
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

}