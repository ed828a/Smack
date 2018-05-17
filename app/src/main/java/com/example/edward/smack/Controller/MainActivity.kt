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
import android.widget.ArrayAdapter
import com.example.edward.smack.Model.Channel
import com.example.edward.smack.R
import com.example.edward.smack.R.id.*
import com.example.edward.smack.Services.AuthService
import com.example.edward.smack.Services.AuthService.isLoggedIn
import com.example.edward.smack.Services.MessageService
import com.example.edward.smack.Services.UserDataService
import com.example.edward.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import com.example.edward.smack.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_channel_dialog.view.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var listViewAdapter: ArrayAdapter<Channel>

    private fun setupListViewAdapter(){
        listViewAdapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = listViewAdapter
        val v = channel_list.adapter

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        setupListViewAdapter()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
                IntentFilter(BROADCAST_USER_DATA_CHANGE))
        socket.connect()
        socket.on("channelCreated", onNewChannel)


    }

    override fun onResume() {
        super.onResume()
//        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
//                IntentFilter(BROADCAST_USER_DATA_CHANGE))
//        socket.connect()

    }


    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        socket.disconnect()
        super.onDestroy()
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

                if (context != null) {
                    MessageService.getChannels(context) { complete ->
                        if (complete){
                            listViewAdapter.notifyDataSetChanged()
                            // listAdapter doesn't have this function,
                            // so we need listViewAdpater(ArrayAdapter) as middle variable
                            // this function tell listview to reload data.
                        }
                    }
                }
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
                        socket.emit("newChannel", channelName, channelDescription)

                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->

                    })
                    .show()

        }
    }

    fun onSendMessageButtonClick(view: View) {

        hideKeyboard()
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

    /**
     * this callback performs on a worker or background thread.
     */
    private val onNewChannel = Emitter.Listener { args: Array<out Any>? ->
        runOnUiThread {
            if (args != null) {
                val channelName = args[0] as String
                val channelDescription = args[1] as String
                val channelId = args[2] as String

                val newChannel = Channel(channelName,channelDescription,channelId)
                MessageService.channels.add(newChannel)

                listViewAdapter.notifyDataSetChanged()
            }
        }

    }

}
