package com.example.petnotes

import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petnotes.db.DBHelper
import com.example.petnotes.model.Note
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ItemInteractionListener {

    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView

    private lateinit var toolbar: Toolbar

    private lateinit var prefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        setContentView(R.layout.activity_main)
        setupToolbar()
        setupFloatingActionButton()
        setupRecycler()
        GetAllNotes().execute()
    }

    private fun setupRecycler() {
        recyclerView = findViewById(R.id.rv_main)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onDeleteButtonCLick(id: Int?) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_note_confirmation_dialog_title)
            .setMessage(R.string.delete_note_confirmation_dialog_message)
            .setPositiveButton(R.string.delete) { dialogInterface, _ ->
                deleteNote(id)
                dialogInterface.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun deleteNote(id: Int?) {
        DeleteNoteAsync().execute(id)
    }

    override fun onEditButtonCLick(id: Int?) {
        val intent = Intent(this, EditNoteActivity::class.java)
        intent.putExtra(EditNoteActivity.NOTE_ID, id)
        startActivity(intent)
    }

    private fun setupFloatingActionButton() {
        floatingActionButton = findViewById(R.id.fab_main_new_note)
        floatingActionButton.setOnClickListener {
            startActivity(Intent(this, NewNoteActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return false
            }
            R.id.action_about_me -> {
                startActivity(Intent(this, AboutMeActivity::class.java))
                return false
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    private fun setupToolbar() {
        toolbar = findViewById(R.id.tb_main)
        updateToolbarTitle()
        setSupportActionBar(toolbar)
    }

    private fun updateToolbarTitle() {
        val petName = prefs.getString(getString(R.string.preference_pet_name), null)
        if (!petName.isNullOrBlank())
            toolbar.title = String.format(getString(R.string.main_title), petName)
    }

    inner class GetAllNotes : AsyncTask<Unit, Unit, List<Note>>() {

        override fun doInBackground(vararg params: Unit): List<Note> {
            return DBHelper(this@MainActivity).getNotes()
        }

        override fun onPostExecute(result: List<Note>) {
            super.onPostExecute(result)
            this@MainActivity.onNotesRetrieved(result)
        }

    }

    inner class DeleteNoteAsync : AsyncTask<Int, Unit, Boolean>() {

        override fun doInBackground(vararg params: Int?): Boolean {
            return DBHelper(this@MainActivity).deleteNote(params[0] ?: throw Exception("No params"))
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            this@MainActivity.onNoteDeleted()
        }

    }

    private fun onNoteDeleted() {
        GetAllNotes().execute()
    }

    private fun onNotesRetrieved(result: List<Note>) {
        if (result.isEmpty()) {
            lav_no_notes_animation.visibility = View.VISIBLE
            tv_no_notes.visibility = View.VISIBLE
        } else {
            lav_no_notes_animation.visibility = View.GONE
            tv_no_notes.visibility = View.GONE
        }
        recyclerView.adapter = MainRecyclerAdapter(result, this)
    }

    override fun onResume() {
        super.onResume()
        updateToolbarTitle()
        GetAllNotes().execute()
    }
}
