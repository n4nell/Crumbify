package com.example.crumbify

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crumbify.adapter.MyWishlistAdapter
import com.example.crumbify.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MyWishlistActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var wishlistList = ArrayList<Product>()
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_wishlist)

        val sharedPref = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        userId = sharedPref.getInt("id", -1)

        recyclerView = findViewById(R.id.rv_wishlist)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        findViewById<ImageButton>(R.id.imageButton10).setOnClickListener {
            finish()
        }

        GetWishlist().execute()
    }

    private fun removeFromWishlist(product: Product) {
        lifecycleScope.launch(Dispatchers.IO) {
            val url = Konfigurasi.URL_WISHLIST_DELETE
            val params = HashMap<String, String>()
            params["user_id"] = userId.toString()
            params["product_id"] = product.id.toString()

            val response = RequestHandler().sendPostRequest(url, params)

            withContext(Dispatchers.Main) {
                try {
                    val jo = JSONObject(response)
                    if (jo.getBoolean("status")) {
                        wishlistList.remove(product)
                        recyclerView.adapter?.notifyDataSetChanged()
                        Toast.makeText(this@MyWishlistActivity, "Delete Wishlist Success!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("WISHLIST_ERROR", e.toString())
                }
            }
        }
    }

    inner class GetWishlist : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String? {
            return try {
                val url = "${Konfigurasi.URL_WISHLIST_INDEX}?user_id=$userId"
                RequestHandler().sendGetRequest(url)
            } catch (e: Exception) { null }
        }

        override fun onPostExecute(result: String?) {
            if (result != null) parseJson(result)
        }
    }

    private fun parseJson(json: String) {
        try {
            val jsonObject = JSONObject(json)
            if (jsonObject.optBoolean("status", false)) {
                val array = jsonObject.getJSONArray("data") // Sesuai dengan PHP
                wishlistList.clear()

                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    wishlistList.add(Product(
                        obj.getInt("id"),
                        obj.getString("name"),
                        obj.getString("price"), // Tanpa Rp (biasanya di adapter baru ditambah)
                        obj.getString("image"),
                        obj.getString("description"),
                        0, 0, true
                    ))
                }
                val adapter = MyWishlistAdapter(wishlistList) { product ->
                    removeFromWishlist(product)
                }
                recyclerView.adapter = adapter
            }
        } catch (e: Exception) {
            Log.e("WISHLIST_ERROR", "Error: ${e.message}")
        }
    }
}