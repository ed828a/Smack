package com.example.edward.smack.Model

/*
 * Created by Edward on 5/17/2018.
 */

class Channel(val name: String, val description: String, val id: String) {
    override fun toString(): String {
        return "#$name"
        // the reason of "#" is it's standard in chat rooms
        // it has the hashtag in from of the name of the channel
    }
}