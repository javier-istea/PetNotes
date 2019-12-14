package com.example.petnotes

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.petnotes.db.DBHelper
import com.example.petnotes.model.User
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupForm()
    }

    private fun setupForm() {
        mb_login_submit.setOnClickListener {
            til_login_email.error = null
            til_login_password.error = null
            if (validateFields()) {
                GetUserAsync().execute(
                    User(
                        null,
                        tiet_login_email.text.toString(),
                        tiet_login_password.text.toString()
                    )
                )
            }
        }
        tv_login_link_to_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivityForResult(intent, REGISTER_REQUEST)
        }
    }


    private fun validateFields(): Boolean {
        return validateEmailField() and validatePasswordField()
    }

    private fun validateEmailField(): Boolean {
        if (tiet_login_email.text.isNullOrBlank()) {
            til_login_email.error = getString(R.string.empty_error_message)
        }
        return til_login_email.error == null
    }

    private fun validatePasswordField(): Boolean {
        if (tiet_login_password.text.isNullOrBlank()) {
            til_login_password.error = getString(R.string.empty_error_message)
        }
        return til_login_password.error == null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REGISTER_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra("email")?.let {
                tiet_login_email.setText(it)
                tiet_login_password.text = null
                tiet_login_password.requestFocus()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        const val REGISTER_REQUEST = 1
    }


    inner class GetUserAsync : AsyncTask<User, Unit, User?>() {

        lateinit var wantedUser: User

        override fun doInBackground(vararg params: User): User? {
            wantedUser = params[0]
            return DBHelper(this@LoginActivity).getUser(
                wantedUser.username
            )
        }

        override fun onPostExecute(result: User?) {
            super.onPostExecute(result)
            if (result == null) {
                this@LoginActivity.onUserNotFound()
            } else {
                if (result.password == wantedUser.password) {
                    onUserFound()
                } else {
                    onWrongPassword()
                }
            }
        }

    }

    private fun onUserNotFound() {
        Toast.makeText(this, R.string.user_not_found, Toast.LENGTH_LONG).show()
    }

    private fun onUserFound() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun onWrongPassword() {
        Toast.makeText(this, R.string.wrong_password, Toast.LENGTH_LONG).show()
    }


}
