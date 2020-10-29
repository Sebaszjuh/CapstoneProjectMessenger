package com.sgriendt.capstoneproject.Interfaces

import com.sgriendt.capstoneproject.Model.UserInfo

interface FirebaseCurrentUserCallBack {
    fun onCallback(user: UserInfo)
}
