package com.example.edward.smack.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.example.edward.smack.R
import com.example.edward.smack.Services.AuthService
import com.example.edward.smack.Services.AuthService.isLoggedIn
import com.example.edward.smack.Services.UserDataService
import com.example.edward.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
                IntentFilter(BROADCAST_USER_DATA_CHANGE))
    }

        private val userDataChangeReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if (AuthService.isLoggedIn){
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                Log.d("Image", ": ${UserDataService.avatarName}")
                val resourceId = resources.getIdentifier(UserDataService.avatarName,
                        "drawable", packageName)
                userImageNavHeader.setImageResource(resourceId)
                userImageNavHeader.setBackgroundColor(
                        UserDataService.returnAvatarColor(UserDataService.avatarColor))

                buttonLoginNavHeader.text = "Logout"
            }
        }
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun onLoginBtnNavClick(view: View) {
        if (AuthService.isLoggedIn){  // after Login, this button was clicked again means logout
            reset()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    private fun reset(){
        buttonLoginNavHeader.text = "Login"
        UserDataService.id =""
        UserDataService.avatarColor = ""
        UserDataService.avatarName = ""
        UserDataService.email = ""
        UserDataService.name = ""
        AuthService.isLoggedIn = false
        AuthService.userEnail = ""
        AuthService.userPassword = ""
        AuthService.authToken = ""
        userImageNavHeader.setImageResource(R.drawable.profiledefault)
        userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
        userEmailNavHeader.text = ""
        userNameNavHeader.text = ""
    }

    fun onAddChannelClick(view: View) {

    }

    fun onSendMessageButtonClick(view: View) {


    }
}
