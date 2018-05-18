package com.example.edward.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.example.edward.smack.Controller.App
import com.example.edward.smack.Model.Channel
import com.example.edward.smack.Utilities.URL_GET_CHANNELS
import org.json.JSONException

/*
 * Created by Edward on 5/17/2018.
 */

object MessageService {
    val channels = ArrayList<Channel>()

    fun getChannels(complete: (Boolean) -> Unit){


        val channelsRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null,
                Response.Listener { response ->
                    try {
                        if (response != null){
                            channels.clear()
                            for (i in 0 until response.length()){

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
                    } catch (e: JSONException){
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

                return headers
            }
        }

        App.sharedPreferences.requestQueue.add(channelsRequest)
    }
}