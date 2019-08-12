package com.example.ezod.ui.student

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ezod.R
import com.example.ezod.Role
import com.example.ezod.model.ODMessage
import com.example.ezod.ui.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_student.*


class StudentActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)
        setOnClickListeners()
        getMessageData()
    }

    private fun setUpRecyclerView(data: MutableList<ODMessage>) {
        student_message_rv.apply {
            layoutManager = LinearLayoutManager(this@StudentActivity)
            adapter = StudentMessageAdapter(data, this@StudentActivity)
        }
    }

    private fun getMessageData() {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("messages/${Role.STUDENT.value()}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val messageData = mutableListOf<ODMessage>()
                    val messageList = p0.children
                    for (data in messageList)  {
                        val message = data.getValue(ODMessage::class.java) ?: ODMessage()
                        if (message.fromEmail == currentUserEmail) { messageData.add(message) }
                    }
                    setUpRecyclerView(messageData)
                }
                override fun onCancelled(p0: DatabaseError) { Log.d("StudentActivity", "get message data cancelled") }
            })
    }

    private fun setOnClickListeners() {
        create_od_fab.setOnClickListener { startActivity(Intent(this, CreateODActivity::class.java)) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_sign_out) logoutUser()
        return true
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        intentWithNoBackStack( Intent(this, LoginActivity::class.java) )
    }

    private fun intentWithNoBackStack(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

}
