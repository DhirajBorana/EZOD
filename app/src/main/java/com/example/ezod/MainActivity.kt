package com.example.ezod

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verifyUserLogin()
    }

    private fun verifyUserLogin() {
        when (val currentUser = FirebaseAuth.getInstance().currentUser) {
            null -> intentWithNoBackStack(Intent(this, LoginActivity::class.java))
            else -> loginUserByType(currentUser.email?.isTeacher() ?: false)
        }
    }

    private fun loginUserByType(isTeacher: Boolean) {
        when {
            isTeacher -> intentWithNoBackStack( Intent(this, TeacherActivity::class.java) )
            else -> intentWithNoBackStack( Intent(this, StudentActivity::class.java) )
        }
    }

    private fun intentWithNoBackStack(intent: Intent) {
        intent.addFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun String.isTeacher() = this.contains("ac.in")
}
