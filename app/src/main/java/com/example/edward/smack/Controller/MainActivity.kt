package com.example.edward.smack.Controller

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.edward.smack.Adapter.MessageAdapter
import com.example.edward.smack.Model.Channel
import com.example.edward.smack.Model.Message
import com.example.edward.smack.R
import com.example.edward.smack.Services.AuthService
import com.example.edward.smack.Services.MessageService
import com.example.edward.smack.Services.UserDataService
import com.example.edward.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import com.example.edward.smack.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_channel_dialog.view.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var listViewAdapter: ArrayAdapter<Channel>
    var selectedChannel: Channel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        setupListViewAdapter()
        setupMessageListView()
        channel_list.setOnItemClickListener { parent, view, position, id ->
            selectedChannel = MessageService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
                IntentFilter(BROADCAST_USER_DATA_CHANGE))
        socket.connect()
        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated", onNewMessage)
        if (App.sharedPreferences.isLoggedIn){
            AuthService.findUserByEmail(this){
                // nothing need to do
            }
        }


    }

    private fun setupListViewAdapter(){
        listViewAdapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = listViewAdapter
    }

    private fun setupMessageListView(){
        messageListView.adapter = MessageAdapter(this, MessageService.messages)

        messageListView.layoutManager = LinearLayoutManager(this)
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
            if (App.sharedPreferences.isLoggedIn) {
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                println("AvatarName: ${UserDataService.avatarName}")

                val resourceId = resources.getIdentifier(UserDataService.avatarName,
                        "drawable", packageName)
                userImageNavHeader.setImageResource(resourceId)

                println("color: ${UserDataService.avatarColor}")
                if (UserDataService.avatarColor == "[0.5, 0.5, 0.5, 1]") {
                    userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
                    println("AvatarColor: Color.TRANSPARENT")
                } else {
                    userImageNavHeader.setBackgroundColor(
                            UserDataService.returnAvatarColor(UserDataService.avatarColor))
                }

                buttonLoginNavHeader.text = "Logout"

                if (context != null) {
                    MessageService.getChannels { complete ->
                        if (complete){
                            if (MessageService.channels.count() > 0){
                                // set the default selectedChannel
                                selectedChannel = MessageService.channels[0]
                                listViewAdapter.notifyDataSetChanged()
                                // listAdapter doesn't have this function,
                                // so we need listViewAdpater(ArrayAdapter) as middle variable
                                // this function tell listview to reload data.

                                updateWithChannel()
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateWithChannel(){
        mainChannelName.text = "#${selectedChannel?.name}"
        // download messages for channel
        if(selectedChannel != null) {
            MessageService.getMessages(selectedChannel!!.id) { complete ->
                if (complete){
                    // notify recyclerView adapter that data changed.
                    messageListView.adapter.notifyDataSetChanged()

                    // to display the last message in the messages
                    if (messageListView.adapter.itemCount > 0){
                        messageListView.smoothScrollToPosition(
                                messageListView.adapter.itemCount - 1)
                    }
                } else {
                    Toast.makeText(this, "Something went to Wrong, please try again!",
                            Toast.LENGTH_SHORT).show()
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
        if (App.sharedPreferences.isLoggedIn) {  // after Login, this button was clicked again means logout
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
        App.sharedPreferences.isLoggedIn = false
        App.sharedPreferences.userEmail = ""
        App.sharedPreferences.password = ""
        App.sharedPreferences.authToken = ""
        userImageNavHeader.setImageResource(R.drawable.profiledefault)
        userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
        userEmailNavHeader.text = ""
        userNameNavHeader.text = ""

        MessageService.clearChannelsAndMessages()
        messageListView.adapter.notifyDataSetChanged()
        listViewAdapter.notifyDataSetInvalidated()
    }

    fun onAddChannelClick(view: View) {
        if (App.sharedPreferences.isLoggedIn) {
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
        if (App.sharedPreferences.isLoggedIn &&
                messageTextField.text.isNotEmpty() && selectedChannel != null){
            val userId = UserDataService.id
            val channelId = selectedChannel!!.id
            // when sending message, the order of parameters is important,
            // mis-ordered parameters cause a disaster
            socket.emit("newMessage", messageTextField.text.toString(),
                    userId, channelId, UserDataService.name, UserDataService.avatarName,
                    UserDataService.avatarColor)

            messageTextField.text.clear()
            hideKeyboard()
        }

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
        if (App.sharedPreferences.isLoggedIn) {
            runOnUiThread {
                if (args != null) {
                    val channelName = args[0] as String
                    val channelDescription = args[1] as String
                    val channelId = args[2] as String

                    val newChannel = Channel(channelName, channelDescription, channelId)
                    MessageService.channels.add(newChannel)

                    listViewAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private val onNewMessage = Emitter.Listener { args: Array<out Any>? ->
        if (App.sharedPreferences.isLoggedIn) {
            runOnUiThread {
                if (args != null) {
                    val channelId = args[2] as String

                    if (channelId == selectedChannel?.id) {
                        val msgBody = args[0] as String
                        val userId = args[1] as String
                        val userName = args[3] as String
                        val userAvatar = args[4] as String
                        val userAvatarColor = args[5] as String
                        val msgId = args[6] as String
                        val msgTimeStamp = args[7] as String

                        val newMessage = Message(msgBody, userId, channelId, userName, userAvatar,
                                userAvatarColor, msgId, msgTimeStamp)
                        MessageService.messages.add(newMessage)

                        messageListView.adapter.notifyDataSetChanged()
                        messageListView.smoothScrollToPosition(messageListView.adapter.itemCount - 1)
                    }
                }
            }
        }
    }

}
