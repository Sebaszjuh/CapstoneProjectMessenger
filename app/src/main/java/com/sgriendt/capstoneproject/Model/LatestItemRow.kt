package com.sgriendt.capstoneproject.Model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sgriendt.capstoneproject.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message.view.*

class LatestItemRow(val chatMessage: ChatMessage) : Item<GroupieViewHolder>() {
    var chatPartner: UserInfo? = null
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_textview_latest_message.text = chatMessage.text

        val chatPartnerId: String
        if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartner = snapshot.getValue(UserInfo::class.java)
                viewHolder.itemView.username_textview_latest_message.text = chatPartner?.username

                val chatMessageImage = viewHolder.itemView.message_imageview_latest_message
                Picasso.get().load(chatPartner?.profileImageUrl).into(chatMessageImage)
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    override fun getLayout(): Int {
        return R.layout.latest_message
    }

}