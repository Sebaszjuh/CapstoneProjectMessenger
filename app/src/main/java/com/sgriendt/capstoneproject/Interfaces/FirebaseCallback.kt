package com.sgriendt.capstoneproject.Interfaces

import com.sgriendt.capstoneproject.Model.UserInfo

/**
 * Callback used to the retrieval of the users.
 */
interface FirebaseCallback {
    fun onCallback(list: ArrayList<UserInfo>)
}



