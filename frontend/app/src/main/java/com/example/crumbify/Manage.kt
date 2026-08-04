package com.example.crumbify

data class OrderItem(
    val productName: String,
    val qty: String,
    val price: String
)

data class Order(
    val orderId: String,
    val userId: String,
    val customerName: String,
    val items: List<OrderItem>,
    val totalAmount: String
)