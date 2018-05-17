package com.example.edward.smack.Services

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.edward.smack.Utilities.*
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

        // expecting string response.
        val registerRequest = object : StringRequest(Method.POST, url,
                Response.Listener { complete(true) },
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

        // expecting JSONOBJECT response.
        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, null,
                Response.Listener { response ->
                    try {
                        authToken = response.getString("token")
                        userEnail = response.getString("user")
                        isLoggedIn = true
                        complete(true)
                    } catch (e: JSONException) {
                        Log.d("JSONException", "Exception: ${e.localizedMessage}")
                        complete(false)
                    }

                }, Response.ErrorListener { error ->
            Log.d("Error", "Login Error: $error")
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

    fun createUser(context: Context, name: String,
                   email: String, avatarColor: String,
                   avatarName: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarColor", avatarColor)
        jsonBody.put("avatarName", avatarName)
        val requestBody = jsonBody.toString()

        val createRequest = object : JsonObjectRequest(Method.POST, URL_CREATE_USER, null,
                Response.Listener { response ->
                    try {
                        UserDataService.avatarColor = response.getString("avatarColor")
                        UserDataService.avatarName = response.getString("avatarName")
                        UserDataService.email = response.getString("email")
                        UserDataService.name = response.getString("name")
                        UserDataService.id = response.getString("_id")
                        complete(true)
                    } catch (e: JSONException) {
                        Log.d("JSONException", "Exception: ${e.localizedMessage}")
                        complete(false)
                    }

                }, Response.ErrorListener { error ->
            Log.d("Error", "Create User Error: $error")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
//                val headers: MutableMap<String, String> = mutableMapOf()
                headers.put("Authorization", "Bearer $authToken")
                return headers
            }
        }

        Volley.newRequestQueue(context).add(createRequest)
    }

    fun findUserByEmail(context: Context, complete: (Boolean) -> Unit){
        val findUserRequest = object : JsonObjectRequest(Method.GET, "$URL_GET_USER$userEnail", null,
                Response.Listener {response ->
                    try {
                        UserDataService.avatarColor = response.getString("avatarColor")
                        UserDataService.avatarName = response.getString("avatarName")
                        UserDataService.email = response.getString("email")
                        UserDataService.name = response.getString("name")
                        UserDataService.id = response.getString("_id")
                        println("Response: $response")
                        val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)

                        complete(true)
                    } catch (e: JSONException) {
                        Log.d("JSONException", "Exception: ${e.localizedMessage}")
                        complete(false)
                    }
                },
                Response.ErrorListener { error ->
                    Log.d("ERROR", "Could not find user.")
                    complete(false)
                }){

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer $authToken")

                return headers
            }
        }

        Volley.newRequestQueue(context).add(findUserRequest)
    }
}