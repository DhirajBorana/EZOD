package com.example.ezod.ui.student

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ezod.R
import com.example.ezod.model.ODMessage
import kotlinx.android.synthetic.main.student_list_item.view.*

class StudentMessageAdapter(private val data: MutableList<ODMessage>, val context: Context, private val listener: OnItemClickListener) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.student_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
        holder.itemView.setOnClickListener {
            listener.onItemClicked(data[position])
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    private val subjectTextView = view.user_message_subject_tv
    private val dateTextView = view.user_message_date_tv
    private val statusTextView = view.user_message_status_tv

    fun bind(message: ODMessage) {
        subjectTextView.text = message.subject
        dateTextView.text = message.date
        statusTextView.text = message.status
    }
}

interface OnItemClickListener {
    fun onItemClicked(item: ODMessage)
}
