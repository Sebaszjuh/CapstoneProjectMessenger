package com.sgriendt.capstoneproject.UI.Messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.longrunning.ListOperationsRequest
import com.sgriendt.capstoneproject.Model.Chat
import com.sgriendt.capstoneproject.R
import com.sgriendt.capstoneproject.adapters.ChatMessageAdapter
import kotlinx.android.synthetic.main.fragment_chat_log.*
import kotlinx.android.synthetic.main.fragment_new_message.*

class ChatLogFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        init()
        return inflater.inflate(R.layout.fragment_chat_log, container, false)
    }


    fun init() {
        activity?.title = "Chat log"

        val c1 = Chat("Chat1", "name1")
        val c2 = Chat("Chat2", "name2")
        val c3 = Chat("Chat3", "name3")
        val list = listOf<Chat>(c1,c2,c3)
        val chatMessageAdapter = ChatMessageAdapter(list)

        //NULLPOINT RECYCLER
        recyclerview_chat_log.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerview_chat_log.adapter = chatMessageAdapter
        recyclerview_chat_log.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        recyclerview_chat_log.apply {
            setHasFixedSize(true)
            layoutManager = rv_new_message.layoutManager
            adapter = chatMessageAdapter
        }

    }


}