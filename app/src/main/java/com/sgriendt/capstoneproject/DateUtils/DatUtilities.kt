package com.sgriendt.capstoneproject.DateUtils

import java.text.SimpleDateFormat
import java.util.*

object DateUtilities {
    fun fromMillisToTimeString(millis: Long): String {
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(millis)
    }
}