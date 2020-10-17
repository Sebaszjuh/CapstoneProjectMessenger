package com.sgriendt.capstoneproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_start.*

class StartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_login_start.setOnClickListener{onLoginClick()}
        btn_login_register.setOnClickListener{onRegisterClick()}
    }

    private fun onLoginClick(){
        findNavController().navigate(R.id.loginFragment)
    }

    private fun onRegisterClick(){
        findNavController().navigate(R.id.registerFragment)
    }
}