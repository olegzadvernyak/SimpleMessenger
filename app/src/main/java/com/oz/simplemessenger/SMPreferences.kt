package com.oz.simplemessenger

import android.content.Context
import androidx.core.content.edit

private const val PREFERENCES_NAME = "SIMPLE_MESSENGER_PREFERENCES"

private const val KEY_ACTIVE_USER_ID = "KEY_ACTIVE_USER_ID"

class SMPreferences(context: Context) {

    private val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    var activeUserId: Long?
        get() {
            val id = prefs.getLong(KEY_ACTIVE_USER_ID, -1L)
            return if (id == -1L) null else id
        }
        set(value) {
            prefs.edit { putLong(KEY_ACTIVE_USER_ID, value ?: -1L) }
        }

    fun clearActiveUserId() {
        activeUserId = null
    }

}