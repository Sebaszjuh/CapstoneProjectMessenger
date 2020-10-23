package com.sgriendt.capstoneproject.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class User (
    val email: String,
    val password: String
)

@Parcelize
data class UserInfo(
    val uid: String,
    val username: String,
    val profileImageUrl: String
): Parcelable{
    constructor() : this("","","")
}