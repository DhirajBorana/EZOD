package com.example.ezod.ui.student

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.example.ezod.R
import com.example.ezod.Role
import com.example.ezod.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_email_select.*

class EmailSelectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_select)

        val sendToEmailData = intent.getStringArrayListExtra("EMAIL") ?: arrayListOf()
        updateUi(sendToEmailData)
        getProfessors()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        email_check_fab.setOnClickListener {
            val array = arrayListOf(
                hod_email_tf.text.toString(),
                event_coordinator_email_tf.text.toString(),
                faculty_adviser_email_tf.text.toString())

            intent.putStringArrayListExtra("EMAIL", array)
            setResult(CreateODActivity.RequestCode.RESULT_OK, intent)
            finish()
        }
    }

    private fun updateUi(sendToEmailData: ArrayList<String>) {
        if (sendToEmailData.isNotEmpty()) {
            hod_email_tf.setText(sendToEmailData[0])
            event_coordinator_email_tf.setText(sendToEmailData[1])
            faculty_adviser_email_tf.setText(sendToEmailData[2])
        }
    }

    private fun getProfessors() {
        val professors = hashMapOf<String, MutableList<String>>()

        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { Log.d("ClassHourSelectActivity", "Cancelled") }

            override fun onDataChange(p0: DataSnapshot) {
                val roles = p0.children
                for ( role in roles) {
                    val listOfFaculties = mutableListOf<String>()
                    when {
                        role.key == Role.HEAD_OF_DEPARTMENT.value() -> {
                            for (data in role.children) {
                                val user = data.getValue(User::class.java) ?: User()
                                listOfFaculties.add(user.email)
                                Log.d("EmailSelectActivity", user.email)
                            }
                            professors[Role.HEAD_OF_DEPARTMENT.value()] = listOfFaculties
                        }
                        role.key == Role.EVENT_COORDINATOR.value() -> {
                            for (data in role.children) {
                                val user = data.getValue(User::class.java) ?: User()
                                listOfFaculties.add(user.email)
                                Log.d("EmailSelectActivity", user.email)
                            }
                            professors[Role.EVENT_COORDINATOR.value()] = listOfFaculties
                        }
                        role.key == Role.FACULTY_ADVISER.value() -> {
                            for (data in role.children) {
                                val user = data.getValue(User::class.java) ?: User()
                                listOfFaculties.add(user.email)
                                Log.d("EmailSelectActivity", user.email)
                            }
                            professors[Role.FACULTY_ADVISER.value()] = listOfFaculties
                        }
                    }
                }
                if (professors.isNotEmpty()) {
                    setArrayAdapter(professors)
                }
            }

        })
    }

    private fun setArrayAdapter(professors: HashMap<String, MutableList<String>>) {
        val adapterHOD = ArrayAdapter(this, R.layout.popup_menu_dropdown_item, professors.getValue(Role.HEAD_OF_DEPARTMENT.value()).toTypedArray())
        val adapterEventCoordinator = ArrayAdapter(this, R.layout.popup_menu_dropdown_item, professors.getValue(Role.EVENT_COORDINATOR.value()).toTypedArray())
        val adapterFacultyAdviser = ArrayAdapter(this, R.layout.popup_menu_dropdown_item, professors.getValue(Role.FACULTY_ADVISER.value()).toTypedArray())

        hod_email_tf.setAdapter(adapterHOD)
        event_coordinator_email_tf.setAdapter(adapterEventCoordinator)
        faculty_adviser_email_tf.setAdapter(adapterFacultyAdviser)
    }

}
