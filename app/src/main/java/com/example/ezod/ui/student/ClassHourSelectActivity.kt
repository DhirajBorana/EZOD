package com.example.ezod.ui.student

import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_class_hour_select.*
import kotlinx.android.synthetic.main.activity_register.*

class ClassHourSelectActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_hour_select)

        val classHourEmailData = intent.getStringArrayListExtra("CLASS_HOUR") ?: arrayListOf()
        updateUi(classHourEmailData)
        getProfessors()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        class_hour_check_fab.setOnClickListener {

            val array = arrayListOf(
                first_hour_email_tf.text.toString(),
                second_hour_email_tf.text.toString(),
                third_hour_email_tf.text.toString(),
                fourth_hour_email_tf.text.toString(),
                fifth_hour_email_tf.text.toString(),
                sixth_hour_email_tf.text.toString(),
                seventh_hour_email_tf.text.toString())

            intent.putStringArrayListExtra("CLASS_HOUR", array)
            setResult(CreateODActivity.RequestCode.RESULT_OK, intent)
            finish()
        }
    }

    private fun updateUi(classHourEmailData: ArrayList<String>) {
        if (classHourEmailData.isNotEmpty()) {
            first_hour_email_tf.setText(classHourEmailData[0])
            second_hour_email_tf.setText(classHourEmailData[1])
            third_hour_email_tf.setText(classHourEmailData[2])
            fourth_hour_email_tf.setText(classHourEmailData[3])
            fifth_hour_email_tf.setText(classHourEmailData[4])
            sixth_hour_email_tf.setText(classHourEmailData[5])
            seventh_hour_email_tf.setText(classHourEmailData[6])
        }
    }

    private fun getProfessors(): Array<String> {
        val professors = mutableListOf<String>()

        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("ClassHourSelectActivity", "Cancelled")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val roles = p0.children
                for ( role in roles) {
                    if (role.key == Role.STUDENT.value()) continue
                    for (data in role.children) {
                        val user = data.getValue(User::class.java) ?: User()
                        professors.add(user.email)
                        Log.d("ClassHourSelectActivity", user.email)
                    }
                }
                if (professors.isNotEmpty()) {
                    setArrayAdapter(professors.toTypedArray())
                }
            }
        })
        return professors.toTypedArray()
    }

    private fun setArrayAdapter(professors: Array<String>) {
        val adapter = ArrayAdapter(this, R.layout.popup_menu_dropdown_item, professors)
        first_hour_email_tf.setAdapter(adapter)
        second_hour_email_tf.setAdapter(adapter)
        third_hour_email_tf.setAdapter(adapter)
        fourth_hour_email_tf.setAdapter(adapter)
        fifth_hour_email_tf.setAdapter(adapter)
        sixth_hour_email_tf.setAdapter(adapter)
        seventh_hour_email_tf.setAdapter(adapter)
    }
}
