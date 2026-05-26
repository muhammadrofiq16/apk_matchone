package com.project.matchone.data.model

data class CartSummary(
    val success: Boolean,
    val data: CartSummaryData
)

data class CartSummaryData(
    val total_items: Int,
    val total_quantity: Int,
    val total_price: Int
)