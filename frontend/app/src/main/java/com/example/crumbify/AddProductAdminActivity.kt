package com.example.crumbify

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.crumbify.helper.MultipartRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

class AddProductAdminActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etCategory: EditText
    private lateinit var etStock: EditText
    private lateinit var etDescription: EditText
    private lateinit var ivDisplay: ImageView
    private lateinit var ivPlaceholder: ImageView
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_product_admin)

        etName = findViewById(R.id.et_name)
        etPrice = findViewById(R.id.et_price)
        etCategory = findViewById(R.id.et_category)
        etStock = findViewById(R.id.et_stock)
        etDescription = findViewById(R.id.et_description)
        ivDisplay = findViewById(R.id.iv_display_picture)
        ivPlaceholder = findViewById(R.id.iv_icon_placeholder)

        val btnAddPicture = ivPlaceholder
        val btnSubmit = findViewById<Button>(R.id.button6)
        val btnBack = findViewById<ImageButton>(R.id.imageButton15)

        btnBack.setOnClickListener { finish() }

        val pickImageAction = View.OnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

        btnAddPicture.setOnClickListener(pickImageAction)
        ivPlaceholder.setOnClickListener(pickImageAction)

        btnSubmit.setOnClickListener {
            uploadProduct()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            if (imageUri != null) {
                ivDisplay.setImageURI(imageUri)
                ivDisplay.visibility = View.VISIBLE
                ivPlaceholder.visibility = View.GONE
            }
        }
    }

    private fun uploadProduct() {
        val name = etName.text.toString().trim()
        val price = etPrice.text.toString().trim()
        val categoryId = etCategory.text.toString().trim()
        val stock = etStock.text.toString().trim()
        val description = etDescription.text.toString().trim()

        if (name.isEmpty() || price.isEmpty() || categoryId.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val file = uriToFile(imageUri!!, "temp_image")

                val params = HashMap<String, String>()
                params["name"] = name
                params["price"] = price
                params["category_id"] = categoryId
                params["stock"] = stock
                params["description"] = description

                val response = MultipartRequest().upload(Konfigurasi.URL_PRODUCT_ADD, params, file)

                withContext(Dispatchers.Main) {
                    val jo = JSONObject(response)
                    if (jo.getBoolean("status")) {
                        Toast.makeText(this@AddProductAdminActivity, "Menu Added Successfully!", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddProductAdminActivity, jo.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("ADD_PRODUCT_ERROR", e.toString())
                    Toast.makeText(this@AddProductAdminActivity, "Error: Check Logcat", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uriToFile(uri: Uri, fileName: String): File {
        val tempFile = File(cacheDir, "$fileName.jpg")
        tempFile.createNewFile()

        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = java.io.FileOutputStream(tempFile)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }
}