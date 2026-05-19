package com.project.matchone.data.model

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val items: List<CartItem> = listOf(), // Ini penting, default-nya list kosong
    val totalAmount: Double = 0.0,
    val paymentMethod: String = "",
    val status: String = "MENUNGGU",
    val orderDate: Long = 0L
)