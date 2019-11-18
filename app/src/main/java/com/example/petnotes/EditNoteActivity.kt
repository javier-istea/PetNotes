package com.example.petnotes

import android.os.AsyncTask
import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.example.petnotes.db.DBHelper
import com.example.petnotes.extentions.disable
import com.example.petnotes.model.Note
import kotlinx.android.synthetic.main.activity_edit_note.*

class EditNoteActivity : AppCompatActivity() {
    companion object IntentExtraKeys {
        const val NOTE_ID = "NOTE_ID"
    }

    private lateinit var note: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getIntExtra(NOTE_ID, 0)

        setContentView(R.layout.activity_edit_note)
        setupRadioGroup()
        setupListeners()
        GetNoteAsync().execute(intent.getIntExtra(NOTE_ID, 0))
    }

    private fun setupRadioGroup() {
        var id = 0
        resources.getStringArray(R.array.note_types).forEach {
            val radioButton = RadioButton(this)
            radioButton.text = it
            radioButton.id = id
            rg_editNote_note_type.addView(radioButton)
            id++
        }
    }

    private fun setupListeners() {
        mb_editNote_saveButton.setOnClickListener {
            it.requestFocus()
            if (validateFields()) {
                it.disable()
                UpdateNoteAsync().execute(getValuesFromFields())
            }
        }
        tiet_editNote_title.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                til_editNote_title.error = null
            }
        }
    }

    private fun getValuesFromFields(): Note {
        return Note(
            id = note.id,
            title = tiet_editNote_title.text.toString(),
            message = tiet_editNote_noteContent.text.toString(),
            type = when (rg_editNote_note_type.checkedRadioButtonId) {
                0 -> Note.VACCINE
                1 -> Note.BATH
                2 -> Note.HAIRCUT
                3 -> Note.OTHER
                else -> throw Exception()
            }
        )
    }

    private fun validateFields(): Boolean {
        return validateTitleField() && validateNoteContentField()
    }

    private fun validateNoteContentField(): Boolean {
        if (tiet_editNote_title.text.isNullOrBlank()) {
            til_editNote_title.error = getString(R.string.empty_note_content_message)
        }
        return til_editNote_title.error == null
    }

    private fun validateTitleField(): Boolean {
        if (tiet_editNote_noteContent.text.isNullOrBlank()) {
            til_editNote_noteContent.error = getString(R.string.empty_title_message)
        }
        return til_editNote_noteContent.error == null && tiet_editNote_noteContent.text.toString().length <= til_editNote_noteContent.counterMaxLength

    }


    inner class UpdateNoteAsync : AsyncTask<Note, Unit, Boolean>() {

        override fun doInBackground(vararg params: Note): Boolean {
            return DBHelper(this@EditNoteActivity).updateNote(params[0])
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            this@EditNoteActivity.onNoteUpdated()
        }

    }

    inner class GetNoteAsync : AsyncTask<Int, Unit, Note>() {

        override fun doInBackground(vararg params: Int?): Note {
            return DBHelper(this@EditNoteActivity).getNote(
                params[0] ?: throw Exception("No params")
            ) ?: throw Exception("Note not found")
        }

        override fun onPostExecute(result: Note) {
            super.onPostExecute(result)
            this@EditNoteActivity.onNoteRetrieved(result)
        }

    }

    private fun onNoteRetrieved(note: Note) {
        this.note = note
        updateFields(note)
    }

    private fun updateFields(note: Note) {
        tiet_editNote_title.setText(note.title)
        tiet_editNote_noteContent.setText(note.message)
        rg_editNote_note_type.clearCheck()
        (rg_editNote_note_type.getChildAt(note.type) as RadioButton).isChecked = true
    }

    private fun onNoteUpdated() {
        finish()
    }
}
