package com.example.crumbify

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crumbify.adapter.ProductAdapter
import com.example.crumbify.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class SearchActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etSearch: EditText
    private var productList = ArrayList<Product>()
    private lateinit var adapter: ProductAdapter
    private var userId: Int = -1

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val sharedPref = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        userId = sharedPref.getInt("id", -1)

        etSearch = findViewById(R.id.txtSearch)
        recyclerView = findViewById(R.id.recviewSearch)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        SearchProducts("").execute()

        findViewById<ImageButton>(R.id.btnBackSearch).setOnClickListener {
            finish()
        }

        // Setup TextWatcher dengan Debounce
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }

                searchRunnable = Runnable {
                    val query = s.toString().trim()
                    SearchProducts(query).execute()
                }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun toggleWishlist(product: Product) {
        if (userId == -1) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        product.isWishlisted = !product.isWishlisted
        if (::adapter.isInitialized) adapter.notifyDataSetChanged()

        lifecycleScope.launch(Dispatchers.IO) {
            val url = if (product.isWishlisted) Konfigurasi.URL_WISHLIST_ADD else Konfigurasi.URL_WISHLIST_DELETE
            val params = HashMap<String, String>()
            params["user_id"] = userId.toString()
            params["product_id"] = product.id.toString()

            val response = RequestHandler().sendPostRequest(url, params)

            withContext(Dispatchers.Main) {
                try {
                    val jo = JSONObject(response)
                    if (!jo.getBoolean("status")) {
                        product.isWishlisted = !product.isWishlisted
                        adapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    Log.e("WISHLIST_ERROR", e.toString())
                }
            }
        }
    }

    inner class SearchProducts(private val query: String) : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String? {
            return try {
                val url = "${Konfigurasi.URL_PRODUCT_LIST}?search=$query&user_id=$userId"
                RequestHandler().sendGetRequest(url)
            } catch (e: Exception) {
                null
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result != null) parseJson(result)
        }
    }

    private fun parseJson(json: String) {
        try {
            val jsonObject = JSONObject(json)
            val status = jsonObject.optBoolean("status", false)

            productList.clear()

            if (status) {
                val array = jsonObject.getJSONArray("data")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    productList.add(Product(
                        obj.getInt("id"),                // id
                        obj.getString("name"),           // name
                        "Rp " + obj.getString("price"),  // price
                        obj.getString("image"),          // imageRes (PHP ngirim full URL sekarang)
                        obj.getString("description"),    // description
                        obj.optInt("stock", 0),          // stock
                        obj.optInt("category_id", 0),    // categoryId (Pastikan nama kolom di PHP sama)
                        obj.optInt("is_wishlisted", 0) == 1 // isWishlisted
                    ))
                }
            }

            if (!::adapter.isInitialized) {
                adapter = ProductAdapter(productList,
                    { product -> toggleWishlist(product) },
                    { product ->
                        val intent = Intent(this, DetailActivity::class.java)
                        intent.putExtra("PRODUCT", product)
                        startActivity(intent)
                    }
                )
                recyclerView.adapter = adapter
            } else {
                adapter.notifyDataSetChanged()
            }

        } catch (e: Exception) {
            Log.e("SEARCH_ERROR", "Error: ${e.message}")
        }
    }
}