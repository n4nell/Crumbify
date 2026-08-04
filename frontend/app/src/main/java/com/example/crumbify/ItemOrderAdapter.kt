package com.example.crumbify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class ItemOrderAdapter(private val items: List<HistoryItem>) : RecyclerView.Adapter<ItemOrderAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvQty: TextView = view.findViewById(R.id.textView13)
        val tvName: TextView = view.findViewById(R.id.textView14)
        val tvPrice: TextView = view.findViewById(R.id.textView15)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recview_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvQty.text = "${item.qty} x"
        holder.tvName.text = item.name

        val q = item.qty.toDoubleOrNull() ?: 0.0
        val p = item.price.toDoubleOrNull() ?: 0.0
        holder.tvPrice.text = formatRupiah(q * p)
    }

    override fun getItemCount(): Int = items.size

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(number).replace("Rp", "Rp ").replace(",00", "")
    }
}