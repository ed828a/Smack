package com.example.edward.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.edward.smack.Utilities.URL_LOGIN
import com.example.edward.smack.Utilities.URL_REGISTER
import org.json.JSONException
import org.json.JSONObject

/*
 * Created by Edward on 5/15/2018.
 */

object AuthService {

    var isLoggedIn = false
    var userEnail = ""
    var userPassword = ""
    var authToken = ""
//    lateinit var requestQueue: RequestQueue

    fun registerUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {
        val url = URL_REGISTER

        userEnail = email
        userPassword = password

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        // we expect string response.
        val registerRequest = object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    println("response: $response")
                    complete(true)
                },
                Response.ErrorListener { error ->
                    Log.d("Error", "Register Error: $error")
                    complete(false)
                }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

//        requestQueue = Volley.newRequestQueue(context)
//        requestQueue.add(registerRequest)
        Volley.newRequestQueue(context).add(registerRequest)
    }

    fun loginUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, null,
                Response.Listener { response ->
                    println(response) // parse JsonObject
                    try {
                        authToken = response.getString("token")
                        userEnail = response.getString("user")
                        isLoggedIn = true
                        complete(true)
                    } catch (e: JSONException){
                        Log.d("JSONException", "Exception: ${e.localizedMessage}")
                    }

                }, Response.ErrorListener { error ->
            Log.d("Error", "Register Error: $error")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(loginRequest)
//        requestQueue.add(loginRequest)
    }
}