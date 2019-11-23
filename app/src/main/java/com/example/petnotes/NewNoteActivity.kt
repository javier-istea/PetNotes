package com.example.petnotes

import android.app.DatePickerDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.petnotes.db.DBHelper
import com.example.petnotes.extentions.disable
import com.example.petnotes.extentions.enable
import com.example.petnotes.model.Note
import kotlinx.android.synthetic.main.activity_new_note.*
import java.util.*

class NewNoteActivity : AppCompatActivity() {

    private var reminderDate: Calendar? = null

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
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            currentFocus?.clearFocus()
            if (validateFields()) {
                it.disable()
                InsertNoteAsync().execute(getValuesFromFields())
            }
        }
        tiet_newNote_date.setOnClickListener {
            showDatePickerDialog(reminderDate ?: Calendar.getInstance())
        }
        tiet_newNote_title.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                til_newNote_title.error = null
            }
        }
        tiet_newNote_noteContent.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                til_newNote_noteContent.error = null
            }
        }
    }

    private fun showDatePickerDialog(calendar: Calendar) {
        DatePickerDialog(
            this,
            R.style.Theme_MaterialComponents_Light_Dialog_MinWidth,
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                reminderDate = Calendar.getInstance().apply { this.set(year, month, day) }
                tiet_newNote_date.setText((String.format("%s/%s/%s", day, month + 1, year)))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun getValuesFromFields(): Note {
        return Note(
            id = null,
            title = tiet_newNote_title.text.toString().trim(),
            message = tiet_newNote_noteContent.text.toString(),
            type = when (rg_newNote_note_type.checkedRadioButtonId) {
                0 -> Note.VACCINE
                1 -> Note.BATH
                2 -> Note.HAIRCUT
                3 -> Note.OTHER
                else -> throw Exception()
            },
            creationDate = Calendar.getInstance().timeInMillis,
            reminderDate = reminderDate?.timeInMillis
        )
    }

    private fun validateFields(): Boolean {
        return validateTitleField() and validateNoteContentField()
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
            params[0].reminderDate?.let {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it
                if (calendar.compareTo(Calendar.getInstance()) < 1)
                    NotificationHandler.showNotification(this@NewNoteActivity, params[0].title)
                else
                    NotificationHandler.scheduleNotification(this@NewNoteActivity, params[0])
            }
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
