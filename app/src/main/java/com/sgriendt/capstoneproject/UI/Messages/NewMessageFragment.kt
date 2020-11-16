package com.sgriendt.capstoneproject.UI.Messages

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgriendt.capstoneproject.Interfaces.OnUserClickListener
import com.sgriendt.capstoneproject.Model.UserInfo
import com.sgriendt.capstoneproject.R
import com.sgriendt.capstoneproject.ViewModel.MessengerViewModel
import com.sgriendt.capstoneproject.adapters.UserItemAdapter
import kotlinx.android.synthetic.main.fragment_new_message.*

class NewMessageFragment : Fragment(), OnUserClickListener {

    private val viewModel: MessengerViewModel by activityViewModels()
    private val userList = arrayListOf<UserInfo>()
    private val userInfoAdapter = UserItemAdapter(userList, this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_message, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.fetchUsers()

    }

    override fun onUserClick(item: UserInfo, position: Int) {
        val bundle = Bundle()
        bundle.putParcelable("usernameSelected", userList[position])
        findNavController().navigate(R.id.chatLogFragment, bundle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack(R.id.latestMessageFragment, true)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_new_messages, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Retrieves users after being called by the observer method and adds the users to the adapter
     */
    private fun getUserFromDatabase() {
        var users: LiveData<ArrayList<UserInfo>>? = null
        users?.value?.clear()
        users = viewModel.userItems
        this@NewMessageFragment.userList.clear()
        this.userList.clear()
        users.value?.let { this@NewMessageFragment.userList.addAll(it) }
        this@NewMessageFragment.userInfoAdapter.notifyDataSetChanged()
        rv_new_message.layoutManager = LinearLayoutManager(activity)
        rv_new_message.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )
        rv_new_message.apply {
            setHasFixedSize(true)
            layoutManager = rv_new_message.layoutManager
            adapter = userInfoAdapter
        }
    }

    private fun init() {
        activity?.title = "Select User"
        observeUsersAreFetched()
    }

    /**
     * Observes if users are fetched and then calls the actual retrieval.
     */
    private fun observeUsersAreFetched() {
        viewModel.fetchedUsers.observe(viewLifecycleOwner, Observer {
            getUserFromDatabase()
        })
    }


}