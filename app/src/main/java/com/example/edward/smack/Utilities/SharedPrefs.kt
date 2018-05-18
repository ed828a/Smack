package com.example.edward.smack.Utilities

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.toolbox.Volley

/*
 * Created by Edward on 5/18/2018.
 */

class SharedPrefs(context: Context) {


    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(IS_LOGGED_IN, value).apply()

    var authToken: String
        get() = prefs.getString(AUTH_TOKEN, "")
        set(value) = prefs.edit().putString(AUTH_TOKEN, value).apply()

    var userEmail: String
        get() = prefs.getString(USER_EMAIL, "")
        set(value) = prefs.edit().putString(USER_EMAIL, value).apply()

    var password: String
        get() = prefs.getString(USER_PASSWORD, "")
        set(value) = prefs.edit().putString(USER_PASSWORD, value).apply()


    // this requestQueue should not be here
    val requestQueue = Volley.newRequestQueue(context)
}