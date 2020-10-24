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
import com.sgriendt.capstoneproject.adapters.OnUserClickListener
import com.sgriendt.capstoneproject.Model.UserInfo
import com.sgriendt.capstoneproject.adapters.UserItemAdapter
import com.sgriendt.capstoneproject.R
import com.sgriendt.capstoneproject.ViewModel.MessengerViewModel
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
    // popBackstack om terug te ganan?
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
            R.id.menu_new_message -> {

                findNavController().navigateUp()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_new_messages, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

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

    private fun observeUsersAreFetched() {
        viewModel.fetchedUsers.observe(viewLifecycleOwner, Observer {
            getUserFromDatabase()
        })
    }


}