package com.sgriendt.capstoneproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sgriendt.capstoneproject.ViewModel.MessengerViewModel

class LatestMessageFragment : Fragment() {

    private val viewModel: MessengerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel.checkLogin()
        observeUserNotloggedIn()
        return inflater.inflate(R.layout.fragment_latest_message, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_nav, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun observeUserNotloggedIn() {
        viewModel.isNotLoggedin.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(R.id.startFragment)
        })
    }
}