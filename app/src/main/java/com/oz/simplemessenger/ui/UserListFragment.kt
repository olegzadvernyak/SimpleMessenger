package com.oz.simplemessenger.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.oz.simplemessenger.R
import com.oz.simplemessenger.db.User
import com.oz.simplemessenger.viewmodel.UserListViewModel
import kotlinx.android.synthetic.main.user_list_fragment.*
import org.koin.android.ext.android.inject

private const val KEY_IS_ADD_OPTION_ENABLED = "KEY_IS_ADD_OPTION_ENABLED"

class UserListFragment : Fragment() {

    private val viewModel by inject<UserListViewModel>()

    private var userAdapter: UserAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userAdapter = UserAdapter { user: User ->
            viewModel.activateUser(user)
            startActivity(
                Intent(
                    context, MainActivity::class.java
                ).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                )
            )
        }
        with(recyclerView) {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewModel.users.observe(this, Observer<PagedList<User>> { list ->
            userAdapter?.submitList(list)
        })

        arguments?.getBoolean(KEY_IS_ADD_OPTION_ENABLED)?.let { isAddOptionEnabled ->
            if (!isAddOptionEnabled) addUserButton.hide()
        }
        addUserButton.setOnClickListener {
            viewModel.deactivateUser()
        }
    }

    companion object {
        fun createInstance(isAddOptionEnabled: Boolean = true): UserListFragment {
            return UserListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(KEY_IS_ADD_OPTION_ENABLED, isAddOptionEnabled)
                }
            }
        }
    }

}