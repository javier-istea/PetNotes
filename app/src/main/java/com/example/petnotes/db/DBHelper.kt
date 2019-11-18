package com.example.petnotes.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.petnotes.model.Note

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DBConstants.DB_NAME, null, DBConstants.DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "create table if not exists ${DBConstants.TABLE_NOTES}" +
                    " (${DBConstants.ID} integer primary key, ${DBConstants.TITLE} text, " +
                    "${DBConstants.TYPE} integer, " +
                    "${DBConstants.MESSAGE} text)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db?.execSQL("drop table ${DBConstants.TABLE_NOTES}")
            onCreate(db)
        }
    }

    fun insertNote(note: Note): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DBConstants.TITLE, note.title)
        contentValues.put(DBConstants.MESSAGE, note.message)
        contentValues.put(DBConstants.TYPE, note.type)
        val result = db.insert(DBConstants.TABLE_NOTES, null, contentValues)
        return result > 0
    }

    fun getNotes(): List<Note> {
        val db = this.readableDatabase
        val notes = ArrayList<Note>()
        val cursor = db.rawQuery("select * from ${DBConstants.TABLE_NOTES}", null)

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val note = Note(
                    id = cursor.getInt(cursor.getColumnIndex(DBConstants.ID)),
                    title = cursor.getString(cursor.getColumnIndex(DBConstants.TITLE)),
                    message = cursor.getString(cursor.getColumnIndex(DBConstants.MESSAGE)),
                    type = cursor.getInt(cursor.getColumnIndex(DBConstants.TYPE))
                )
                notes.add(note)
                cursor.moveToNext()
            }
        }
        cursor.close()

        return notes
    }

    fun getNote(noteId: Int): Note? {
        val db = this.readableDatabase
        db.rawQuery(
            "select * from ${DBConstants.TABLE_NOTES} where ${DBConstants.ID} = $noteId",
            null
        ).use {
            if (it.moveToFirst()) {
                return Note(
                    id = it.getInt(it.getColumnIndex(DBConstants.ID)),
                    title = it.getString(it.getColumnIndex(DBConstants.TITLE)),
                    message = it.getString(it.getColumnIndex(DBConstants.MESSAGE)),
                    type = it.getInt(it.getColumnIndex(DBConstants.TYPE))
                )
            }
        }
        return null
    }

    fun updateNote(note: Note): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(DBConstants.TITLE, note.title)
        contentValues.put(DBConstants.MESSAGE, note.message)
        contentValues.put(DBConstants.TYPE, note.type)

        return db.update(
            DBConstants.TABLE_NOTES,
            contentValues,
            "${DBConstants.ID} = ?",
            arrayOf(note.id.toString())
        ) > 0
    }

    fun deleteNote(noteId: Int): Boolean {
        val db = this.writableDatabase

        return db.delete(
            DBConstants.TABLE_NOTES,
            "${DBConstants.ID} = ?",
            arrayOf(noteId.toString())
        ) > 0
    }

}