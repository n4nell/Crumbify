package com.example.crumbify

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class CategoriesAdminFragment : Fragment(R.layout.fragment_categories_admin) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sweet = view.findViewById<CardView>(R.id.cardView)
        val salty = view.findViewById<CardView>(R.id.cardView2)
        val coffee = view.findViewById<CardView>(R.id.cardView3)
        val nonCoffee = view.findViewById<CardView>(R.id.cardView4)

        sweet.setOnClickListener { openCategory("Sweet") }
        salty.setOnClickListener { openCategory("Salty") }
        coffee.setOnClickListener { openCategory("Coffee") }
        nonCoffee.setOnClickListener { openCategory("Non-Coffee") }
    }

    private fun openCategory(category: String) {
        val intent = Intent(requireContext(), CategoryAdminActivity::class.java)
        intent.putExtra("CATEGORY_NAME", category)
        startActivity(intent)
    }
}
