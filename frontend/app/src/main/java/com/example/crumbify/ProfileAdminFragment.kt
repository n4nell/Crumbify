package com.example.crumbify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class ProfileAdminFragment : Fragment(R.layout.fragment_profile_admin) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvName = view.findViewById<TextView>(R.id.textView19)
        val tvEmail = view.findViewById<TextView>(R.id.textView4)

        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val name = sharedPref.getString("name", "Admin")
        val email = sharedPref.getString("email", "-")

        tvName?.text = name
        tvEmail?.text = email

        view.findViewById<CardView>(R.id.cardView34)?.setOnClickListener {
            startActivity(Intent(requireContext(), MyProfileAdminActivity::class.java))
        }

        view.findViewById<CardView>(R.id.cardView33)?.setOnClickListener {
            startActivity(Intent(requireContext(), AddProductAdminActivity::class.java))
        }

        view.findViewById<CardView>(R.id.cardViewLogoutAdmin)?.setOnClickListener {
            sharedPref.edit().clear().apply()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}