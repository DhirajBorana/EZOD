package com.example.ezod

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private val _TAG = "RegisterActivity"
    private val  firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val nameEditText by lazy { register_name_tf.editText }
    private val emailEditText by lazy { register_email_tf.editText }
    private val passwordEditText by lazy { register_password_tf.editText }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val roles = arrayOf(
            Role.STUDENT.value(),
            Role.HEAD_OF_DEPARTMENT.value(),
            Role.EVENT_COORDINATOR.value(),
            Role.FACULTY_ADVISER.value(),
            Role.PROFESSOR.value())

        val adapter = ArrayAdapter(this, R.layout.popup_menu_dropdown_item, roles)
        roles_dropdown.setAdapter(adapter)

        sign_up_btn.setOnClickListener {
            val name = nameEditText?.text.toString()
            val email = emailEditText?.text.toString()
            val password = passwordEditText?.text.toString()
            val role = roles_dropdown.text.toString()

            when {
                name.isBlank() || email.isBlank() || password.isBlank() || role.isBlank() -> showError(name, email, password, role)
                else -> createNewUser(name, email, password, role)
            }
        }

        already_sign_up_btn.setOnClickListener {
            intentWithNoBackStack( Intent(this, LoginActivity::class.java) )
        }

    }

    private fun showError(name: String, email: String, password: String, role: String) {
        when {
            name.isEmpty() -> nameEditText?.error = "Required"
            email.isEmpty() -> emailEditText?.error = "Required"
            password.isEmpty() -> passwordEditText?.error = "Required"
            role.isEmpty() -> roles_dropdown?.error = "Required"
        }
    }

    private fun createNewUser(name: String, email: String, password: String, role: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            when {
                task.isSuccessful -> {
                    val isTeacher = email.isTeacher()
                    when {
                        isTeacher -> addUserToDatabase(name, isTeacher, role)
                        else -> addUserToDatabase(name, isTeacher)
                    }
                    loginUserByType(isTeacher)
                }
                else -> validateCredentials(task.exception)
            }
        }
    }

    private fun addUserToDatabase(userName: String, isTeacher: Boolean, role: String = Role.STUDENT.value()) {
        val uid = firebaseAuth.currentUser?.uid ?: ""
        val user = User(uid, userName, isTeacher, role)

        FirebaseDatabase.getInstance().getReference("users/$uid")
            .setValue(user)
            .addOnSuccessListener { Log.d(_TAG, "User data Successfully added") }
            .addOnFailureListener { Log.d(_TAG, "Failed to add User data: ${it.message}") }
            .addOnCanceledListener { Log.d(_TAG, "User data canceled") }
    }

    private fun loginUserByType(isTeacher: Boolean) {
        when {
            isTeacher -> intentWithNoBackStack( Intent(this, TeacherActivity::class.java) )
            else -> intentWithNoBackStack( Intent(this, StudentActivity::class.java) )
        }
    }

    private fun validateCredentials(exception: Exception?) {
        Log.d(_TAG, "${exception?.message}")
        when (exception?.message) {
            "The email address is already in use by another account." -> emailEditText?.error = "Email is already in use"
            "The email address is badly formatted." -> emailEditText?.error = "Invalid email"
            "The given password is invalid. [ Password should be at least 6 characters ]" -> passwordEditText?.error = "Password should be at least 6 characters"
        }
    }

    private fun intentWithNoBackStack(intent: Intent) {
        intent.addFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun String.isTeacher() = this.contains("ac.in")
}