package com.sgriendt.capstoneproject.Interfaces

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

interface FirebaseMessagesCallbackGroup {
    fun onCallback(listTo: GroupAdapter<GroupieViewHolder>)
}
