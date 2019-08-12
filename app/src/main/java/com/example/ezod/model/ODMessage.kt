package com.example.ezod.model

import java.io.Serializable

data class ODMessage(var createdOn: Long = 0L,
                     var fromEmail: String = "",
                     var sendToEmail: ArrayList<String> = arrayListOf(),
                     var date: String = "",
                     var imageFileUid: String = "",
                     var subject: String = "",
                     var content: String = "",
                     var sendToEmailAfterAccepted: ArrayList<String> = arrayListOf(),
                     var status: String = "") : Serializable