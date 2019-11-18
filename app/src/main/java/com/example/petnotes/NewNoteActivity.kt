package com.example.petnotes

import android.os.AsyncTask
import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.petnotes.db.DBHelper
import com.example.petnotes.extentions.disable
import com.example.petnotes.extentions.enable
import com.example.petnotes.model.Note
import kotlinx.android.synthetic.main.activity_new_note.*

class NewNoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)
        setupRadioGroup()
        setupListeners()
    }

    private fun setupRadioGroup() {
        var id = 0
        resources.getStringArray(R.array.note_types).forEach {
            val radioButton = RadioButton(this)
            radioButton.text = it
            radioButton.id = id
            rg_newNote_note_type.addView(radioButton)
            id++
        }
        (rg_newNote_note_type.getChildAt(0) as RadioButton).isChecked = true
    }

    private fun setupListeners() {
        mb_newNote_saveButton.setOnClickListener {
            it.requestFocus()
            if (validateFields()) {
                it.disable()
                InsertNoteAsync().execute(getValuesFromFields())
            }
        }
        tiet_newNote_title.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                til_newNote_title.error = null
            }
        }
    }

    private fun getValuesFromFields(): Note {
        return Note(
            id = null,
            title = tiet_newNote_title.text.toString(),
            message = tiet_newNote_noteContent.text.toString(),
            type = when (rg_newNote_note_type.checkedRadioButtonId) {
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
        if (tiet_newNote_noteContent.text.isNullOrBlank()) {
            til_newNote_noteContent.error = getString(R.string.empty_note_content_message)
        }
        return til_newNote_noteContent.error == null
    }

    private fun validateTitleField(): Boolean {
        if (tiet_newNote_title.text.isNullOrBlank()) {
            til_newNote_title.error = getString(R.string.empty_title_message)
        }
        return til_newNote_title.error == null && tiet_newNote_title.text.toString().length <= til_newNote_title.counterMaxLength

    }


    inner class InsertNoteAsync : AsyncTask<Note, Unit, Boolean>() {

        override fun doInBackground(vararg params: Note): Boolean {
            return DBHelper(this@NewNoteActivity).insertNote(params[0])
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            this@NewNoteActivity.onNoteInserted(result)
        }

    }

    private fun onNoteInserted(result: Boolean) {
        if (result)
            finish()
        else
            showFailureDialog()
    }

    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.generic_failure_dialog_title)
            .setMessage(R.string.generic_failure_dialog_message)
            .setPositiveButton(R.string.ok) { dialogInterface, _ ->
                mb_newNote_saveButton.enable()
                dialogInterface.dismiss()
            }
            .show()
    }
}
