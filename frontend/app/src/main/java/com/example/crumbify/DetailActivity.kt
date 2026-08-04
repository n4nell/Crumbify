package com.example.crumbify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.crumbify.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    private var quantity = 1
    private var maxStock = 0
    private var productId: String = ""
    private lateinit var btnAddToCart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val btnGoToCart = findViewById<ImageButton>(R.id.imageButton12)
        btnGoToCart.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("TARGET_FRAGMENT", "CART")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        val product = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("PRODUCT", Product::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Product>("PRODUCT")
        }

        if (product == null) {
            Toast.makeText(this, "Data produk tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val tvName = findViewById<TextView>(R.id.textView28)
        val tvPrice = findViewById<TextView>(R.id.textView29)
        val tvDesc = findViewById<TextView>(R.id.textView32)
        val tvQty = findViewById<TextView>(R.id.textView18)
        val tvStockInfo = findViewById<TextView>(R.id.textView30)
        val ivProduct = findViewById<ImageView>(R.id.imageView9)
        btnAddToCart = findViewById<Button>(R.id.button3)

        product.let {
            productId = it.id.toString()
            maxStock = it.stock
            tvName.text = it.name
            tvDesc.text = it.description
            tvStockInfo.text = "$maxStock pcs"
            tvQty.text = String.format("%02d", quantity)

            val cleanPrice = it.price.replace(Regex("[^\\d]"), "")
            val priceDouble = cleanPrice.toDoubleOrNull() ?: 0.0
            tvPrice.text = formatRupiah(priceDouble)

            Glide.with(this).load(it.imageRes).into(ivProduct)
        }

        findViewById<View>(R.id.textView21).setOnClickListener {
            if (quantity < maxStock) {
                quantity++
                tvQty.text = String.format("%02d", quantity)
            } else {
                Toast.makeText(this, "Stok tidak mencukupi!", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<View>(R.id.textView20).setOnClickListener {
            if (quantity > 1) {
                quantity--
                tvQty.text = String.format("%02d", quantity)
            }
        }

        btnAddToCart.setOnClickListener {
            if (maxStock > 0) {
                btnAddToCart.isEnabled = false
                tambahKeKeranjang()
            } else {
                Toast.makeText(this, "Stok habis!", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<ImageButton>(R.id.imageButton11).setOnClickListener { finish() }
    }

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(number).replace("Rp", "Rp ").replace(",00", "")
    }

    private fun tambahKeKeranjang() {
        val sharedPref = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("id", -1)

        if (userId == -1) {
            Toast.makeText(this, "Silakan Login Terlebih Dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val params = HashMap<String, String>()
            params["user_id"] = userId.toString()
            params["product_id"] = productId
            params["qty"] = quantity.toString()

            val rh = RequestHandler()
            val response = try {
                rh.sendPostRequest(Konfigurasi.URL_ADD_CART, params)
            } catch (e: Exception) {
                null
            }

            withContext(Dispatchers.Main) {
                btnAddToCart.isEnabled = true
                if (response != null) {
                    try {
                        val jo = JSONObject(response.trim())
                        if (jo.optBoolean("status")) {
                            showSuccessDialog()
                        } else {
                            Toast.makeText(
                                this@DetailActivity,
                                jo.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@DetailActivity,
                            "Gagal memproses respon server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this@DetailActivity, "Koneksi gagal", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showSuccessDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this@DetailActivity)
        builder.setTitle("Added to Cart")
        builder.setMessage("\nSuccess! Your item has been successfully added to the cart.\n")
        builder.setCancelable(false)

        builder.setPositiveButton("Check Cart") { _, _ ->
            val intent = Intent(this@DetailActivity, HomeActivity::class.java)
            intent.putExtra("TARGET_FRAGMENT", "CART")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton("Continue Shopping") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

        val messageView = dialog.findViewById<TextView>(android.R.id.message)
        messageView?.gravity = android.view.Gravity.CENTER
    }
}