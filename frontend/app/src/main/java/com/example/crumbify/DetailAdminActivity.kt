package com.example.crumbify

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.crumbify.model.Product
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale

class DetailAdminActivity : AppCompatActivity() {

    private var productId: Int = 0
    private lateinit var tvName: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvDesc: TextView
    private lateinit var tvStock: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_admin)

        tvName = findViewById(R.id.textView280)
        tvPrice = findViewById(R.id.textView290)
        tvDesc = findViewById(R.id.textView320)
        tvStock = findViewById(R.id.textView300)
        val imgView = findViewById<ImageView>(R.id.imageView90)

        val product = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("PRODUCT", Product::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Product>("PRODUCT")
        }

        product?.let {
            productId = it.id
            updateUI(it.name, it.description, it.stock.toDouble(), it.price.replace(Regex("[^\\d]"), "").toDoubleOrNull() ?: 0.0)

            Glide.with(this)
                .load(it.imageRes)
                .placeholder(R.drawable.cinnamon_roll)
                .error(R.drawable.cinnamon_roll)
                .into(imgView)
        }

        findViewById<ImageButton>(R.id.imageButton120).setOnClickListener { showEditForm() }
        findViewById<ImageButton>(R.id.imageButton11).setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        GetDetailProduct().execute()
    }

    private fun updateUI(name: String, desc: String, stock: Double, price: Double) {
        tvName.text = name
        tvDesc.text = desc
        tvStock.text = "${stock.toInt()} pcs"
        tvPrice.text = formatRupiah(price)
    }

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(number).replace("Rp", "Rp ").replace(",00", "")
    }

    inner class GetDetailProduct : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String? {
            return try {
                RequestHandler().sendGetRequestParam(Konfigurasi.URL_PRODUCT_SINGLE, "?id=$productId")
            } catch (e: Exception) { null }
        }

        override fun onPostExecute(result: String?) {
            result?.let {
                try {
                    val json = JSONObject(it)
                    if (json.getBoolean("status")) {
                        val data = json.getJSONObject("data")
                        updateUI(
                            data.getString("name"),
                            data.getString("description"),
                            data.getDouble("stock"),
                            data.getDouble("price")
                        )
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }

    private fun showEditForm() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 40, 60, 10)
        }

        val etName = EditText(this).apply { setText(tvName.text.toString()) }
        val etPrice = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            setText(tvPrice.text.toString().replace(Regex("[^\\d]"), ""))
        }
        val etStock = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            setText(tvStock.text.toString().replace(Regex("[^\\d]"), ""))
        }
        val etDesc = EditText(this).apply { setText(tvDesc.text.toString()) }

        layout.addView(etName); layout.addView(etPrice); layout.addView(etStock); layout.addView(etDesc)

        AlertDialog.Builder(this)
            .setTitle("Quick Edit")
            .setView(layout)
            .setPositiveButton("Update") { _, _ ->
                val name = etName.text.toString()
                val price = etPrice.text.toString()
                val stock = etStock.text.toString()
                val desc = etDesc.text.toString()

                if (name.isNotEmpty() && price.isNotEmpty() && stock.isNotEmpty()) {
                    PerformUpdate(name, price, stock, desc).execute()
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null).show()
    }

    inner class PerformUpdate(val name: String, val price: String, val stock: String, val desc: String) :
        AsyncTask<Void, Void, String>() {
        private lateinit var pd: ProgressDialog

        override fun onPreExecute() {
            super.onPreExecute()
            pd = ProgressDialog.show(this@DetailAdminActivity, "Updating", "Please wait...", false, false)
        }

        override fun doInBackground(vararg params: Void?): String? {
            val data = HashMap<String, String>().apply {
                put("id", productId.toString()); put("name", name)
                put("price", price); put("stock", stock); put("description", desc)
            }
            return try { RequestHandler().sendPostRequest(Konfigurasi.URL_PRODUCT_UPDATE, data) } catch (e: Exception) { null }
        }

        override fun onPostExecute(result: String?) {
            pd.dismiss()
            result?.let {
                try {
                    val json = JSONObject(it)
                    if (json.getBoolean("status")) {
                        Toast.makeText(this@DetailAdminActivity, "Product Updated", Toast.LENGTH_SHORT).show()
                        updateUI(name, desc, stock.toDouble(), price.toDouble())
                    } else {
                        Toast.makeText(this@DetailAdminActivity, json.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }
}