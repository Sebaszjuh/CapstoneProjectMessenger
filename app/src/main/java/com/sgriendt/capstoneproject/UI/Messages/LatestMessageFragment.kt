package com.sgriendt.capstoneproject.UI.Messages

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgriendt.capstoneproject.Model.LatestItemRow
import com.sgriendt.capstoneproject.R
import com.sgriendt.capstoneproject.ViewModel.MessengerViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_latest_message.*


class LatestMessageFragment : Fragment() {

    private val viewModel: MessengerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "Chats"
        viewModel.getLatestMessage()
        return inflater.inflate(R.layout.fragment_latest_message, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeMessagesAreFetched()
    }

    /**
     * Navigation for buttons in the menu bar
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sign_out -> {
                viewModel.signOut()
                viewModel.latestMessage.value!!.clear()
                findNavController().navigate(R.id.startFragment)
            }
            R.id.menu_new_message -> {
                findNavController().navigate(R.id.newMessageFragment)
            }
            android.R.id.home -> {
                viewModel.signOut()
                findNavController().navigate(R.id.startFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_nav, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Observers if latestmessages boolean changed and then calls retrievelatestmessage
     */
    private fun observeMessagesAreFetched() {
        viewModel.latestMessageFetched.observe(viewLifecycleOwner, Observer {
            retrieveLatestMessage()
        })
    }

    /**
     * Retrieves the actual messages and sets the adapter to the latestmessages if one of the rows is clicked it directs it to the correct message
     */
    private fun retrieveLatestMessage() {
        val adapter: GroupAdapter<GroupieViewHolder>
        val messages = viewModel.latestMessage
        adapter = messages.value!!
        rv_latest_messages.layoutManager = LinearLayoutManager(activity)
        rv_latest_messages.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        rv_latest_messages.adapter = adapter

        adapter.setOnItemClickListener { item, view ->
            val bundle = Bundle()

            val row = item as LatestItemRow
            bundle.putParcelable("usernameSelected", row.chatPartner)
            findNavController().navigate(R.id.chatLogFragment, bundle)
        }

    }

}

