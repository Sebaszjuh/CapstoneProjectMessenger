package com.sgriendt.capstoneproject.Interfaces

import com.sgriendt.capstoneproject.Model.UserInfo
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

/**
 * Callback used to the retrieval of the users.
 */
interface FirebaseCallback {
    fun onCallback(list: ArrayList<UserInfo>)
}

interface FirebaseMessagesCallbackGroup {
    fun onCallback(listTo: GroupAdapter<GroupieViewHolder>)
}

interface FirebaseCurrentUserCallBack {
    fun onCallback(user: UserInfo)
}