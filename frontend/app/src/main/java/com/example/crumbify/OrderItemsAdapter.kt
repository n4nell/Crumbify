package com.example.crumbify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale
import kotlin.text.toDoubleOrNull

class OrderItemsAdapter(private val items: List<OrderItem>) :
    RecyclerView.Adapter<OrderItemsAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvQty: TextView = v.findViewById(R.id.textView13)
        val tvName: TextView = v.findViewById(R.id.textView14)
        val tvPrice: TextView = v.findViewById(R.id.textView15)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recview_order, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvQty.text = "${item.qty} x "
        holder.tvName.text = item.productName

        val q = item.qty.toDoubleOrNull() ?: 0.0
        val p = item.price.toDoubleOrNull() ?: 0.0

        val totalHargaItem = q * p

        holder.tvPrice.text = formatRupiah(totalHargaItem)
    }

    override fun getItemCount() = items.size

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(number).replace("Rp", "Rp ").replace(",00", "")
    }
}