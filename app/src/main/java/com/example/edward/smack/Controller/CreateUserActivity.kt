package com.example.edward.smack.Controller

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.example.edward.smack.R
import com.example.edward.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
    }

    fun onGenerateUserAvatar(view: View) {
        val random = Random()
        val color = random.nextInt(2) // this function generate next random integer
        // start from 0 to upbound which is input param,
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
        var random = Random()
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
        createUserNameText.text.clear()
        createEmailText.text.clear()
        createPasswordText.text.clear()

        AuthService.registerUser(this, email, password) { registerSuccess ->
            if (registerSuccess) {
                AuthService.loginUser(this, email, password) { loginSuccess ->
                    if (loginSuccess) {
                        println(AuthService.authToken)
                        println(AuthService.userEnail)
                    }
                }
            } else {
                println("Register failed")
            }
        }
    }
}
