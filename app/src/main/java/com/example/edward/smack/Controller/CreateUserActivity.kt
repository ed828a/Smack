package com.example.edward.smack.Controller

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.widget.Toast
import com.example.edward.smack.R
import com.example.edward.smack.R.id.*
import com.example.edward.smack.Services.AuthService
import com.example.edward.smack.Services.UserDataService
import com.example.edward.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profiledefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        createSpinner.visibility = View.INVISIBLE
    }

    fun onGenerateUserAvatar(view: View) {
        val random = Random()
        val color = random.nextInt(2) // this function generate next random integer
        // start from 0 to up-bound which is input param,
        // but not be included
        val avatar = random.nextInt(28)
        userAvatar = when (color) {
            0 -> "light$avatar"
            1 -> "dark$avatar"
            else -> "dark$avatar"
        }

        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)
        imageViewCreateAvatar.setImageResource(resourceId)
    }

    fun onGenerateBackgroundColorClick(view: View) {
        val random = Random()
        val r = random.nextInt(256)
        val g = random.nextInt(256)
        val b = random.nextInt(256)
        imageViewCreateAvatar.setBackgroundColor(Color.rgb(r, g, b))

        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255
        avatarColor = "[$savedR, $savedG, $savedB, 1]"

    }

    fun onCreateUserClick(view: View) {

        val userName = createUserNameText.text.toString()
        val email = createEmailText.text.toString()
        val password = createPasswordText.text.toString()


        // here, you can check the length and special character for email and password
        if (!userName.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            enableSpinner(true)

            AuthService.registerUser(email, password) { registerSuccess ->
                if (registerSuccess) {
                    AuthService.loginUser(email, password) { loginSuccess ->
                        if (loginSuccess) {
                            AuthService.createUser(userName, email,
                                    avatarColor, userAvatar) { createSuccess ->
                                if (createSuccess) {
                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this)
                                            .sendBroadcast(userDataChange)

                                    enableSpinner(false)
                                    finish()
                                } else {
                                    errorToast()
                                }
                            }
                        } else {
                            errorToast()
                        }
                    }
                } else {
                    errorToast()
                }
            }
        } else {
            enableSpinner(false)
            Toast.makeText(this,
                    "Make sure user name, email and password are filled in. ",
                    Toast.LENGTH_SHORT).show()
        }
    }

    private fun enableSpinner(enable: Boolean) {
        if (enable) {
            createSpinner.visibility = View.VISIBLE
        } else {
            createSpinner.visibility = View.INVISIBLE
        }
        imageViewCreateAvatar.isEnabled = !enable
        buttonBackgroundColor.isEnabled = !enable
        buttonCreateUser.isEnabled = !enable
    }

    private fun errorToast() {
        enableSpinner(false)
        Toast.makeText(this, "Something went wrong, please try again!",
                Toast.LENGTH_SHORT).show()
    }

}
