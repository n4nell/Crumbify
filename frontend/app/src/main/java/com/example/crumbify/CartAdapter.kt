package com.example.crumbify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.widget.ImageView
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private var list: MutableList<Cart>,
    private val listener: OnCartChangeListener
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    interface OnCartChangeListener {
        fun onChange(itemId: String, newQty: Int)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.textView10)
        val tvPrice: TextView = view.findViewById(R.id.textView11)
        val tvQty: TextView = view.findViewById(R.id.textView18)
        val btnPlus: ImageButton = view.findViewById(R.id.imageButton)
        val btnMinus: ImageButton = view.findViewById(R.id.imageButton4)
        val ivProduct: ImageView = view.findViewById(R.id.imageView4)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recview_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvName.text = item.name

        val priceDouble = item.price.toDoubleOrNull() ?: 0.0
        val formatter = NumberFormat.getInstance(Locale("in", "ID"))
        holder.tvPrice.text = "Rp ${formatter.format(priceDouble)}"

        holder.tvQty.text = String.format("%02d", item.qty.toInt())

        val imageUrl = Konfigurasi.URL_IMAGE + item.image

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.profile)
            .error(R.drawable.profile)
            .centerCrop()
            .into(holder.ivProduct)

        holder.btnPlus.setOnClickListener {
            listener.onChange(item.id, item.qty.toInt() + 1)
        }

        holder.btnMinus.setOnClickListener {
            listener.onChange(item.id, item.qty.toInt() - 1)
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<Cart>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun getData(): List<Cart> {
        return list
    }
}