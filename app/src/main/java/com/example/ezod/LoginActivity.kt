package com.example.ezod

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val _TAG = "LoginActivity"
    private val  firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val emailEditText by lazy { login_email_tf.editText }
    private val passwordEditText by lazy { login_password_tf.editText }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        log_in_btn.setOnClickListener {
            val email = emailEditText?.text.toString()
            val password = passwordEditText?.text.toString()

            when {
                email.isNotBlank() || password.isNotBlank() -> loginUser(email, password)
                else -> showError()
            }
        }

        already_log_in_btn.setOnClickListener {
            intentWithNoBackStack( Intent(this, RegisterActivity::class.java) )
        }
    }

    private fun showError() {
        emailEditText?.error = "Required"
        passwordEditText?.error = "Required"
    }

    private fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            when {
                task.isSuccessful -> loginUserByType(email.isTeacher())
                else -> validateCredentials(task.exception)
            }
        }
    }

    private fun loginUserByType(isTeacher: Boolean) {
        when {
            isTeacher -> intentWithNoBackStack( Intent(this, TeacherActivity::class.java) )
            else -> intentWithNoBackStack( Intent(this, StudentActivity::class.java) )
        }
    }

    private fun validateCredentials(exception: Exception?) {
        when (exception?.message) {
            "There is no user record corresponding to this identifier. The user may have been deleted." -> emailEditText?.error = "Email doesn't exist"
            "The password is invalid or the user does not have a password." -> passwordEditText?.error = "Wrong password"
        }
    }

    private fun intentWithNoBackStack(intent: Intent) {
        intent.addFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun String.isTeacher() = this.contains("ac.in")

}
