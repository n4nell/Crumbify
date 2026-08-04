package com.example.crumbify

import android.app.AlertDialog
import android.app.ProgressDialog
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crumbify.adapter.ProductAdminAdapter
import com.example.crumbify.model.Product
import org.json.JSONObject

class CategoryAdminActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var productList = ArrayList<Product>()
    private var categoryName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category_admin)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        categoryName = intent.getStringExtra("CATEGORY_NAME") ?: "Products"
        findViewById<TextView>(R.id.textView24).text = categoryName

        recyclerView = findViewById(R.id.rv_category_admin)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val btnEditMode: ImageButton = findViewById(R.id.imageButton14)
        btnEditMode.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Admin Control")
                .setMessage("To Edit or Delete a specific product, please LONG-PRESS on the product image below.")
                .setPositiveButton("Understood", null)
                .show()
        }

        findViewById<ImageButton>(R.id.imageButton5).setOnClickListener { finish() }

        GetCategoryProducts().execute()
    }

    override fun onResume() {
        super.onResume()
        GetCategoryProducts().execute()
    }

    inner class GetCategoryProducts : AsyncTask<Void, Void, String>() {
        private lateinit var progressDialog: ProgressDialog

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog.show(this@CategoryAdminActivity, "Please Wait", "Loading products...", false, false)
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
            return rh.sendGetRequest("${Konfigurasi.URL_PRODUCT_LIST}?category_id=$categoryId")
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
            if (jsonObject.optBoolean("status", false)) {
                val array = jsonObject.getJSONArray("data")
                productList.clear()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    productList.add(Product(
                        id = obj.getInt("id"),
                        name = obj.getString("name"),
                        price = obj.getString("price"),
                        imageRes = obj.getString("image"),
                        description = obj.getString("description"),
                        stock = obj.getInt("stock"),
                        categoryId = obj.optInt("category_id", 0)
                    ))
                }

                recyclerView.adapter = ProductAdminAdapter(
                    productList,
                    { product -> showEditDeleteDialog(product) },
                    { product ->
                        val intent = Intent(this, DetailAdminActivity::class.java)
                        intent.putExtra("PRODUCT", product)
                        startActivity(intent)
                    }
                )
            } else {
                productList.clear()
                recyclerView.adapter?.notifyDataSetChanged()
                Toast.makeText(this, "No products found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("JSON_ERROR", e.message.toString())
        }
    }

    private fun showEditDeleteDialog(product: Product) {
        val options = arrayOf("Update Product Details", "Change Category", "Delete Permanently")
        AlertDialog.Builder(this)
            .setTitle("Manage ${product.name}")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        val intent = Intent(this, DetailAdminActivity::class.java)
                        intent.putExtra("PRODUCT", product)
                        startActivity(intent)
                    }
                    1 -> showMoveCategoryDialog(product)
                    2 -> confirmDelete(product)
                }
            }
            .show()
    }

    private fun showMoveCategoryDialog(product: Product) {
        val categories = arrayOf("Sweet", "Salty", "Coffee", "Non-Coffee")
        val categoryIds = arrayOf(1, 2, 3, 4)

        AlertDialog.Builder(this)
            .setTitle("Move Category")
            .setItems(categories) { _, which ->
                PerformUpdateCategory(product.id, categoryIds[which]).execute()
            }
            .show()
    }

    private fun confirmDelete(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Warning")
            .setMessage("Are you sure you want to delete ${product.name} from the database? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ -> PerformDeleteProduct(product.id).execute() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    inner class PerformUpdateCategory(val pid: Int, val cid: Int) : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String? {
            val data = HashMap<String, String>().apply {
                put("id", pid.toString())
                put("category_id", cid.toString())
            }
            return RequestHandler().sendPostRequest(Konfigurasi.URL_PRODUCT_UPDATE, data)
        }

        override fun onPostExecute(result: String?) {
            Toast.makeText(this@CategoryAdminActivity, "Product moved successfully", Toast.LENGTH_SHORT).show()
            GetCategoryProducts().execute()
        }
    }

    inner class PerformDeleteProduct(val pid: Int) : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String? {
            return RequestHandler().sendGetRequestParam(Konfigurasi.URL_PRODUCT_DELETE, "?id=$pid")
        }
        override fun onPostExecute(result: String?) {
            Toast.makeText(this@CategoryAdminActivity, "Product deleted permanently", Toast.LENGTH_SHORT).show()
            GetCategoryProducts().execute()
        }
    }
}