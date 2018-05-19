package com.example.edward.smack.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.edward.smack.Model.Message
import com.example.edward.smack.R
import com.example.edward.smack.Services.UserDataService
import kotlinx.android.synthetic.main.message_list_view.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/*
 * Created by Edward on 5/18/2018.
 */

class MessageAdapter(val context: Context, private val messages: ArrayList<Message> ) : RecyclerView.Adapter<MessageAdapter.messageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): messageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view, parent, false)

        return messageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.messages.count()
    }

    override fun onBindViewHolder(holder: messageViewHolder, position: Int) {
        val message = this.messages[position]
        val resourceId = context.resources.getIdentifier(message.userAvatar, "drawable", context.packageName)
        holder.messageImage?.setImageResource(resourceId)
        holder.messageImage?.setBackgroundColor(UserDataService.returnAvatarColor(message.userAvatarColor))
        holder.messageUserName?.text = message.userName
        holder.messageTimeStamp?.text = parseTimeStamp(message.timeStamp)
        holder.messageBody?.text = message.messageBody
    }

    inner class messageViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView){
        val messageImage = itemView?.messageUserImage
        val messageUserName = itemView?.messageUserNameText
        val messageTimeStamp = itemView?.messageTimeStampText
        val messageBody = itemView?.messageBodyText
    }

    fun parseTimeStamp(timeStamp: String): String {
        // input:   2018-05-18T11:39:44.888Z
        // output: Monday 4:35 PM

        val ISO8601DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
        var convertedDate = Date()
        try {
            convertedDate = isoFormatter.parse(timeStamp)
        } catch (e: ParseException){
            Log.d("PARSE", "Can not parse date")
            e.printStackTrace()
        }

        val outDateString = SimpleDateFormat("EEE, MMM d, h:mm a", Locale.getDefault())
        return outDateString.format(convertedDate)
    }
}