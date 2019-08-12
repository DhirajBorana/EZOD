package com.example.ezod.ui.student

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import com.example.ezod.R
import com.example.ezod.Role
import com.example.ezod.Status
import com.example.ezod.model.ODMessage
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_create_od.*
import java.io.IOException
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CreateODActivity : AppCompatActivity() {

    object RequestCode {
        const val EMAIL = 1
        const val CLASS_HOUR = 2
        const val IMAGE = 3
        const val RESULT_OK = 0
    }

    private val calendar = Calendar.getInstance()
    private val dateChangeListener:  DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        updateDateUi()
    }

    private fun updateDateUi() {
        val format = "dd/MM/yyyy"
        val dateFormat = SimpleDateFormat(format, Locale.US)

        date_tf.setText(dateFormat.format(calendar.time))
    }

    private var sendToEmailData = arrayListOf<String>()
    private var classHourEmailData = arrayListOf<String>()
    private lateinit var imageFilePathData: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_od)

        updateFromEmailUi()
        setOnClickListeners()
    }

    private fun updateFromEmailUi() {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Null"
        from_email_tf.setText(currentUserEmail)
    }

    private fun setOnClickListeners() {
        to_email_tf.setOnClickListener { chooseFacultyEmail() }

        class_hour_tf.setOnClickListener { chooseClassHour() }

        image_file_tf.setOnClickListener { chooseImage() }

        date_tf.setOnClickListener {
            DatePickerDialog(
                this,
                dateChangeListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        send_od_fab.setOnClickListener {
            Toast.makeText(this, "Sending", Toast.LENGTH_SHORT).show()
            addODMessageToDatabase()
            finish()
        }
    }

    private fun addODMessageToDatabase() {
        val currentTime = System.currentTimeMillis()
        val fromEmail = from_email_tf.text.toString()
        val sendToEmail = sendToEmailData
        val date = date_tf.text.toString()
        val imageFileUid = UUID.randomUUID().toString()
        val subject = subject_tf.text.toString()
        val content = content_tf.text.toString()
        val sendToEmailAfterAccepted = classHourEmailData
        val status = Status.PENDING.toString()

        val newMessage = ODMessage(currentTime, fromEmail, sendToEmail, date, imageFileUid, subject, content, sendToEmailAfterAccepted, status)

        val ref = FirebaseDatabase.getInstance().reference
        ref.child("messages/${Role.STUDENT.value()}").push()
            .setValue(newMessage)
            .addOnSuccessListener { Log.d("CreateOD", "message added successfully")
                Toast.makeText(this, "Sent", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { Log.d("CreateOD", "message added Fail")
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
            .addOnCanceledListener { Log.d("CreateOD", "message added cancelled") }

        val messageForFaculty = ODMessage(
            createdOn = currentTime,
            fromEmail = fromEmail,
            date = date,
            imageFileUid = imageFileUid,
            subject = subject,
            content = content,
            sendToEmailAfterAccepted = sendToEmailAfterAccepted,
            status = Status.PENDING.toString()
        )
        for (i in 0 until sendToEmail.size) {
            when (i) {
                0 -> { sendODMessageToFaculty(messageForFaculty, Role.HEAD_OF_DEPARTMENT.value()) }
                1 -> { sendODMessageToFaculty(messageForFaculty, Role.EVENT_COORDINATOR.value()) }
                2 -> { sendODMessageToFaculty(messageForFaculty, Role.FACULTY_ADVISER.value()) }
            }
        }
        addImageFileToFirebaseStorage(imageFilePathData, imageFileUid)
    }

    private fun sendODMessageToFaculty(message: ODMessage, role: String) {
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("messages/$role").push()
            .setValue(message)
            .addOnSuccessListener { Log.d("CreateOD", "message sent successfully") }
            .addOnFailureListener { Log.d("CreateOD", "message sent Fail") }
            .addOnCanceledListener { Log.d("CreateOD", "message sent cancelled") }
    }

    private fun addImageFileToFirebaseStorage(imageFilePathData: Uri, imageUid: String) {
        val ref = FirebaseStorage.getInstance().reference.child("images/$imageUid")
        ref.putFile(imageFilePathData)
            .addOnSuccessListener { Log.d("CreateOD", "image successfully added") }
            .addOnFailureListener {  Log.d("CreateOD", "fail to add image") }
    }

    private fun chooseFacultyEmail() {
        val intent = Intent(this, EmailSelectActivity::class.java)
        intent.putStringArrayListExtra("EMAIL", sendToEmailData)
        startActivityForResult(intent, RequestCode.EMAIL)
    }

    private fun chooseClassHour() {
        val intent = Intent(this, ClassHourSelectActivity::class.java)
        intent.putStringArrayListExtra("CLASS_HOUR", classHourEmailData)
        startActivityForResult(intent, RequestCode.CLASS_HOUR)
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), RequestCode.IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RequestCode.EMAIL -> {
                sendToEmailData = data?.getStringArrayListExtra("EMAIL") ?: arrayListOf()
                sendToEmailData.forEach { Log.d("CreateODActivity", it) }
                updateSentToUi(sendToEmailData)
            }
            RequestCode.CLASS_HOUR -> {
                classHourEmailData = data?.getStringArrayListExtra("CLASS_HOUR") ?: arrayListOf()
                classHourEmailData.forEach { Log.d("CreateODActivity", it) }
                updateClassHourUi(classHourEmailData)
            }
            RequestCode.IMAGE -> {
                imageFilePathData = data?.data ?: Uri.EMPTY
                updateImageFileUi(imageFilePathData)
            }
        }
    }

    private fun updateImageFileUi(filePath: Uri) {
        image_file_tf.setText(filePath.toString())
    }

    private fun updateSentToUi(sendToEmailData: ArrayList<String>) {
        var stringEmail = ""
        for (i in 0 until sendToEmailData.size) {
            if (sendToEmailData[i].isNotEmpty()) {
                when (i) {
                    0 -> stringEmail += "${Role.HEAD_OF_DEPARTMENT.value()} - ${sendToEmailData[i]}\n"
                    1 -> stringEmail += "${Role.EVENT_COORDINATOR.value()} - ${sendToEmailData[i]}\n"
                    2 -> stringEmail += "${Role.FACULTY_ADVISER.value()} - ${sendToEmailData[i]}\n"
                }
            }
        }
        to_email_tf.setText(stringEmail)
    }

    private fun updateClassHourUi(classHourEmailData: ArrayList<String>) {
        var stringEmail = ""
        for (i in 0 until classHourEmailData.size) {
            if (classHourEmailData[i].isNotEmpty()) {
                stringEmail += "${i+1} - ${classHourEmailData[i]}\n"
            }
        }
        class_hour_tf.setText(stringEmail)
    }
}
