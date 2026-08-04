package com.example.crumbify.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.crumbify.R
import com.example.crumbify.model.Product
import java.text.NumberFormat
import java.util.Locale

class ProductAdminAdapter(
    private val productList: List<Product>,
    private val onEditClick: (Product) -> Unit,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdminAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.textView100)
        val tvPrice: TextView = itemView.findViewById(R.id.textView110)
        val ivProduct: ImageView = itemView.findViewById(R.id.imageView40)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recview_product_admin, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.tvName.text = product.name
        val cleanPrice = product.price.replace(Regex("[^\\d]"), "")
        val priceDouble = cleanPrice.toDoubleOrNull() ?: 0.0
        holder.tvPrice.text = formatRupiah(priceDouble)

        Glide.with(holder.itemView.context)
            .load(product.imageRes)
            .placeholder(R.drawable.cinnamon_roll)
            .error(R.drawable.cinnamon_roll)
            .into(holder.ivProduct)

        holder.itemView.setOnClickListener {
            onItemClick(product)
        }

        holder.itemView.setOnLongClickListener {
            onEditClick(product)
            true
        }
    }

    override fun getItemCount(): Int = productList.size

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(number).replace("Rp", "Rp ").replace(",00", "")
    }
}