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

    fun returnAvatarColor(colorString: String): Int{
        //[0.1568627450980392, 0.6509803921568628, 0.8862745098039215, 1]
        // 0.1568627450980392 0.6509803921568628 0.8862745098039215, 1
//        val strippedColor = components.replace("[", "")
//                .replace("]", "")
//                .replace(",", "")
//        var r = 0
//        var g = 0
//        var b = 0
//        val scanner = Scanner(strippedColor)
//        if (scanner.hasNext()){
//            r = (scanner.nextDouble() * 255).toInt()
//            g = (scanner.nextDouble() * 255).toInt()
//            b = (scanner.nextDouble() * 255).toInt()
//        }
//
//        return Color.rgb(r, g, b)
        /**
         * cheap and efficient way,
         * the key is String.split() functionfun
         * CharSequence.split(vararg delimiters: String,
         *                    ignoreCase: Boolean = false, limit: Int = 0): List<String>
         */
        val separate2 = colorString.split(", ", "[", "]")
        val red = (separate2[1].toDouble() * 255).toInt()
        val green = (separate2[2].toDouble() * 255).toInt()
        val blue = (separate2[3].toDouble() * 255).toInt()

        return Color.rgb(red, green, blue)
    }
}