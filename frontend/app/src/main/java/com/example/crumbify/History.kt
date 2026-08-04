package com.example.crumbify

import com.google.gson.annotations.SerializedName

data class History(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("total_order") val totalOrder: Double,
    val items: List<HistoryItem>
)

data class HistoryItem(
    val name: String,
    val qty: String,
    val price: String
)