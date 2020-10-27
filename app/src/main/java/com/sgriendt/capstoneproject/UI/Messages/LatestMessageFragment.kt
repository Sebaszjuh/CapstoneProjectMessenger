package com.sgriendt.capstoneproject.UI.Messages

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgriendt.capstoneproject.Model.ChatMessage
import com.sgriendt.capstoneproject.Model.UserInfo
import com.sgriendt.capstoneproject.R
import com.sgriendt.capstoneproject.ViewModel.MessengerViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_chat_log.*
import kotlinx.android.synthetic.main.fragment_latest_message.*
import kotlinx.android.synthetic.main.latest_message.view.*


class LatestMessageFragment : Fragment() {

    private val viewModel: MessengerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "Chats"

        return inflater.inflate(R.layout.fragment_latest_message, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLatestMessage()
        observeMessagesAreFetched()
    }

    private fun observeMessagesAreFetched() {
        viewModel.latestMessageFetched.observe(viewLifecycleOwner, Observer {
            retrieveLatestMessage()
        })
    }

    private fun retrieveLatestMessage() {

        var adapter: GroupAdapter<GroupieViewHolder>
        val messages = viewModel.latestMessage
        adapter = messages.value!!
//        messages.value!!.clear()
        rv_latest_messages.layoutManager = LinearLayoutManager(activity)
        rv_latest_messages.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sign_out -> {
                viewModel.signOut()
                findNavController().navigate(R.id.startFragment)
            }
            R.id.menu_new_message -> {
                findNavController().navigate(R.id.newMessageFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_nav, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}

class LatestItemRow(val chatMessage: ChatMessage) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_textview_latest_message.text = chatMessage.text

    }

    override fun getLayout(): Int {
        return R.layout.latest_message
    }

}