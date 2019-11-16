package com.example.petnotes

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var floatingActionButton: FloatingActionButton

    private lateinit var toolbar: Toolbar

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        setContentView(R.layout.activity_main)
        setupToolbar()
        setupFloatingActionButton()
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    private fun setupToolbar() {
        toolbar = findViewById(R.id.tb_main)
        val petName = prefs.getString(getString(R.string.preference_pet_name), null)
        if (petName.isNullOrBlank()){
            toolbar.title = String.format(getString(R.string.main_title), petName)
        }
        setSupportActionBar(toolbar)
    }

}
