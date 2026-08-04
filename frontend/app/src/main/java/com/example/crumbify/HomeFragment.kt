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
import com.example.crumbify.adapter.ProductAdapter
import com.example.crumbify.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var recyclerView: RecyclerView
    private var productList = ArrayList<Product>()
    private var userId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("name", "User")
        userId = sharedPref.getInt("id", -1)

        val tvGreetingName = view.findViewById<TextView>(R.id.textView38)
        tvGreetingName.text = userName

        recyclerView = view.findViewById(R.id.rv_suggested_products)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Setup klik tombol lainnya tetap sama
        setupButtons(view)
    }

    // --- TAMBAHAN PENTING ---
    // Agar stok terupdate otomatis saat kembali dari Checkout/Detail
    override fun onResume() {
        super.onResume()
        refreshProducts()
    }

    private fun refreshProducts() {
        // Menggunakan Coroutine agar lebih modern daripada AsyncTask
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RequestHandler().sendGetRequestParam(Konfigurasi.URL_PRODUCT_LIST, "?user_id=$userId")
                withContext(Dispatchers.Main) {
                    if (response != null) parseJson(response)
                }
            } catch (e: Exception) {
                Log.e("HOME_ERROR", e.toString())
            }
        }
    }

    private fun parseJson(json: String) {
        try {
            val jsonObject = JSONObject(json)
            if (jsonObject.optBoolean("status", false)) {
                val array = jsonObject.getJSONArray("data")
                productList.clear()

                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val isLoved = obj.optInt("is_wishlisted", 0) == 1

                    productList.add(Product(
                        obj.getInt("id"),
                        obj.getString("name"),
                        "Rp " + obj.getString("price"),
                        obj.getString("image"),
                        obj.getString("description"),
                        obj.getInt("stock"), // Stok akan terupdate di sini
                        obj.optInt("category_id", 0),
                        isLoved
                    ))
                }

                val adapter = ProductAdapter(productList,
                    { product -> toggleWishlist(product) },
                    { product ->
                        val intent = Intent(requireContext(), DetailActivity::class.java)
                        intent.putExtra("PRODUCT", product)
                        startActivity(intent)
                    }
                )
                recyclerView.adapter = adapter
            }
        } catch (e: Exception) {
            Log.e("JSON_ERROR", e.toString())
        }
    }

    private fun setupButtons(view: View) {
        view.findViewById<ImageButton>(R.id.imageButton18).setOnClickListener {
            startActivity(Intent(requireContext(), NotifActivity::class.java))
        }
        view.findViewById<CardView>(R.id.cardView).setOnClickListener { openCategory("Sweet") }
        view.findViewById<CardView>(R.id.cardView2).setOnClickListener { openCategory("Salty") }
        view.findViewById<CardView>(R.id.cardView3).setOnClickListener { openCategory("Coffee") }
        view.findViewById<CardView>(R.id.cardView4).setOnClickListener { openCategory("Non-Coffee") }
        view.findViewById<ImageButton>(R.id.imageButton16).setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }
        view.findViewById<TextView>(R.id.textView36).setOnClickListener {
            val tabLayout = activity?.findViewById<com.google.android.material.tabs.TabLayout>(R.id.tabLayout)
            tabLayout?.getTabAt(1)?.select()
        }
    }

    private fun toggleWishlist(product: Product) {
        if (userId == -1) return
        product.isWishlisted = !product.isWishlisted
        recyclerView.adapter?.notifyDataSetChanged()

        lifecycleScope.launch(Dispatchers.IO) {
            val url = if (product.isWishlisted) Konfigurasi.URL_WISHLIST_ADD else Konfigurasi.URL_WISHLIST_DELETE
            val params = HashMap<String, String>()
            params["user_id"] = userId.toString()
            params["product_id"] = product.id.toString()
            RequestHandler().sendPostRequest(url, params)
        }
    }

    private fun openCategory(category: String) {
        val intent = Intent(requireContext(), CategoryActivity::class.java)
        intent.putExtra("CATEGORY_NAME", category)
        startActivity(intent)
    }
}