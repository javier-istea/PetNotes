package com.example.petnotes.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.petnotes.model.Note
import com.example.petnotes.model.User

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DBConstants.DB_NAME, null, DBConstants.DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "create table if not exists ${DBConstants.TABLE_NOTES}" +
                    " (${DBConstants.NOTE_ID} integer primary key, ${DBConstants.TITLE} text, " +
                    "${DBConstants.TYPE} integer, " +
                    "${DBConstants.CREATION_DATE} integer, " +
                    "${DBConstants.REMINDER_DATE} integer, " +
                    "${DBConstants.MESSAGE} text)"
        )
        db?.execSQL(
            "create table if not exists ${DBConstants.TABLE_USERS}" +
                    " (${DBConstants.USER_ID} integer primary key, ${DBConstants.USER_NAME} text, " +
                    "${DBConstants.USER_PASSWORD} text)"
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
        contentValues.put(DBConstants.CREATION_DATE, note.creationDate)
        contentValues.put(DBConstants.REMINDER_DATE, note.reminderDate)
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
                    id = cursor.getInt(cursor.getColumnIndex(DBConstants.NOTE_ID)),
                    title = cursor.getString(cursor.getColumnIndex(DBConstants.TITLE)),
                    message = cursor.getString(cursor.getColumnIndex(DBConstants.MESSAGE)),
                    type = cursor.getInt(cursor.getColumnIndex(DBConstants.TYPE)),
                    creationDate = cursor.getLong(cursor.getColumnIndex(DBConstants.CREATION_DATE)),
                    reminderDate = cursor.getLong(cursor.getColumnIndex(DBConstants.REMINDER_DATE))
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
            "select * from ${DBConstants.TABLE_NOTES} where ${DBConstants.NOTE_ID} = $noteId",
            null
        ).use {
            if (it.moveToFirst()) {
                return Note(
                    id = it.getInt(it.getColumnIndex(DBConstants.NOTE_ID)),
                    title = it.getString(it.getColumnIndex(DBConstants.TITLE)),
                    message = it.getString(it.getColumnIndex(DBConstants.MESSAGE)),
                    type = it.getInt(it.getColumnIndex(DBConstants.TYPE)),
                    creationDate = it.getLong(it.getColumnIndex(DBConstants.CREATION_DATE)),
                    reminderDate = it.getLong(it.getColumnIndex(DBConstants.REMINDER_DATE))
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
        contentValues.put(DBConstants.CREATION_DATE, note.creationDate)
        contentValues.put(DBConstants.REMINDER_DATE, note.reminderDate)

        return db.update(
            DBConstants.TABLE_NOTES,
            contentValues,
            "${DBConstants.NOTE_ID} = ?",
            arrayOf(note.id.toString())
        ) > 0
    }

    fun deleteNote(noteId: Int): Boolean {
        val db = this.writableDatabase

        return db.delete(
            DBConstants.TABLE_NOTES,
            "${DBConstants.NOTE_ID} = ?",
            arrayOf(noteId.toString())
        ) > 0
    }

    fun insertUser(user: User): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DBConstants.USER_NAME, user.username)
        contentValues.put(DBConstants.USER_PASSWORD, user.password)
        val result = db.insert(DBConstants.TABLE_USERS, null, contentValues)
        return result > 0
    }

    fun getUser(username: String): User? {
        readableDatabase.rawQuery(
            "select * from ${DBConstants.TABLE_USERS} where ${DBConstants.USER_NAME} = '$username'",
            null
        ).use {
            if (it.moveToFirst()) {
                return User(
                    userId = it.getInt(it.getColumnIndex(DBConstants.USER_ID)),
                    username = it.getString(it.getColumnIndex(DBConstants.USER_NAME)),
                    password = it.getString(it.getColumnIndex(DBConstants.USER_PASSWORD))
                )
            }
        }
        return null
    }
}