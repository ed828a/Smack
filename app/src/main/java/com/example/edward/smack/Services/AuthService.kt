package com.example.edward.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.edward.smack.Utilities.URL_REGISTER
import org.json.JSONObject

/*
 * Created by Edward on 5/15/2018.
 */

object AuthService {

   fun registerUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit){
       val url = URL_REGISTER

       val jsonBody = JSONObject()
       jsonBody.put("email", email)
       jsonBody.put("password", password)
       val requestBody = jsonBody.toString()

       val registerRequest = object : StringRequest(Method.POST, url,
               Response.Listener { response ->
                   println("response: $response")
                   complete(true) },
               Response.ErrorListener { error ->
                   Log.d("Error", "Register Error: $error")
                   complete(false)
               }){

           override fun getBodyContentType(): String {
               return "application/json; charset=utf-8"
           }

           override fun getBody(): ByteArray {
               return requestBody.toByteArray()
           }
       }

       Volley.newRequestQueue(context).add(registerRequest)
   }
}