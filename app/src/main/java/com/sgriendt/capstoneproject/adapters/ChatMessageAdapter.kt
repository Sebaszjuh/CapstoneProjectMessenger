package com.sgriendt.capstoneproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.sgriendt.capstoneproject.Model.Chat
import com.sgriendt.capstoneproject.Model.UserInfo
import com.sgriendt.capstoneproject.R
import com.sgriendt.capstoneproject.Repository.MessengerRepository
import com.sgriendt.capstoneproject.ViewModel.MessengerViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.user.view.*
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

private const val VIEW_TYPE_MY_MESSAGE = 0
private const val VIEW_TYPE_OTHER_MESSAGE = 1

class ChatMessageAdapter(private val chatMessages: List<Chat>) :
    RecyclerView.Adapter<MessageViewHolder>()  {

    private val repository: MessengerRepository = MessengerRepository()

    protected var number: Int = 0

    private val messages: ArrayList<Chat> = ArrayList()

    fun addMessage(message: Chat){
        messages.add(message)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if(number == VIEW_TYPE_MY_MESSAGE) {
            messageToViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.chat_to_row, parent, false)
            )
        } else {
            messageFromViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.chat_from_row, parent, false)
            )
        }
    }

    inner class messageToViewHolder(itemView: View) : MessageViewHolder(itemView) {
//
//        fun databind(chatMessage: Chat) {
//            itemView.message_user_from.text = chatMessage.textMessage
//            itemView.timeText_to.text = DateUtils.fromMillisToTimeString(System.currentTimeMillis())
//        }
    }

    inner class messageFromViewHolder(itemView: View) : MessageViewHolder(itemView) {

//        fun databind(chatMessage: Chat) {
//            itemView.message_user_from.text = chatMessage.textMessage
//            itemView.timeText_from.text = DateUtils.fromMillisToTimeString(System.currentTimeMillis())
//            number = 1
//        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages.get(position)

        holder.bind(message)
    }
}

open class MessageViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message:Chat) {}
}

object DateUtils1 {
    fun fromMillisToTimeString(millis: Long) : String {
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(millis)
    }
}