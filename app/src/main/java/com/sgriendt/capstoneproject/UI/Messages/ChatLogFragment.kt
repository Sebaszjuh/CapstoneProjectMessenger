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
import com.sgriendt.capstoneproject.Model.UserInfo
import com.sgriendt.capstoneproject.R
import com.sgriendt.capstoneproject.ViewModel.MessengerViewModel
import com.sgriendt.capstoneproject.adapters.ChatMessageAdapter
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
        viewModel.getMessages()
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun sendMessage() {
        val user: UserInfo = arguments!!.getParcelable("usernameSelected")!!
        viewModel.sendMessage(edit_text_chat_log.text.toString(), user.uid)
    }

    private fun observeMessagesAreFetched() {
        viewModel.fetchedMessages.observe(viewLifecycleOwner, Observer {
            getMessagesFromdatabase()
        })
    }

    private fun getMessagesFromdatabase(){
        val adapter = GroupAdapter<GroupieViewHolder>()
        var messageTo: LiveData<ArrayList<ChatTo>>? = null
        var messageFrom: LiveData<ArrayList<ChatFrom>>? = null
        messageFrom?.value?.clear()
        messageTo?.value?.clear()
        messageTo = viewModel.messagesTo
        messageFrom = viewModel.messagesFrom
        for(message in messageFrom.value!!){
            Log.d("TEST", message.text)
            adapter.add(ChatFrom(message.text))
        }
        for(message in messageTo.value!!){
            adapter.add(ChatTo(message.text))
        }
//        adapter.add(ChatTo(messageTo))
        rv_chat_log.layoutManager = LinearLayoutManager(activity)
        rv_chat_log.adapter = adapter
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun init() {
        val user: UserInfo = arguments!!.getParcelable("usernameSelected")!!
        observeMessagesAreFetched()
        activity?.title = user.username

//        val adapter = GroupAdapter<GroupieViewHolder>()
//        adapter.add(ChatTo("YOURE A POTATO"))
//        adapter.add(ChatFrom("IM A POTATO"))
//
//        rv_chat_log.layoutManager = LinearLayoutManager(activity)
//        rv_chat_log.adapter = adapter
    }
}

class ChatTo(val text: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_message_to.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}

class ChatFrom(val text: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_message_from.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}