package com.example.ezod.ui.teacher

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.example.ezod.R
import com.example.ezod.ui.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class TeacherActivity : AppCompatActivity() {

    private lateinit var textMessage: TextView
    private val fragmentMessageHome = MessageFragment()
    private val fragmentMessageComplete = BlankFragment()
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
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
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
