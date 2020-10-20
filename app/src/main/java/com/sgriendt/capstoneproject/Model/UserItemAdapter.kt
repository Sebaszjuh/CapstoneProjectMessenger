package com.sgriendt.capstoneproject.Model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sgriendt.capstoneproject.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user.view.*

class UserItemAdapter(private val users: List<UserInfo>) :
    RecyclerView.Adapter<UserItemAdapter.ViewHolder>()  {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun databind(userInfo: UserInfo) {
            itemView.userName.text = userInfo.username
            Picasso.get().load(userInfo.profileImageUrl).into(itemView.profileImage.profileImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.user, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.databind(users[position])
    }



}