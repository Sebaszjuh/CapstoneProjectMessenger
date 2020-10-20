package com.sgriendt.capstoneproject.UI.Messages

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sgriendt.capstoneproject.R
import com.sgriendt.capstoneproject.ViewModel.MessengerViewModel

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
        observeIsLoggedOut()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_sign_out ->{
                viewModel.signOut()
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

    private fun observeIsLoggedOut() {
        viewModel.isLoggedOut.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(R.id.loginFragment)
        })
    }

}