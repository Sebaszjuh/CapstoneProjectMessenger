package com.sgriendt.capstoneproject.Model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserItem(

    var userName: String,
    var profileImage: Uri?

): Parcelable

