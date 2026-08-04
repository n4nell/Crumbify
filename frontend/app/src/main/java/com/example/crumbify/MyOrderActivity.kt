package com.example.crumbify

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MyOrderActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var btnBack: ImageButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_order)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvHistory = findViewById(R.id.rv_myorder)
        btnBack = findViewById(R.id.imageButton13)

        rvHistory.layoutManager = LinearLayoutManager(this)

        btnBack.setOnClickListener { finish() }

        muatDataHistory()
    }

    private fun muatDataHistory() {
        val sharedPref = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("id", -1)

        if (userId == -1) return

        lifecycleScope.launch(Dispatchers.IO) {
            val rh = RequestHandler()
            val json = rh.sendGetRequestParam(Konfigurasi.URL_ORDER_HISTORY, "?user_id=$userId")

            withContext(Dispatchers.Main) {
                try {
                    val jo = JSONObject(json)
                    if (jo.getBoolean("status")) {
                        val result = jo.getJSONArray("data")
                        val listHistory = mutableListOf<History>()

                        for (i in 0 until result.length()) {
                            val obj = result.getJSONObject(i)

                            val itemArray = obj.optJSONArray("items") ?: org.json.JSONArray()
                            val listItems = mutableListOf<HistoryItem>()

                            for (j in 0 until itemArray.length()) {
                                val itemObj = itemArray.getJSONObject(j)
                                listItems.add(HistoryItem(
                                    itemObj.optString("name", "Unknown"),
                                    itemObj.optString("qty", "0"),
                                    itemObj.optString("price", "0")
                                ))
                            }

                            listHistory.add(History(
                                obj.optString("order_id", "-"),
                                obj.optDouble("total_order", 0.0),
                                listItems
                            ))
                        }

                        adapter = HistoryAdapter(listHistory)
                        rvHistory.adapter = adapter

                    } else {
                        Toast.makeText(this@MyOrderActivity, "Belum ada riwayat pesanan", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@MyOrderActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}