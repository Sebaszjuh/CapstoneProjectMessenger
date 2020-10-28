package com.sgriendt.capstoneproject.Model

import com.sgriendt.capstoneproject.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_to_row.view.*


class ChatTo(val text: String, val user: UserInfo, val time: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_message_to.text = text
        val uri = user.profileImageUrl
        viewHolder.itemView.timeText_to.text = time
        val profileImage = viewHolder.itemView.profileImage_to_chat
        Picasso.get().load(uri).into(profileImage)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}