package com.example.edward.smack

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun onLoginActivityLoginButtonClick(view: View){

    }

    fun onLoginCreateUserButtonClick(view: View){
        val intent = Intent(this, CreateUserActivity::class.java)
        startActivity(intent)
    }
}
