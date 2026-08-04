package com.example.crumbify

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart(
    val id: String,
    val name: String,
    val price: String,
    val qty: String,
    val total: String,
    val image: String
) : Parcelable