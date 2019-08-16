package com.example.ezod.ui.teacher

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ezod.R
import com.example.ezod.model.ODMessage
import com.example.ezod.ui.student.OnItemClickListener
import kotlinx.android.synthetic.main.teacher_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class TeacherMessageAdapter(private val data: MutableList<ODMessage>, private val listener: OnItemClickListener) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.teacher_list_item, parent, false))
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
    private val userNameTextView = view.user_name_tv
    private val registerIdTextView = view.user_register_id_tv
    private val dateTextView = view.user_date_tv
    private val statusTextView = view.user_status_tv
    private val subjectTextView = view.user_subject_tv

    fun bind(message: ODMessage) {
        userNameTextView.text = message.user.name
        registerIdTextView.text = message.user.registerId
        dateTextView.text = formatDateFromLong(message.createdOn)
        statusTextView.text = message.status
        subjectTextView.text = message.subject
    }

    private fun formatDateFromLong(time: Long): String {
        return if (time != 0L) SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date(time))
        else ""
    }
}

interface OnItemClickListener {
    fun onItemClicked(item: ODMessage)
}
