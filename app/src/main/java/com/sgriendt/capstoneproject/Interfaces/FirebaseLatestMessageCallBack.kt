package com.sgriendt.capstoneproject.Interfaces

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

interface FirebaseLatestMessageCallBack{
    fun onCallBack(latestMessages: GroupAdapter<GroupieViewHolder>)
}