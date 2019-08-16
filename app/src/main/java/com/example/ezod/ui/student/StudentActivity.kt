package com.example.ezod.ui.student

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ezod.R
import com.example.ezod.Role
import com.example.ezod.model.ODMessage
import com.example.ezod.ui.LoginActivity
import com.example.ezod.ui.MessageActivity
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
        setLoadingData()
        getMessageData()
    }

    private fun setLoadingData() {
        val loadingData = mutableListOf<ODMessage>()
        loadingData.add(ODMessage(subject = "Loading . . . ."))
        setUpRecyclerView(loadingData)
    }

    private fun setUpRecyclerView(data: MutableList<ODMessage>) {
        student_message_rv.apply {
            layoutManager = LinearLayoutManager(this@StudentActivity)
            adapter = StudentMessageAdapter(data, this@StudentActivity, object : OnItemClickListener {
                override fun onItemClicked(item: ODMessage) {
                    Log.d("StudentActivity", item.subject)
                    showODMessage(item)
                }
            })
        }
    }

    private fun showODMessage(message: ODMessage) {
        val intent = Intent(this, MessageActivity::class.java)
        intent.putExtra("OD_MESSAGE", message)
        startActivity(intent)
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
