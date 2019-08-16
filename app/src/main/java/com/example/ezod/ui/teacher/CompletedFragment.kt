package com.example.ezod.ui.teacher


import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_image_view.*
import kotlinx.android.synthetic.main.fragment_completed.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class CompletedFragment : Fragment() {

    private var userRole: String = ""
    private var userEmail: String = ""

    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as TeacherActivity).setActionBarTitle("Completed")

        val rootView= inflater.inflate(R.layout.fragment_completed, container, false)
        recyclerView = rootView.teacher_completed_message_rv

        setLoadingData()

        return rootView
    }

    private fun setLoadingData() {
        val loadingData = mutableListOf<ODMessage>()
        loadingData.add(ODMessage(user = User(name = "Loading . . . .")))
        setUpRecyclerView(loadingData)
        getUserRole()
    }

    private fun getUserRole() {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { Log.d("CompletedFragment", "Cancelled") }

            override fun onDataChange(p0: DataSnapshot) {
                val roles = p0.children
                for ( role in roles) {
                    if (role.key == Role.STUDENT.value()) continue
                    for (data in role.children) {
                        val user = data.getValue(User::class.java) ?: User()
                        if (user.uid == userUid) {
                            userRole = user.role
                            userEmail = user.email
                            (activity as TeacherActivity).setUserRoleAndEmail(userRole, userEmail)
                            if (userRole != Role.PROFESSOR.value()) getMessageData()
                            else setUpRecyclerView(mutableListOf(ODMessage(user = User(name = "No Messages"))))
                            Log.d("CompletedFragment", user.email)
                        }
                    }
                }
            }
        })
    }

    private fun getMessageData() {
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("messages/$userRole")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val messageData = mutableListOf<ODMessage>()
                    for (data in p0.children)  {
                        val message = data.getValue(ODMessage::class.java) ?: ODMessage()
                        if (message.sendToEmail.contains(userEmail) && message.status != Status.PENDING.toString()) {
                            messageData.add(message)
                        }
                    }
                    if (messageData.isEmpty()) {
                        setUpRecyclerView(mutableListOf(ODMessage(user = User(name = "No Messages"))))
                    } else setUpRecyclerView(messageData)
                }
                override fun onCancelled(p0: DatabaseError) { Log.d("CompletedFragment", "get message data cancelled") }
            })
    }

    private fun setUpRecyclerView(messageData: MutableList<ODMessage>) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = TeacherMessageAdapter(messageData, object : OnItemClickListener {
                override fun onItemClicked(item: ODMessage) {
                    Log.d("CompletedFragment", item.subject)
                    (activity as TeacherActivity).showODMessage(item)
                }
            })
        }
    }

}
