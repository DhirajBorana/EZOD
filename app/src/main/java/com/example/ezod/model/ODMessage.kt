package com.example.ezod.model

import java.io.Serializable

data class ODMessage(var uid: String = "",
                     var createdOn: Long = 0L,
                     var fromEmail: String = "",
                     var sendToEmail: ArrayList<String> = arrayListOf(),
                     var date: String = "",
                     var imageFileName: String = "",
                     var imageFileUid: String = "",
                     var subject: String = "",
                     var content: String = "",
                     var sendToEmailAfterAccepted: ArrayList<String> = arrayListOf(),
                     var status: String = "",
                     var user: User = User()) : Serializable