package com.sgriendt.capstoneproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sgriendt.capstoneproject.Model.Chat
import com.sgriendt.capstoneproject.Model.UserInfo
import com.sgriendt.capstoneproject.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user.view.*

class ChatMessageAdapter(private val chatMessages: List<Chat>) :
    RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>()  {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun databind(chatMessage: Chat) {
            itemView.message_user_from.text = chatMessage.textMessage
//            Picasso.get().load(userInfo.profileImageUrl).into(itemView.profileImage.profileImage)
//            itemView.userName.text = userInfo.username
//            Picasso.get().load(userInfo.profileImageUrl).into(itemView.profileImage.profileImage)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.user, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.databind(chatMessages[position])
    }

    fun getLayOut(): Int{
        return R.layout.chat_from_row
    }


}