package com.example.petnotes.model

import java.io.Serializable

data class Note(
    var id: Int?,
    var title: String,
    var message: String,
    var type: Int
) : Serializable {
    companion object NoteTypes {
        const val VACCINE = 0
        const val OTHER = 1
        const val BATH = 2
        const val HAIRCUT = 3
    }
}
