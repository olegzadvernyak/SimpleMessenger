package com.oz.simplemessenger.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

const val EXTRA_IS_ADD_OPTION_ENABLED = "EXTRA_IS_ADD_OPTION_ENABLED"

class UserListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .add(android.R.id.content, UserListFragment.createInstance(
                isAddOptionEnabled = intent.getBooleanExtra(EXTRA_IS_ADD_OPTION_ENABLED, true)
            ))
            .commit()
    }

}