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

class NotifActivity : AppCompatActivity() {
    private lateinit var rvNotif: RecyclerView
    private var listNotif = ArrayList<NotifUser>()
    private lateinit var adapter: NotifAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notif)

        val btnBack = findViewById<ImageButton>(R.id.imageButton6)
        rvNotif = findViewById(R.id.rvNotif)

        rvNotif.layoutManager = LinearLayoutManager(this)
        adapter = NotifAdapter(listNotif)
        rvNotif.adapter = adapter

        btnBack.setOnClickListener { finish() }

        FetchNotifications().execute()
    }

    inner class FetchNotifications : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String? {
            return RequestHandler().sendGetRequest("${Konfigurasi.URL_GET_NOTIF}?is_admin=0")
        }

        override fun onPostExecute(result: String?) {
            if (result != null) {
                try {
                    val jsonObject = JSONObject(result)
                    if (jsonObject.optBoolean("status", false)) {
                        val array = jsonObject.getJSONArray("result")
                        val updatedList = ArrayList<NotifUser>()

                        for (i in 0 until array.length()) {
                            val obj = array.getJSONObject(i)
                            updatedList.add(NotifUser(
                                obj.getString("title"),
                                obj.getString("message")
                            ))
                        }
                        adapter.updateData(updatedList)
                    }
                } catch (e: Exception) {
                    Log.e("ERROR_NOTIF", "Error: ${e.message}")
                }
            }
        }
    }
}