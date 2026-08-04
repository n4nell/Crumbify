package com.example.crumbify

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView

class CategoriesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_categories, container, false)

        val sweet = view.findViewById<CardView>(R.id.cardView)
        val salty = view.findViewById<CardView>(R.id.cardView2)
        val coffee = view.findViewById<CardView>(R.id.cardView3)
        val nonCoffee = view.findViewById<CardView>(R.id.cardView4)

        sweet.setOnClickListener { openCategory("Sweet") }
        salty.setOnClickListener { openCategory("Salty") }
        coffee.setOnClickListener { openCategory("Coffee") }
        nonCoffee.setOnClickListener { openCategory("Non-Coffee") }

        return view
    }

    private fun openCategory(category: String) {
        val intent = Intent(requireContext(), CategoryActivity::class.java)
        intent.putExtra("CATEGORY_NAME", category)
        startActivity(intent)
    }
}