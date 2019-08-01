package com.example.ezod

enum class Role {
    STUDENT {
        override fun value() = "Student"
    },
    HEAD_OF_DEPARTMENT {
        override fun value() = "Head of Department"
    },
    EVENT_COORDINATOR {
        override fun value() = "Event Coordinator"
    },
    FACULTY_ADVISER {
        override fun value() = "Faculty Adviser"
    },
    PROFESSOR {
        override fun value() = "Professor"
    };

    abstract fun value(): String
}