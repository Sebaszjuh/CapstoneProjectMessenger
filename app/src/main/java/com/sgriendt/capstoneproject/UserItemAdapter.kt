package com.sgriendt.capstoneproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sgriendt.capstoneproject.Model.UserInfo
import com.sgriendt.capstoneproject.Model.UserItem
import kotlinx.android.synthetic.main.user.view.*

class UserItemAdapter(private val users: List<UserInfo>) :
    RecyclerView.Adapter<UserItemAdapter.ViewHolder>()  {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun databind(userInfo: UserInfo) {
            itemView.userName.text = userInfo.username


//            itemView.profileImage.setImageURI(userItem.profileImage)
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