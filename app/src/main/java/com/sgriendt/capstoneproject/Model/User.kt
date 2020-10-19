package com.sgriendt.capstoneproject.Model



data class User (
    val email: String,
    val password: String
)

data class UserInfo(
    val uid: String,
    val username: String,
    val profileImageUrl: String
){
    constructor() : this("","","")
}