package com.example.ezod.ui.teacher

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.example.ezod.R
import com.example.ezod.model.ODMessage
import com.example.ezod.ui.LoginActivity
import com.example.ezod.ui.MessageActivity
import com.google.firebase.auth.FirebaseAuth

class TeacherActivity : AppCompatActivity() {

    private var userRole = ""
    private var userEmail = ""

    private val fragmentMessageHome = HomeFragment()
    private val fragmentMessageComplete = CompletedFragment()
    private val fragmentMessageAttendance = AttendanceFragment()
    private val fragmentManager = supportFragmentManager

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragmentMessageHome).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_completed -> {
                fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragmentMessageComplete).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_attendance -> {
                fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragmentMessageAttendance).commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)


        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    fun showODMessage(message: ODMessage) {
        val intent = Intent(this, MessageActivity::class.java)
        intent.putExtra("OD_MESSAGE", message)
        intent.putExtra("USER_ROLE", userRole)
        intent.putExtra("USER_EMAIL", userEmail)
        startActivity(intent)
    }

    fun showAttendanceMessage(message: ODMessage) {
        val intent = Intent(this, AttendanceActivity::class.java)
        intent.putExtra("ATTENDANCE_MESSAGE", message)
        intent.putExtra("USER_EMAIL", userEmail)
        startActivity(intent)
    }

    fun setUserRoleAndEmail(role: String, email: String) {
        userRole = role
        userEmail = email
    }

    fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_sign_out) {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        return true
    }
}
