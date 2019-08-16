package com.example.ezod.ui.teacher


import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.ezod.R
import com.example.ezod.Role
import com.example.ezod.Status
import com.example.ezod.model.ODMessage
import com.example.ezod.model.User
import com.example.ezod.ui.student.OnItemClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_attendance.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class AttendanceFragment : Fragment() {

    private var userRole: String = ""
    private var userEmail: String = ""
    val userMessageDataIndex = mutableListOf<Int>()
    val userMessageDataEmail = mutableListOf<String>()
    val userMessageDataUid = mutableListOf<String>()
    val userMessageDataStatus = mutableListOf<Int>()



    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as TeacherActivity).setActionBarTitle("Attendance")

        val rootView =  inflater.inflate(R.layout.fragment_attendance, container, false)
        recyclerView = rootView.teacher_attendance_message_rv

        setLoadingData()

        return rootView
    }

    private fun setLoadingData() {
        clearData()
        val loadingData = mutableListOf<ODMessage>()
        loadingData.add(ODMessage(user = User(name = "Loading . . . .")))
        setUpRecyclerView(loadingData)
        getUserRole()
    }

    private fun clearData() {
        userMessageDataIndex.clear()
        userMessageDataEmail.clear()
        userMessageDataStatus.clear()
        userMessageDataUid.clear()
    }

    private fun getUserRole() {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { Log.d("HomeFragment", "Cancelled") }

            override fun onDataChange(p0: DataSnapshot) {
                val roles = p0.children
                for ( role in roles) {
                    if (role.key == Role.STUDENT.value()) continue
                    for (data in role.children) {
                        val user = data.getValue(User::class.java) ?: User()
                        if (user.uid == userUid) {
                            userRole = user.role
                            userEmail = user.email
                            getUserMessageData()
                            Log.d("HomeFragment", user.email)
                        }
                    }
                }
            }
        })
    }

    private fun getUserMessageData() {
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("messages/${Role.STUDENT.value()}")
            .addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { Log.d("AttendanceFragment", "get message data Cancelled") }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children) {
                    val uid = data.key ?: ""
                    val message = data.getValue(ODMessage::class.java) ?: ODMessage()
                    for ( (index, email) in message.sendToEmailAfterAccepted.withIndex()) {
                        if (email == userEmail) {
                            userMessageDataEmail.add(email)
                            userMessageDataIndex.add(index)
                            userMessageDataUid.add(uid)
                            userMessageDataStatus.add(0)
                        }
                    }
                }
                getAttendanceData()
            }
        })
    }

    private fun getAttendanceData() {
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("messages")
            .addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { Log.d("AttendanceFragment", "Attendance data Cancelled") }

            override fun onDataChange(p0: DataSnapshot) {
                val messageData = mutableListOf<ODMessage>()
                val roles = p0.children
                for ( role in roles) {
                    if (role.key == Role.HEAD_OF_DEPARTMENT.value() || role.key == Role.EVENT_COORDINATOR.value()) {
                        for (data in role.children) {
                            val message = data.getValue(ODMessage::class.java) ?: ODMessage()
                            val index = userMessageDataUid.indexOf(data.key)
                            if (index != -1 && message.status != Status.PENDING.toString()) {
                                userMessageDataStatus[index] += 1
                                if (userMessageDataStatus[index] == 2) {
                                    messageData.add(message)
                                }
                            }
                        }
                    }
                }
                if (messageData.isEmpty()) {
                    setUpRecyclerView(mutableListOf(ODMessage(user = User(name = "No Messages"))))
                } else {
                    setUpRecyclerView(messageData.filter { it.status == Status.APPROVED.toString() }.toMutableList())
                    addAttendanceDataToFirebaseDatabase(messageData)
                }
            }
        })
    }

    private fun addAttendanceDataToFirebaseDatabase(messageData: MutableList<ODMessage>) {
        val professorRef = FirebaseDatabase.getInstance().getReference("messages/${Role.PROFESSOR.value()}")
        val studentRef = FirebaseDatabase.getInstance().getReference("messages/${Role.STUDENT.value()}")
        messageData.forEach {
            professorRef.child(it.uid).setValue(it)
            studentRef.child(it.uid).setValue(it)
        }
    }

    private fun setUpRecyclerView(messageData: MutableList<ODMessage>) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = TeacherMessageAdapter(messageData, object : OnItemClickListener {
                override fun onItemClicked(item: ODMessage) {
                    (activity as TeacherActivity).showAttendanceMessage(item)
                }
            })
        }
    }

}

