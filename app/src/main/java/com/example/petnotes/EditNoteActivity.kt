package com.example.petnotes

import android.app.DatePickerDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.example.petnotes.db.DBHelper
import com.example.petnotes.extentions.disable
import com.example.petnotes.model.Note
import kotlinx.android.synthetic.main.activity_edit_note.*
import java.util.*


class EditNoteActivity : AppCompatActivity() {
    companion object IntentExtraKeys {
        const val NOTE_ID = "NOTE_ID"
    }

    private lateinit var note: Note
    private lateinit var reminderDate: Calendar

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
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            currentFocus?.clearFocus()
            if (validateFields()) {
                it.disable()
                UpdateNoteAsync().execute(getValuesFromFields())
            }
        }
        tiet_editNote_date.setOnClickListener {
            showDatePickerDialog(if (reminderDate.timeInMillis == 0L) Calendar.getInstance() else reminderDate)
        }
        tiet_editNote_title.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                til_editNote_title.error = null
            }
        }
        tiet_editNote_noteContent.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                til_editNote_noteContent.error = null
            }
        }
    }

    private fun showDatePickerDialog(calendar: Calendar) {
        DatePickerDialog(
            this,
            R.style.Theme_MaterialComponents_Light_Dialog_MinWidth,
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                reminderDate = Calendar.getInstance().apply { this.set(year, month, day) }
                tiet_editNote_date.setText((String.format("%s/%s/%s", day, month + 1, year)))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun getValuesFromFields(): Note {
        return Note(
            id = note.id,
            title = tiet_editNote_title.text.toString().trim(),
            message = tiet_editNote_noteContent.text.toString(),
            type = when (rg_editNote_note_type.checkedRadioButtonId) {
                0 -> Note.VACCINE
                1 -> Note.BATH
                2 -> Note.HAIRCUT
                3 -> Note.OTHER
                else -> throw Exception()
            },
            creationDate = note.creationDate,
            reminderDate = reminderDate.timeInMillis
        )
    }

    private fun validateFields(): Boolean {
        return validateTitleField() and validateNoteContentField()
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


    inner class UpdateNoteAsync : AsyncTask<Note, Unit, Note?>() {

        override fun doInBackground(vararg params: Note): Note? {
            return if (DBHelper(this@EditNoteActivity).updateNote(params[0])) params[0] else null
        }

        override fun onPostExecute(result: Note?) {
            super.onPostExecute(result)
            this@EditNoteActivity.onNoteUpdated(result)
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
        note.reminderDate?.let {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            this.reminderDate = calendar
        }
        updateFields(note)
    }

    private fun updateFields(note: Note) {
        tiet_editNote_title.setText(note.title)
        tiet_editNote_noteContent.setText(note.message)
        rg_editNote_note_type.clearCheck()
        (rg_editNote_note_type.getChildAt(note.type) as RadioButton).isChecked = true
        note.reminderDate?.let {
            if (it != 0L) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it
                tiet_editNote_date.setText(
                    String.format(
                        "%s/%s/%s",
                        calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.YEAR)
                    )
                )
            }
        }
    }

    private fun onNoteUpdated(note: Note?) {
        note?.reminderDate?.let {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            if (calendar.compareTo(Calendar.getInstance()) < 1)
                NotificationHandler.showNotification(this, note.title)
            else
                NotificationHandler.scheduleNotification(this, note)
        }
        finish()
    }
}
