package com.sgriendt.capstoneproject.UI.Messages

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sgriendt.capstoneproject.Model.Chat
import com.sgriendt.capstoneproject.Model.User
import com.sgriendt.capstoneproject.Model.UserInfo
import com.sgriendt.capstoneproject.R
import com.sgriendt.capstoneproject.ViewModel.MessengerViewModel
import com.sgriendt.capstoneproject.adapters.ChatMessageAdapter
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.fragment_chat_log.*
import kotlinx.android.synthetic.main.fragment_new_message.*
import kotlinx.android.synthetic.main.user.view.*

class ChatLogFragment : Fragment() {
    private val viewModel: MessengerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        retrieveMessages()
        send_button_chat_log.setOnClickListener {
            sendMessage()
        }
    }

    private fun retrieveMessages() {
        val user: UserInfo = arguments!!.getParcelable("usernameSelected")!!

        viewModel.getMessages(user)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun sendMessage() {
        if (edit_text_chat_log.text == null) {
            return
        } else {
            val user: UserInfo = arguments!!.getParcelable("usernameSelected")!!
            viewModel.sendMessage(edit_text_chat_log.text.toString(), user.uid)
            edit_text_chat_log.text = null
        }

    }

    private fun observeMessagesAreFetched() {
        viewModel.fetchedMessages.observe(viewLifecycleOwner, Observer {
            getMessagesFromdatabase()
        })
    }

    private fun getMessagesFromdatabase() {
        val adapter: GroupAdapter<GroupieViewHolder>
        val messages = viewModel.groupAdapter
        adapter = messages.value!!
        rv_chat_log.layoutManager = LinearLayoutManager(activity)
        rv_chat_log.adapter = adapter
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun init() {
        val user: UserInfo = arguments!!.getParcelable("usernameSelected")!!
        observeMessagesAreFetched()
        activity?.title = user.username
    }
}

class ChatTo(val text: String, val user: UserInfo) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_message_to.text = text
        val uri = user.profileImageUrl
        val profileImage = viewHolder.itemView.profileImage_to_chat
        Picasso.get().load(uri).into(profileImage)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}

class ChatFrom(val text: String, val user: UserInfo) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_message_from.text = text
        val uri = user.profileImageUrl
        val profileImage = viewHolder.itemView.profileImage_from_chat
        Picasso.get().load(uri).into(profileImage)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}