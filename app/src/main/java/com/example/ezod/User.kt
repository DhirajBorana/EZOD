package com.example.ezod

data class User(var uid: String = "",
                var registerId: String = "",
                var name: String = "",
                var isTeacher: Boolean = false,
                var role: String = "")