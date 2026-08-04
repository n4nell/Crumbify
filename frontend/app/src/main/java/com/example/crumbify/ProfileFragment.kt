package com.example.crumbify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvName = view.findViewById<TextView>(R.id.textView51)
        val tvEmail = view.findViewById<TextView>(R.id.textView50)

        val sharedPref = requireContext()
            .getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val name = sharedPref.getString("name", null)
        val email = sharedPref.getString("email", null)
        val role = sharedPref.getString("role", null)

        tvName.text = name ?: "Guest"
        tvEmail.text = email ?: "-"

        view.findViewById<CardView>(R.id.card_profile).setOnClickListener {
            startActivity(Intent(requireContext(), MyProfileActivity::class.java))
        }

        view.findViewById<CardView>(R.id.cardView29).setOnClickListener {
            startActivity(Intent(requireContext(), MyOrderActivity::class.java))
        }

        view.findViewById<CardView>(R.id.cardView7).setOnClickListener {
            startActivity(Intent(requireContext(), MyWishlistActivity::class.java))
        }

        view.findViewById<CardView>(R.id.cardViewLogout).setOnClickListener {
            val editor = sharedPref.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
