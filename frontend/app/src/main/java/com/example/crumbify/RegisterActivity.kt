package com.example.crumbify

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.txtRegisName)
        etUsername = findViewById(R.id.txtRegisUsername)
        etEmail = findViewById(R.id.txtRegisEmail)
        etPassword = findViewById(R.id.txtRegisPW)
        btnRegister = findViewById(R.id.btnRegis)
        tvLogin = findViewById(R.id.txtLogin)


        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields must be completed.", Toast.LENGTH_SHORT).show()
            } else {
                RegisterUser(name, username, email, password).execute()
            }
        }

         tvLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
         }
    }

    inner class RegisterUser(
        private val name: String,
        private val username: String,
        private val email: String,
        private val password: String
    ) : AsyncTask<Void, Void, String>() {

        private lateinit var progressDialog: ProgressDialog

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog.show(this@RegisterActivity, "Register", "Creating account...", false, false)
        }

        override fun doInBackground(vararg params: Void?): String? {
            val data = HashMap<String, String>()
            data["name"] = name
            data["username"] = username
            data["email"] = email
            data["password"] = password
            data["role"] = "user"

            val requestHandler = RequestHandler()
            return requestHandler.sendPostRequest(Konfigurasi.URL_REGISTER, data)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            progressDialog.dismiss()

            try {
                val jsonObject = JSONObject(result)
                val status = jsonObject.optBoolean("status")
                val message = jsonObject.getString("message")

                if (status) {
                    Toast.makeText(this@RegisterActivity, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Error: $result", Toast.LENGTH_LONG).show()
            }
        }
    }
}