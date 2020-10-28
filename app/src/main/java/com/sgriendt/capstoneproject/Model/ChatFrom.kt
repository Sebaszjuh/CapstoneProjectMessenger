package com.sgriendt.capstoneproject.Model

import com.sgriendt.capstoneproject.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_from_row.view.*

class ChatFrom(val text: String, val user: UserInfo, val time: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_message_from.text = text
        viewHolder.itemView.timeText_from.text = time
        val uri = user.profileImageUrl
        val profileImage = viewHolder.itemView.profileImage_from_chat
        Picasso.get().load(uri).into(profileImage)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}