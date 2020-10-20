package com.sgriendt.capstoneproject.UI.Messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sgriendt.capstoneproject.R

class ChatLogFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.title = "Chat log"
        return inflater.inflate(R.layout.fragment_chat_log, container, false)
    }

}