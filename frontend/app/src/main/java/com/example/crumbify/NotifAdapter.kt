package com.example.crumbify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotifAdapter(
    private var list: MutableList<NotifUser>
) : RecyclerView.Adapter<NotifAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.textView23)
        val tvMessage: TextView = view.findViewById(R.id.textView22)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recview_notif_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notif = list[position]
        holder.tvTitle.text = notif.title
        holder.tvMessage.text = notif.message
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<NotifUser>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}
