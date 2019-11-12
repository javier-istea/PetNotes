package com.example.petnotes

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.petnotes.db.DBHelper
import com.example.petnotes.model.Note

class NewNoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)
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
            .setPositiveButton(R.string.ok) { dialogInterface, i -> dialogInterface.dismiss() }
            .show()
    }
}
