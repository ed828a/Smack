package com.example.edward.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.edward.smack.Model.Channel
import com.example.edward.smack.Utilities.URL_GET_CHANNELS
import org.json.JSONException

/*
 * Created by Edward on 5/17/2018.
 */

object MessageService {
    val channels = ArrayList<Channel>()

    fun getChannels(context: Context, complete: (Boolean) -> Unit){


        val channelsRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null,
                Response.Listener { response ->
                    try {
                        if (response != null){
                            for (i in 0 until response.length()){

                                val channel = response.getJSONObject(i)
                                val name = channel.getString("name")
                                val chanDesc = channel.getString("description")
                                val channelId = channel.getString("_id")

                                val newChannel = Channel(name, chanDesc, channelId)

                                channels.add(newChannel)
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
                headers.put("Authorization", "Bearer ${AuthService.authToken}")

                return headers
            }
        }

        Volley.newRequestQueue(context).add(channelsRequest)
    }
}