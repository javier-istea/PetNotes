package com.example.petnotes.model

import java.io.Serializable

data class Note(
    var id: Int?,
    var title: String,
    var message: String,
    var type: String
) : Serializable