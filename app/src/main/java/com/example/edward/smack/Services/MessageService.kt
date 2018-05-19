package com.example.edward.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.example.edward.smack.Controller.App
import com.example.edward.smack.Model.Channel
import com.example.edward.smack.Model.Message
import com.example.edward.smack.Utilities.URL_GET_CHANNELS
import com.example.edward.smack.Utilities.URL_GET_MESSAGES
import org.json.JSONException
import org.json.JSONObject

/*
 * Created by Edward on 5/17/2018.
 */

object MessageService {
    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()


    fun getChannels(complete: (Boolean) -> Unit) {

        val channelsRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null,
                Response.Listener { response ->
                    try {
                        if (response != null) {
                            // should clear channels only or clear both
//                            channels.clear()
                            clearChannelsAndMessages()


                            for (i in 0 until response.length()) {

                                val channel = response.getJSONObject(i)
                                val name = channel.getString("name")
                                val chanDesc = channel.getString("description")
                                val channelId = channel.getString("_id")

                                val newChannel = Channel(name, chanDesc, channelId)

                                channels.add(newChannel)
                                /**
                                 * this application is coded in java style, like above.
                                 * if in Kotlin style, they should be like below:
                                 * I will Kotlin stylize the app after the course

                                with(channel){
                                channels.add(Channel(getString("name"), getString("description"), getString("_id")))
                                }
                                 */
                            }
                            complete(true)

                        }
                    } catch (e: JSONException) {
                        Log.d("JSONException", "${e.localizedMessage}")
                    }

                },
                Response.ErrorListener { error ->
                    Log.d("ERROR", "Could not retrieve channels")
                    complete(false)
                }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.sharedPreferences.authToken}")
//                headers["Authorization"] = "Bearer ${App.sharedPreferences.authToken}"
                return headers
            }
        }

        App.sharedPreferences.requestQueue.add(channelsRequest)
    }

    fun getMessages(channelId: String, complete: (Boolean) -> Unit) {

        val url = "$URL_GET_MESSAGES$channelId"

        val messagesRequest = object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->
            try {
                if (response != null) {
                    messages.clear()
                    for (i in 0 until response.length()) {
                        val message: JSONObject = response.getJSONObject(i)

                        val msgBody = message.getString("messageBody")
                        val userId = message.getString("userId")
//                        val channelId = message.getString("channelId")
                        val userName = message.getString("userName")
                        val userAvatar = message.getString("userAvatar")
                        val userAvatarColor = message.getString("userAvatarColor")
                        val msgId = message.getString("_id")
                        val msgTimeStamp = message.getString("timeStamp")

                        val newMessage = Message(msgBody, userId, channelId, userName, userAvatar,
                                userAvatarColor, msgId, msgTimeStamp)
                        messages.add(newMessage)
                    }
                    complete(true)
                }

            } catch (e: JSONException) {
                Log.d("JSONException", "${e.localizedMessage}")
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not retrieve messages")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
//                headers.put("Authorization", "Bearer ${App.sharedPreferences.authToken}")
                headers["Authorization"] = "Bearer ${App.sharedPreferences.authToken}"
                return headers
            }
        }

        App.sharedPreferences.requestQueue.add(messagesRequest)
    }

    fun clearChannelsAndMessages(){
        channels.clear()
        messages.clear()
    }


    /**
     * default return is the first Channel
     */
    fun getChannelById(channelId: String): Channel {
        for (channel in channels){
            if (channel.id == channelId){
                return channel
            }
        }

        return channels[0]
    }
}