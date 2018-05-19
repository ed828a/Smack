package com.example.edward.smack.Model

import android.os.Parcel
import android.os.Parcelable

/*
 * Created by Edward on 5/17/2018.
 */

class Channel(val name: String, val description: String, val id: String): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun toString(): String {
        return "#$name"
        // the reason of "#" is it's standard in chat rooms
        // it has the hashtag in from of the name of the channel
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Channel> {
        override fun createFromParcel(parcel: Parcel): Channel {
            return Channel(parcel)
        }

        override fun newArray(size: Int): Array<Channel?> {
            return arrayOfNulls(size)
        }
    }
}