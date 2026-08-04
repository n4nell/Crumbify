package com.example.crumbify.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize

data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val imageRes: String,
    val description: String,
    val stock: Int,
    val categoryId: Int,
    var isWishlisted: Boolean = false
) : Parcelable


