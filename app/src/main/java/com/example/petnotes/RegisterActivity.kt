package com.example.petnotes

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.petnotes.db.DBHelper
import com.example.petnotes.model.User
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupViews()
    }

    private fun setupViews() {
        mb_register_submit.setOnClickListener {
            til_register_email.error = null
            til_register_password.error = null
            til_register_confirm_password.error = null
            if (validateFields()) {
                InsertUserAsync().execute(
                    User(
                        null,
                        tiet_register_email.text.toString(),
                        tiet_register_password.text.toString()
                    )
                )
            }
        }
    }


    private fun validateFields(): Boolean {
        return validateEmailField() and validatePasswordField() and validateConfirmPasswordField() and validatePasswordFieldsMatches()
    }

    private fun validateEmailField(): Boolean {
        if (tiet_register_email.text.isNullOrBlank()) {
            til_register_email.error = getString(R.string.empty_error_message)
        }
        return til_register_email.error == null
    }

    private fun validatePasswordField(): Boolean {
        if (tiet_register_password.text.isNullOrBlank()) {
            til_register_password.error = getString(R.string.empty_error_message)
        }
        return til_register_password.error == null
    }

    private fun validateConfirmPasswordField(): Boolean {
        if (tiet_register_confirm_password.text.isNullOrBlank()) {
            til_register_confirm_password.error = getString(R.string.empty_error_message)
        }
        return til_register_confirm_password.error == null
    }

    private fun validatePasswordFieldsMatches(): Boolean {
        val matches =
            tiet_register_password.text.toString() == tiet_register_confirm_password.text.toString()
        if (!matches) {
            Toast.makeText(this, R.string.passwords_dont_match, Toast.LENGTH_LONG).show()
        }
        return matches
    }


    inner class InsertUserAsync : AsyncTask<User, Unit, Boolean>() {

        override fun doInBackground(vararg params: User): Boolean {

            return DBHelper(this@RegisterActivity).insertUser(params[0])
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            this@RegisterActivity.onUserInsertedSuccess(result)
        }

    }

    private fun onUserInsertedSuccess(result: Boolean) {
        if (result) {
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, R.string.user_insertion_fail, Toast.LENGTH_LONG).show()
        }
    }

}
