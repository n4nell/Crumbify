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

class MainActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usernameEditText = findViewById(R.id.txtLoginUsername)
        passwordEditText = findViewById(R.id.txtLoginPW)
        loginButton = findViewById(R.id.btnLogin)
        registerTextView = findViewById(R.id.txtRegister)

        registerTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username and Password are required", Toast.LENGTH_SHORT)
                    .show()
            } else {
                LoginUser(username, password).execute()
            }
        }
    }

    inner class LoginUser(private val username: String, private val password: String) :
        AsyncTask<Void, Void, String>() {

        private lateinit var progressDialog: ProgressDialog

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog.show(this@MainActivity, "Login", "Authenticating...", false, false)
        }

        override fun doInBackground(vararg params: Void?): String? {
            val data = HashMap<String, String>()
            data["username"] = username
            data["password"] = password
            return RequestHandler().sendPostRequest(Konfigurasi.URL_LOGIN, data)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            progressDialog.dismiss()

            if (result.isNullOrEmpty()) {
                Toast.makeText(this@MainActivity, "Failed to connect to server", Toast.LENGTH_LONG).show()
                return
            }

            try {
                val jsonObject = JSONObject(result)
                val status = jsonObject.getBoolean("status")
                val message = jsonObject.getString("message")

                if (status) {
                    val user = jsonObject.getJSONObject("user")
                    val id = user.getInt("id")
                    val role = user.getString("role")

                    val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putInt("id", id)
                    editor.putString("role", role)
                    editor.putString("name", user.getString("name"))
                    editor.putString("email", user.getString("email"))
                    editor.putString("username", user.getString("username"))
                    editor.commit()

                    Toast.makeText(this@MainActivity, "Welcome, ${user.getString("name")}", Toast.LENGTH_SHORT).show()

                    val intent = if (role == "admin") {
                        Intent(this@MainActivity, HomeAdminActivity::class.java)
                    } else {
                        Intent(this@MainActivity, HomeActivity::class.java)
                    }
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("LOGIN_ERROR", e.toString())
                Toast.makeText(this@MainActivity, "Response error", Toast.LENGTH_LONG).show()
            }
        }
    }
}
