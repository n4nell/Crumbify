package com.example.crumbify

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MyProfileAdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_profile_admin)

        val btnBack = findViewById<ImageButton>(R.id.imageButton99)
        btnBack.setOnClickListener { finish() }

        val tvName = findViewById<TextView>(R.id.tv_name_value_admin)
        val tvUsername = findViewById<TextView>(R.id.tv_username_value_admin)
        val tvEmail = findViewById<TextView>(R.id.tv_email_value_admin)

        val sharedPref = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val name = sharedPref.getString("name", "Guest")
        val username = sharedPref.getString("username", "-")
        val email = sharedPref.getString("email", "-")

        tvName.text = name
        tvUsername.text = username
        tvEmail.text = email

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
