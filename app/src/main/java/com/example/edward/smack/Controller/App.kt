package com.example.edward.smack.Controller

import android.app.Application
import com.example.edward.smack.Utilities.SharedPrefs

/*
 * Created by Edward on 5/18/2018.
 */

class App: Application() {

    companion object {
        lateinit var sharedPreferences: SharedPrefs
    }

    override fun onCreate() {
        sharedPreferences = SharedPrefs(applicationContext)
        super.onCreate()
    }
}