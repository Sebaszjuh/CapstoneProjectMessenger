package com.sgriendt.capstoneproject.Model

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.net.URI
import java.net.URL

@Parcelize
data class UserItem(

    var userName: String,
    var profileImage: Uri?

): Parcelable

