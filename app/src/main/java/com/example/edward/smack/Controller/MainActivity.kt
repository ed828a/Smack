package com.example.edward.smack.Controller

import android.app.ProgressDialog.show
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.edward.smack.R
import com.example.edward.smack.R.id.*
import com.example.edward.smack.Services.AuthService
import com.example.edward.smack.Services.AuthService.isLoggedIn
import com.example.edward.smack.Services.UserDataService
import com.example.edward.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_channel_dialog.view.*
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
//        hideKeyboard()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
                IntentFilter(BROADCAST_USER_DATA_CHANGE))
    }

    override fun onStart() {
        super.onStart()
        hideKeyboard()
    }

    private val userDataChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (AuthService.isLoggedIn) {
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                println("AvatarName: ${UserDataService.avatarName}")

                val resourceId = resources.getIdentifier(UserDataService.avatarName,
                        "drawable", packageName)
                userImageNavHeader.setImageResource(resourceId)

                if (UserDataService.avatarColor == "[0.5, 0.5, 0.5, 1]") {
                    userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
                    println("AvatarColor: Color.TRANSPARENT")
                } else {
                    userImageNavHeader.setBackgroundColor(
                            UserDataService.returnAvatarColor(UserDataService.avatarColor))
                }

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
        if (AuthService.isLoggedIn) {  // after Login, this button was clicked again means logout
            reset()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    private fun reset() {
        buttonLoginNavHeader.text = "Login"
        UserDataService.id = ""
        UserDataService.avatarColor = ""
        UserDataService.avatarName = ""
        UserDataService.email = ""
        UserDataService.name = ""
        AuthService.isLoggedIn = false
        AuthService.userEnail = ""
        AuthService.userPassword = ""
        AuthService.authToken = ""
        AuthService.isLoggedIn = false
        userImageNavHeader.setImageResource(R.drawable.profiledefault)
        userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
        userEmailNavHeader.text = ""
        userNameNavHeader.text = ""
    }

    fun onAddChannelClick(view: View) {
        if (AuthService.isLoggedIn) {
            val dialogBuilder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)
            val tv = dialogBuilder.setView(dialogView)
                    .setPositiveButton("Add", DialogInterface.OnClickListener { dialog, which ->
                        //store channel name and channel description
                        val channelName = dialogView.addChannelNameText.text.toString()
                        val channelDescription = dialogView.addChannelDescriptionText.text.toString()
                        
                        //create channel with channel name and description
                        hideKeyboard()
                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                        hideKeyboard()
                    })
                    .show()

        }
    }

    fun onSendMessageButtonClick(view: View) {


    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
}
