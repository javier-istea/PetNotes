package com.example.petnotes.model

import java.io.Serializable

data class Note(
    var id: Int?,
    var title: String,
    var message: String,
    var type: Int
) : Serializable{
    companion object NoteTypes{
        val VACCINE = 0
        val VET_VISIT = 1
        val BATH = 2
        val HAIRCUT = 3
    }
}
