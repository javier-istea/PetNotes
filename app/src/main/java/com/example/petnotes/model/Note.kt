package com.example.petnotes.model

import java.io.Serializable

data class Note(
    var id: Int?,
    var title: String,
    var message: String,
    var type: Int,
    var creationDate: Long,
    var reminderDate: Long?
) : Serializable {
    companion object NoteTypes {
        const val VACCINE = 0
        const val BATH = 1
        const val HAIRCUT = 2
        const val OTHER = 3
    }
}
