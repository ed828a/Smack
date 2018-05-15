package com.example.edward.smack.Services

import android.graphics.Color
import java.util.*

/*
 * Created by Edward on 5/15/2018.
 */

object UserDataService {
    var id =""
    var avatarColor = ""
    var avatarName = ""
    var email = ""
    var name = ""

    fun returnAvatarColor(components: String): Int{
        //[0.1568627450980392, 0.6509803921568628, 0.8862745098039215, 1]
        // 0.1568627450980392 0.6509803921568628 0.8862745098039215, 1
        val strippedColor = components.replace("[", "")
                .replace("]", "")
                .replace(",", "")
        var r = 0
        var g = 0
        var b = 0
        val scanner = Scanner(strippedColor)
        if (scanner.hasNext()){
            r = (scanner.nextDouble() * 255).toInt()
            g = (scanner.nextDouble() * 255).toInt()
            b = (scanner.nextDouble() * 255).toInt()
        }

        return Color.rgb(r, g, b)
    }
}