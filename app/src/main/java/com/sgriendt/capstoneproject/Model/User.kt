package com.sgriendt.capstoneproject.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class User (
    val email: String,
    val password: String
)