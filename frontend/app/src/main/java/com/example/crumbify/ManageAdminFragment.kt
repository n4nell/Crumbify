package com.example.crumbify

import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class ManageAdminFragment : Fragment(R.layout.fragment_manage_admin) {

    private lateinit var recyclerView: RecyclerView
    private var manageList = ArrayList<Order>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_manage_admin)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        GetOrderHistory().execute()
    }

    inner class GetOrderHistory : AsyncTask<Void, Void, String>() {
        private lateinit var progressDialog: ProgressDialog

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog.show(requireContext(), "Mohon Tunggu", "Loading Orders...", false, false)
        }

        override fun doInBackground(vararg params: Void?): String? {
            val rh = RequestHandler()
            return rh.sendGetRequest(Konfigurasi.URL_ORDER_ADMIN_HISTORY)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            if (result != null) {
                parseJson(result)
            } else {
                Toast.makeText(requireContext(), "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun parseJson(json: String) {
        Log.d("CEK_PHP", "Isi Data: $json")

        try {
            val jsonObject = JSONObject(json)
            if (jsonObject.optBoolean("status", false)) {
                val arrayOrder = jsonObject.getJSONArray("data")
                manageList.clear()

                for (i in 0 until arrayOrder.length()) {
                    val objOrder = arrayOrder.getJSONObject(i)
                    val arrayItems = objOrder.getJSONArray("items")
                    val itemsList = ArrayList<OrderItem>()

                    for (j in 0 until arrayItems.length()) {
                        val objItem = arrayItems.getJSONObject(j)
                        itemsList.add(OrderItem(
                            productName = objItem.getString("product_name"),
                            qty = objItem.getString("qty"),
                            price = objItem.getString("price")
                        ))
                    }

                    manageList.add(Order(
                        orderId = objOrder.getString("id"),
                        userId = objOrder.getString("user_id"),
                        customerName = objOrder.getString("customer_name"),
                        items = itemsList,
                        totalAmount = objOrder.getString("total_price")
                    ))
                }
                recyclerView.adapter = ManageAdminAdapter(manageList)
            } else {
                Toast.makeText(requireContext(), "Belum ada pesanan masuk", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("JSON_ERROR", "Detail Error: ${e.message}")
            Log.e("JSON_ERROR", "Raw JSON: $json")
            Toast.makeText(requireContext(), "Format data salah! Cek Logcat", Toast.LENGTH_SHORT).show()
        }
    }
}