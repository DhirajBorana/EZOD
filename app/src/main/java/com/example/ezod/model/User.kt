package com.example.ezod.model

import java.io.Serializable

data class User(var uid: String = "",
                var registerId: String = "",
                var name: String = "",
                var email: String = "",
                var isTeacher: Boolean = false,
                var role: String = "") : Serializable