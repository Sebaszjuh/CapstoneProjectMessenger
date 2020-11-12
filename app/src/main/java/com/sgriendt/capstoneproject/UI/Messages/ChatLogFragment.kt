package com.sgriendt.capstoneproject.UI.Messages

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgriendt.capstoneproject.Model.UserInfo
import com.sgriendt.capstoneproject.R
import com.sgriendt.capstoneproject.ViewModel.MessengerViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_chat_log.*

class ChatLogFragment : Fragment() {
    private val viewModel: MessengerViewModel by activityViewModels()
    private lateinit var adapterThing: GroupAdapter<GroupieViewHolder>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat_log, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        retrieveMessages()
        send_button_chat_log.setOnClickListener {
            sendMessage()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack(R.id.latestMessageFragment, true)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    /**
     * Method that gets called on creation of fragment. Retrieves the messages from firebase through the viewmodel. Also adds user to the viewmodel to relais to repo
     */
    @SuppressLint("UseRequireInsteadOfGet")
    private fun retrieveMessages() {
        val user: UserInfo = arguments!!.getParcelable("usernameSelected")!!
        viewModel.setUser(user)
        viewModel.getMessages(user)
    }

    /**
     * Retrieves text from the layout and delete everything inside. Sends the text to the viewmodel to send to the repo.
     * Also passes the userinfo through the viewmodel
     */
    @SuppressLint("UseRequireInsteadOfGet")
    private fun sendMessage() {
        if (edit_text_chat_log.text == null) {
            return
        } else {
            val user: UserInfo = arguments!!.getParcelable("usernameSelected")!!
            viewModel.setUser(user)
            viewModel.sendMessage(edit_text_chat_log.text.toString(), user.uid)
            edit_text_chat_log.text = null
            observeMessagesSendSuccesfully()
        }
    }

    /**
     * Observes if messages are done ( asynchronous loading ) If so calls actual retrieval of messages
     */
    private fun observeMessagesAreFetched() {
        viewModel.fetchedMessages.observe(viewLifecycleOwner, Observer {
            getMessagesFromdatabase()
        })
    }

    /**
     * Actual retrieval of messages of database. Adds them to an adapter to show
     */
    private fun getMessagesFromdatabase() {
        val adapter: GroupAdapter<GroupieViewHolder>
        val messages = viewModel.groupAdapter
        adapter = messages.value!!
        adapterThing = adapter
        rv_chat_log.layoutManager = LinearLayoutManager(activity)
        rv_chat_log.adapter = adapter

    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun init() {
        val user: UserInfo = arguments!!.getParcelable("usernameSelected")!!
        observeMessagesAreFetched()
        if(user != null){
            activity?.title = user.username
        } else {
            activity?.title = "Username unknown"
        }

    }

    /**
     * Retrieves if message is send succesfully and then scrolls to bottom
     */
    private fun  observeMessagesSendSuccesfully(){
        viewModel.succesfulSendMessage.observe(viewLifecycleOwner, Observer {
            rv_chat_log.scrollToPosition(adapterThing.itemCount-1)
        })
    }
}


