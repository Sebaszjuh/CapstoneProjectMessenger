package com.sgriendt.capstoneproject.Interfaces

import com.sgriendt.capstoneproject.Model.UserInfo

interface OnUserClickListener {
    fun onUserClick(item: UserInfo, position: Int)
}