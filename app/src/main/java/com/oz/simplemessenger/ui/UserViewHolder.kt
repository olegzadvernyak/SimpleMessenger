package com.oz.simplemessenger.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.oz.simplemessenger.db.User
import kotlinx.android.synthetic.main.user_list_item.view.*

class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(user: User, userClickListener: (User) -> Unit) {
        with(itemView) {
            usernameTextView.text = user.username
            isActiveView.visibility = if (user.isActive) View.VISIBLE else View.GONE
            setOnClickListener { userClickListener.invoke(user) }
        }
    }

    fun clear() {
        with(itemView) {
            usernameTextView.text = ""
            isActiveView.visibility = View.GONE
            setOnClickListener(null)
        }
    }

}