package com.project.matchone.data.model

data class CartItem(
    val id: Int, // ID Keranjang
    val product_id: Int,
    val name: String,
    val price: Int,
    val quantity: Int,
    val image: String?
)

data class CartSummary(
    val total_items: Int,
    val total_price: Int
)

data class CartResponse(
    val message: String,
    val data: List<CartItem>?
)