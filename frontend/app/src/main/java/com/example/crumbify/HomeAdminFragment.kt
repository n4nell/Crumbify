package com.example.crumbify

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crumbify.adapter.ProductAdminAdapter
import com.example.crumbify.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class HomeAdminFragment : Fragment(R.layout.fragment_home_admin) {

    private lateinit var recyclerView: RecyclerView
    private var productList = ArrayList<Product>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_product_admin_home)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        setupAdminUI(view)
    }

    // Refresh data setiap kali Fragment tampil kembali
    override fun onResume() {
        super.onResume()
        loadAdminProducts()
    }

    private fun loadAdminProducts() {
        lifecycleScope.launch(Dispatchers.IO) {
            val response = RequestHandler().sendGetRequest(Konfigurasi.URL_PRODUCT_LIST)
            withContext(Dispatchers.Main) {
                if (response != null) parseAdminJson(response)
            }
        }
    }

    private fun parseAdminJson(json: String) {
        try {
            val jsonObject = JSONObject(json)
            if (jsonObject.optBoolean("status", false)) {
                val array = jsonObject.getJSONArray("data")
                productList.clear()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    productList.add(Product(
                        obj.getInt("id"),
                        obj.getString("name"),
                        "Rp " + obj.getString("price"),
                        obj.getString("image"),
                        obj.getString("description"),
                        obj.getInt("stock"),
                        categoryId = obj.optInt("category_id", 0)
                    ))
                }
                recyclerView.adapter = ProductAdminAdapter(productList,
                    { /* Edit Logic */ },
                    { product ->
                        val intent = Intent(requireContext(), DetailAdminActivity::class.java)
                        intent.putExtra("PRODUCT", product)
                        startActivity(intent)
                    }
                )
            }
        } catch (e: Exception) {
            Log.e("ADMIN_JSON_ERROR", e.toString())
        }
    }

    private fun setupAdminUI(view: View) {
        view.findViewById<CardView>(R.id.cardView).setOnClickListener { openCategory("Sweet") }
        view.findViewById<CardView>(R.id.cardView2).setOnClickListener { openCategory("Salty") }
        view.findViewById<CardView>(R.id.cardView3).setOnClickListener { openCategory("Coffee") }
        view.findViewById<CardView>(R.id.cardView4).setOnClickListener { openCategory("Non-Coffee") }
        view.findViewById<TextView>(R.id.textView36).setOnClickListener {
            val tabLayout = activity?.findViewById<com.google.android.material.tabs.TabLayout>(R.id.tabLayout)
            tabLayout?.getTabAt(1)?.select()
        }
        val btnNotif = view.findViewById<ImageButton>(R.id.imageButton26)
        btnNotif.setOnClickListener {
            val intent = Intent(requireContext(), NotifAdminActivity::class.java)
            startActivity(intent)
        }

        val btnManage = view.findViewById<ImageButton>(R.id.imageButton27)
        btnManage.setOnClickListener {
            val tabLayout = activity?.findViewById<com.google.android.material.tabs.TabLayout>(R.id.tabLayout)
            tabLayout?.getTabAt(2)?.select()
        }
    }

    private fun openCategory(category: String) {
        val intent = Intent(requireContext(), CategoryAdminActivity::class.java)
        intent.putExtra("CATEGORY_NAME", category)
        startActivity(intent)
    }
}