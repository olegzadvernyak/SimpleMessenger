package com.oz.simplemessenger.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.oz.simplemessenger.ConnectionTestResult
import com.oz.simplemessenger.R
import com.oz.simplemessenger.db.User
import com.oz.simplemessenger.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.login_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val viewModel by viewModel<LoginViewModel>()

    private var otherUsersExist = true

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
            when(result.status) {
                ConnectionTestResult.Status.IN_PROGRESS -> showInProgressUI()
                ConnectionTestResult.Status.SUCCESS -> {
                    result.user?.let {
                        viewModel.createUser(it)
                    }
                }
                ConnectionTestResult.Status.CONNECTION_FAILED -> showConnectionFailedUI()
                ConnectionTestResult.Status.HOST_UNKNOWN -> showHostUnknownUI()
                ConnectionTestResult.Status.NOT_AUTHORIZED -> showNotAuthorizedUI()
                ConnectionTestResult.Status.UNDEFINED_ERROR -> showUndefinedErrorUI()
            }
        })
        viewModel.userCreated.observe(this, Observer { isCreated ->
            if (isCreated) {
                startActivity(
                    Intent(
                        context, MainActivity::class.java
                    ).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                )
            }
        })
        viewModel.userCount.observe(this, Observer { count ->
            otherUsersExist = count != 0
            if (!otherUsersExist and (chooseButton.visibility == View.VISIBLE)) chooseButton.visibility = View.GONE
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
        chooseButton.setOnClickListener {
            startActivity(
                Intent(
                    context, UserListActivity::class.java
                ).putExtra(
                    EXTRA_IS_ADD_OPTION_ENABLED, false
                )
            )
        }
    }

    private fun hideInProgressUI() {
        hostEditText.isEnabled = true
        portEditText.isEnabled = true
        domainEditText.isEnabled = true
        usernameEditText.isEnabled = true
        passwordEditText.isEnabled = true
        submitButton.visibility = View.VISIBLE
        chooseButton.visibility = if (otherUsersExist) View.VISIBLE else View.GONE
        progressBar.visibility = View.INVISIBLE
    }

    private fun showInProgressUI() {
        hostEditText.isEnabled = false
        portEditText.isEnabled = false
        domainEditText.isEnabled = false
        usernameEditText.isEnabled = false
        passwordEditText.isEnabled = false
        submitButton.visibility = View.INVISIBLE
        chooseButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun showConnectionFailedUI() {
        hideInProgressUI()
        Snackbar.make(
            submitButton,
            R.string.login_connection_failed,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showHostUnknownUI() {
        hideInProgressUI()
        domainEditText.error = getString(R.string.login_host_unknown)
    }

    private fun showNotAuthorizedUI() {
        hideInProgressUI()
        usernameEditText.error = getString(R.string.login_invalid_username_password)
        passwordEditText.error = getString(R.string.login_invalid_username_password)
    }

    private fun showUndefinedErrorUI() {
        hideInProgressUI()
        Snackbar.make(
            submitButton,
            R.string.login_undefined_error,
            Snackbar.LENGTH_SHORT
        ).show()
    }

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
            User(
                host = host,
                port = port.toInt(),
                domain = domain,
                username = username,
                password = password
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