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

class MyWishlistAdapter(
    private val wishlistItems: List<Product>,
    private val onRemoveClick: (Product) -> Unit
) : RecyclerView.Adapter<MyWishlistAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.textView23)
        val tvPrice: TextView = view.findViewById(R.id.textView22)
        val btnRemove: ImageView = view.findViewById(R.id.imageView8)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recview_wishlist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = wishlistItems[position]

        holder.tvName.text = product.name
        holder.tvPrice.text = if (product.price.contains("Rp")) product.price else "Rp ${product.price}"

        holder.btnRemove.setImageResource(R.drawable.baseline_favorite_24)

        holder.btnRemove.setOnClickListener {
            onRemoveClick(product)
        } }

    override fun getItemCount(): Int = wishlistItems.size
}