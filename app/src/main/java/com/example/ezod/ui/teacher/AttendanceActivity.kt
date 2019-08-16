package com.example.ezod.ui.teacher

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ezod.R
import com.example.ezod.model.ODMessage
import kotlinx.android.synthetic.main.activity_attendance.*

class AttendanceActivity : AppCompatActivity() {

    private var userEmail = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)
        supportActionBar?.title = ""
        getAttendanceMessage()
    }

    private fun getAttendanceMessage() {
        val intent = intent
        val attendanceMessage = intent.getSerializableExtra("ATTENDANCE_MESSAGE") as ODMessage
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""
        updateUi(attendanceMessage)
    }

    private fun updateUi(attendanceMessage: ODMessage) {
        val studentName = attendanceMessage.user.name
        val studentRegisterId = attendanceMessage.user.registerId
        val date = attendanceMessage.date
        var classHour = ""

        attendanceMessage.sendToEmailAfterAccepted.forEachIndexed { index, s ->
            if (s == userEmail) classHour += "${index + 1}, "
        }
        classHour += " Class Hour"

        attendance_user_name_tv.text = studentName
        attendance_user_register_id_tv.text = studentRegisterId
        attendance_attendance_for_tv.text = classHour
        attendance_date_tv.text = date
    }
}
