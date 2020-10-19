package com.sgriendt.capstoneproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.sgriendt.capstoneproject.ViewModel.MessengerViewModel
import kotlinx.android.synthetic.main.fragment_start.*

class StartFragment : Fragment() {

    private val viewModel: MessengerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.title = "Start"
        viewModel.checkLogin()

        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUserLoggedIn()

        btn_login_start.setOnClickListener{onLoginClick()}
        btn_login_register.setOnClickListener{onRegisterClick()}
    }

    private fun onLoginClick(){
        viewModel.isNotLoggedin.observe(viewLifecycleOwner, Observer{
            Log.d("NOT LOGGED IN", "NOT LOGGED IN")
            findNavController().navigate(R.id.loginFragment)
        })

    }

    private fun onRegisterClick(){
        findNavController().navigate(R.id.registerFragment)
    }

    private fun observeUserLoggedIn() {
        viewModel.isLoggedin.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(R.id.latestMessageFragment)
        })
    }

}