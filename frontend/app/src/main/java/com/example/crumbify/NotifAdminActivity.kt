package com.example.crumbify

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class NotifAdminActivity : AppCompatActivity() {

    private lateinit var rvNotif: RecyclerView
    private var listAdmin = ArrayList<NotifAdmin>()
    private lateinit var adapter: NotifAdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notif_admin)

        val btnBack = findViewById<ImageButton>(R.id.imageButton6)
        rvNotif = findViewById(R.id.rvNotifAdmin)

        rvNotif.layoutManager = LinearLayoutManager(this)
        adapter = NotifAdminAdapter(listAdmin)
        rvNotif.adapter = adapter

        btnBack.setOnClickListener { finish() }

        FetchAdminNotifications().execute()
    }

    inner class FetchAdminNotifications : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String? {
            return RequestHandler().sendGetRequest("${Konfigurasi.URL_GET_NOTIF}?is_admin=1")
        }

        override fun onPostExecute(result: String?) {
            if (result != null) {
                try {
                    val jsonObject = JSONObject(result)
                    if (jsonObject.optBoolean("status", false)) {
                        val array = jsonObject.getJSONArray("result")
                        val updatedList = ArrayList<NotifAdmin>()

                        for (i in 0 until array.length()) {
                            val obj = array.getJSONObject(i)
                            updatedList.add(NotifAdmin(
                                obj.getString("title"),
                                obj.getString("message")
                            ))
                        }
                        adapter.updateData(updatedList)
                    }
                } catch (e: Exception) {
                    Log.e("JSON_ERROR", "Error parsing: ${e.message}")
                }
            }
        }
    }
}