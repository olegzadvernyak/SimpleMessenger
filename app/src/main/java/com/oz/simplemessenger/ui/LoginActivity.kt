package com.oz.simplemessenger.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) supportFragmentManager
            .beginTransaction()
            .add(android.R.id.content, LoginFragment.newInstance())
            .commit()
    }

}