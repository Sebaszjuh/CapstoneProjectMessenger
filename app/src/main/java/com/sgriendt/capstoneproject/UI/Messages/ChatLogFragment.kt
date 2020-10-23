package com.sgriendt.capstoneproject.UI.Messages

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sgriendt.capstoneproject.Model.Chat
import com.sgriendt.capstoneproject.Model.UserInfo
import com.sgriendt.capstoneproject.R
import com.sgriendt.capstoneproject.adapters.ChatMessageAdapter
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_chat_log.*
import kotlinx.android.synthetic.main.fragment_new_message.*

class ChatLogFragment : Fragment() {

//    private val list = arrayListOf<Chat>()
//    private val chatMessageAdapter = ChatMessageAdapter(list)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_chat_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }


    @SuppressLint("UseRequireInsteadOfGet")
    fun init() {
        val user: UserInfo = arguments!!.getParcelable("usernameSelected")!!

        activity?.title = user.username

        val adapter = GroupAdapter<GroupieViewHolder>()
        adapter.add(ChatTo())
        adapter.add(ChatFrom())
        adapter.add(ChatTo())
        adapter.add(ChatFrom())
        adapter.add(ChatTo())
        adapter.add(ChatFrom())
        adapter.add(ChatTo())
        adapter.add(ChatTo())
        adapter.add(ChatFrom())
        rv_chat_log.layoutManager = LinearLayoutManager(activity)
        rv_chat_log.adapter = adapter
    }


}

class ChatTo: Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}

class ChatFrom: Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    }

    override fun getLayout(): Int {
       return R.layout.chat_from_row
    }

}