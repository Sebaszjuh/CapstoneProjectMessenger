package com.sgriendt.capstoneproject

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgriendt.capstoneproject.Model.User
import com.sgriendt.capstoneproject.Model.UserInfo
import com.sgriendt.capstoneproject.Model.UserItem
import com.sgriendt.capstoneproject.Repository.MessengerRepository
import com.sgriendt.capstoneproject.ViewModel.MessengerViewModel
import kotlinx.android.synthetic.main.fragment_new_message.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewMessageFragment : Fragment() {

    private val viewModel: MessengerViewModel by activityViewModels()
    private val userList = arrayListOf<UserInfo>()
    private val UserInfoAdapter = UserItemAdapter(userList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_message, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserFromDatabase()
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
        CoroutineScope(Dispatchers.Main).launch {
            val shoppingList = withContext(Dispatchers.IO) {
                viewModel.fetchUsers()
            }
            this@NewMessageFragment.userList.clear()

            this@NewMessageFragment.userList.addAll(shoppingList)

            this@NewMessageFragment.UserInfoAdapter.notifyDataSetChanged()
        }
    }

    private fun init() {
        activity?.title = "Select User"
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
            adapter = UserInfoAdapter
        }
    }


}