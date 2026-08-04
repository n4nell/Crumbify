package com.example.crumbify

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale

class ManageAdminAdapter(private val orders: ArrayList<Order>) :
    RecyclerView.Adapter<ManageAdminAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHeader: TextView = view.findViewById(R.id.textView41)
        val tvTotal: TextView = view.findViewById(R.id.textView42)
        val rvItems: RecyclerView = view.findViewById(R.id.recyclerView2)
        val btnComplete: MaterialButton = view.findViewById(R.id.button5)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recview_manage_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.tvHeader.text = "Order : #${order.orderId} - ${order.customerName}"
        val totalDouble = order.totalAmount.toDoubleOrNull() ?: 0.0
        holder.tvTotal.text = "Total : ${formatRupiah(totalDouble)}"

        holder.rvItems.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvItems.adapter = OrderItemsAdapter(order.items)

        holder.btnComplete.setOnClickListener {
            val currentPos = holder.adapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                showConfirmDialog(holder.itemView.context, order, currentPos)
            }
        }
    }

    private fun showConfirmDialog(context: Context, order: Order, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Confirm Order")
            .setMessage("Are you sure order #${order.orderId} is ready?")
            .setPositiveButton("Yes") { _, _ -> sendNotification(order.orderId, order.userId, context, position) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun sendNotification(orderId: String, userId: String, context: Context, position: Int) {
        val data = HashMap<String, String>().apply {
            put("order_id", orderId)
            put("user_id", userId)
            put("action", "complete_order")
        }

        @SuppressLint("StaticFieldLeak")
        val asyncTask = object : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String? =
                RequestHandler().sendPostRequest(Konfigurasi.URL_SEND_NOTIF, data)

            override fun onPostExecute(result: String?) {
                try {
                    val json = JSONObject(result!!)
                    if (json.getBoolean("status")) {
                        orders.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, orders.size)
                        Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("ERROR_PARSING", "Check PHP Output: $result")
                    Toast.makeText(context, "Format Salah! Cek Logcat", Toast.LENGTH_LONG).show()
                }
            }
        }
        asyncTask.execute()
    }

    override fun getItemCount(): Int = orders.size
    private fun formatRupiah(n: Double): String =
        NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(n).replace(",00", "")
}