package com.example.edward.smack.Utilities

/*
 * Created by Edward on 5/15/2018.
 */
 
// for local hosting: http://10.0.2.2:3005/v1/   and the default ip for the simulator is 10.0.2.2
const val BASE_URL = "https://ed828chat.herokuapp.com/v1/"    // v1 is a part of the endpoint
const val SOCKET_URL = "https://ed828chat.herokuapp.com/"
const val URL_REGISTER = "${BASE_URL}account/register"
const val URL_LOGIN = "${BASE_URL}account/login"
const val URL_CREATE_USER = "${BASE_URL}user/add"
const val URL_GET_USER = "${BASE_URL}user/byEmail/"
const val URL_GET_CHANNELS = "${BASE_URL}channel/"
const val URL_GET_MESSAGES = "${BASE_URL}message/byChannel/"

// Broadcast Constants
const val BROADCAST_USER_DATA_CHANGE = "BROADCAST_USER_DATA_CHANGE"

// SharedPreferences Constants
const val PREFS_FILENAME = "prefs"
const val IS_LOGGED_IN = "isLoggedIn"
const val AUTH_TOKEN = "authToken"
const val USER_EMAIL = "userEmail"
const val USER_PASSWORD = "password"