package com.example.crumbify

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crumbify.adapter.ProductAdapter
import com.example.crumbify.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class CategoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var productList = ArrayList<Product>()
    private var categoryName: String = ""
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPref = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        userId = sharedPref.getInt("id", -1)

        categoryName = intent.getStringExtra("CATEGORY_NAME") ?: ""
        findViewById<TextView>(R.id.textView24).text = categoryName

        recyclerView = findViewById(R.id.rv_category)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        GetCategoryProducts().execute()

        findViewById<ImageButton>(R.id.imageButton5).setOnClickListener { finish() }
    }

    private fun toggleWishlist(product: Product) {
        if (userId == -1) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val url = if (product.isWishlisted) Konfigurasi.URL_WISHLIST_DELETE else Konfigurasi.URL_WISHLIST_ADD
            val params = HashMap<String, String>()
            params["user_id"] = userId.toString()
            params["product_id"] = product.id.toString()

            val response = RequestHandler().sendPostRequest(url, params)

            withContext(Dispatchers.Main) {
                try {
                    val jo = JSONObject(response)
                    if (jo.getBoolean("status")) {
                        product.isWishlisted = !product.isWishlisted
                        recyclerView.adapter?.notifyDataSetChanged()
                        Toast.makeText(this@CategoryActivity, jo.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("WISHLIST_ERROR", e.toString())
                }
            }
        }
    }

    inner class GetCategoryProducts : AsyncTask<Void, Void, String>() {
        private lateinit var progressDialog: ProgressDialog

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog.show(this@CategoryActivity, "Please Wait", "Loading products...", false, false)
        }

        override fun doInBackground(vararg params: Void?): String? {
            val rh = RequestHandler()
            val categoryId = when(categoryName) {
                "Sweet" -> 1
                "Salty" -> 2
                "Coffee" -> 3
                "Non-Coffee" -> 4
                else -> 0
            }
            return rh.sendGetRequestParam(Konfigurasi.URL_PRODUCT_LIST, "?category_id=$categoryId&user_id=$userId")
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            if (result != null) parseJson(result)
        }
    }

    private fun parseJson(json: String) {
        try {
            val jsonObject = JSONObject(json)
            val status = jsonObject.optBoolean("status", false)

            if (status) {
                val array = jsonObject.getJSONArray("data")
                productList.clear()

                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    productList.add(Product(
                        id = obj.getInt("id"),
                        name = obj.getString("name"),
                        price = "Rp ${obj.getString("price")}",
                        imageRes = obj.getString("image"),
                        description = obj.getString("description"),
                        stock = obj.getInt("stock"),
                        categoryId = obj.optInt("category_id", 0),
                        isWishlisted = obj.optInt("is_wishlisted", 0) == 1
                    ))
                }

                val adapter = ProductAdapter(productList,
                    { product -> toggleWishlist(product) },
                    { product ->
                        val intent = Intent(this, DetailActivity::class.java)
                        intent.putExtra("PRODUCT", product)
                        startActivity(intent)
                    }
                )
                recyclerView.adapter = adapter
            } else {
                Toast.makeText(this, "No products found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("JSON_ERROR", e.message.toString())
        }
    }
}