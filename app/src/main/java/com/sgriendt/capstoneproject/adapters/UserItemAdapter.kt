package com.sgriendt.capstoneproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sgriendt.capstoneproject.Interfaces.OnUserClickListener
import com.sgriendt.capstoneproject.Model.UserInfo
import com.sgriendt.capstoneproject.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user.view.*

class UserItemAdapter(private val users: List<UserInfo>, var clickListener: OnUserClickListener) :
    RecyclerView.Adapter<UserItemAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun databind() {}

        fun initialize(item: UserInfo, action: OnUserClickListener) {
            itemView.message_user_from.text = item.username
            Picasso.get().load(item.profileImageUrl).into(itemView.profileImage.profileImage)
            itemView.setOnClickListener {
                action.onUserClick(item, adapterPosition)
            }
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
        holder.initialize(users.get(position), clickListener)
    }
}