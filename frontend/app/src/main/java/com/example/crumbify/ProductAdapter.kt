package com.example.crumbify.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.crumbify.R
import com.example.crumbify.model.Product
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private val products: List<Product>,
    private val onWishlistClick: (Product) -> Unit,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProduct: ImageView = view.findViewById(R.id.imageView4)
        val tvName: TextView = view.findViewById(R.id.textView10)
        val tvPrice: TextView = view.findViewById(R.id.textView11)
        val btnWishlist: ImageButton = view.findViewById(R.id.imageButton3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recview_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.tvName.text = product.name

        val cleanPrice = product.price.replace(Regex("[^\\d]"), "")
        val priceDouble = cleanPrice.toDoubleOrNull() ?: 0.0

        holder.tvPrice.text = formatRupiah(priceDouble)

        if (product.isWishlisted) {
            holder.btnWishlist.setImageResource(R.drawable.baseline_favorite_24)
        } else {
            holder.btnWishlist.setImageResource(R.drawable.baseline_favorite_border_24)
        }

        Glide.with(holder.itemView.context).load(product.imageRes).into(holder.ivProduct)

        holder.btnWishlist.setOnClickListener { onWishlistClick(product) }
        holder.itemView.setOnClickListener { onItemClick(product) }
    }

    override fun getItemCount() = products.size

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(number).replace("Rp", "Rp ").replace(",00", "")
    }
}