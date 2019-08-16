package com.example.ezod.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.ezod.R
import com.example.ezod.model.ODMessage
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_message.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.util.Log
import android.view.View
import com.example.ezod.ImageViewActivity
import com.example.ezod.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.class_hour_layout.*
import kotlinx.android.synthetic.main.image_file_layout.*
import kotlinx.android.synthetic.main.status_button_layout.*


class MessageActivity : AppCompatActivity() {

    private var imageFileUri = Uri.EMPTY
    private var messageUid = ""
    private var userRole = ""
    private var userEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        supportActionBar?.title = ""
        setOnClickListeners()
        getMessage()
    }

    private fun setOnClickListeners() {
        message_image_file_iv.setOnClickListener {
            val intent = Intent(this, ImageViewActivity::class.java)
            intent.putExtra("IMAGE_FILE", imageFileUri.toString())
            startActivity(intent)
        }

        status_toggle_group.addOnButtonCheckedListener { _, _, _ ->
           setMessageStatus(status_toggle_group.checkedButtonId)
        }
    }

    private fun setMessageStatus(checkedId: Int) {
        val ref = FirebaseDatabase.getInstance().getReference("messages/$userRole/$messageUid/status")
            when (checkedId) {
                R.id.status_approve_btn -> { ref.setValue(Status.APPROVED.toString()) }
                R.id.status_decline_btn -> { ref.setValue(Status.DECLINED.toString()) }
                else -> { ref.setValue(Status.PENDING.toString()) }
            }
    }

    private fun getMessage() {
        val intent = intent
        val message = intent.getSerializableExtra("OD_MESSAGE") as ODMessage
        messageUid = message.uid
        userRole = intent.getStringExtra("USER_ROLE") ?: ""
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""
        updateUi(message)
    }

    private fun updateUi(message: ODMessage) {
        message_subject_tv.text = message.subject
        message_status_tv.text = message.status
        message_from_email_tv.text = message.fromEmail
        message_to_email_tv.text = getEmailsFromArrayList(message.sendToEmail)
        message_date_date_tv.text = formatDateFromLong(message.createdOn)
        message_content_tv.text = message.content
        message_image_filename_tv.text = message.imageFileName
        getImageFromFirebaseStorage(message.imageFileUid)
        updateClassHourUi(message)
        val email = FirebaseAuth.getInstance().currentUser?.email ?: ""
        if (email.contains("ac.in")) status_toggle_group.visibility = View.VISIBLE

        when(message.status) {
            Status.APPROVED.toString() -> status_toggle_group.check(R.id.status_approve_btn)
            Status.DECLINED.toString() -> status_toggle_group.check(R.id.status_decline_btn)
        }
    }

    private fun updateClassHourUi(message: ODMessage) {
        for ( (index, value) in message.sendToEmailAfterAccepted.withIndex()) {
            if (value.isNotEmpty() && value.isNotBlank()) {
                when (index+1) {
                    1 -> {
                        message_class_hour_1_tv.visibility = View.VISIBLE
                        message_class_hour_1_email_tv.visibility = View.VISIBLE
                        message_class_hour_1_email_tv.text = value }
                    2 -> {
                        message_class_hour_2_tv.visibility = View.VISIBLE
                        message_class_hour_2_email_tv.visibility = View.VISIBLE
                        message_class_hour_2_email_tv.text = value }
                    3 -> {
                        message_class_hour_3_tv.visibility = View.VISIBLE
                        message_class_hour_3_email_tv.visibility = View.VISIBLE
                        message_class_hour_3_email_tv.text = value }
                    4 -> {
                        message_class_hour_4_tv.visibility = View.VISIBLE
                        message_class_hour_4_email_tv.visibility = View.VISIBLE
                        message_class_hour_4_email_tv.text = value }
                    5 -> {
                        message_class_hour_5_tv.visibility = View.VISIBLE
                        message_class_hour_5_email_tv.visibility = View.VISIBLE
                        message_class_hour_5_email_tv.text = value }
                    6 -> {
                        message_class_hour_6_tv.visibility = View.VISIBLE
                        message_class_hour_6_email_tv.visibility = View.VISIBLE
                        message_class_hour_6_email_tv.text = value }
                    7 -> {
                        message_class_hour_7_tv.visibility = View.VISIBLE
                        message_class_hour_7_email_tv.visibility = View.VISIBLE
                        message_class_hour_7_email_tv.text = value }
                }
            }
        }
    }

    private fun getEmailsFromArrayList(sendToEmail: ArrayList<String>): String {
        var emails = ""
        sendToEmail.forEach { emails += "$it\n" }
        return emails.trimIndent()

    }

    private fun formatDateFromLong(time: Long): String {
        return if (time != 0L) SimpleDateFormat("MMM d, yyyy, hh:mm aaa", Locale.US).format(Date(time))
        else ""
    }

    private fun getImageFromFirebaseStorage(imageFileUid: String) {
        val storageReference = FirebaseStorage.getInstance().getReference("images/$imageFileUid")
        storageReference.downloadUrl
            .addOnSuccessListener { imageFileUri = it
                loadImage(it) }
            .addOnCanceledListener { Log.d("MessageActivity", "Cancelled to load image") }
            .addOnFailureListener { Log.d("MessageActivity", "Failed to load image") }
    }

    private fun loadImage(imageUri: Uri?) {
        Glide.with(this)
            .load(imageUri)
            .centerCrop()
            .into(message_image_file_iv)
    }
}
