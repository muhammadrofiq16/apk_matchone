package com.project.matchone.data.model

import com.google.gson.annotations.SerializedName

data class CheckoutResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("order")
    val order: OrderDetail
)

data class OrderDetail(
    @SerializedName("id")
    val id: Int,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("invoice_number")
    val invoiceNumber: String,

    @SerializedName("total_price")
    val totalPrice: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("payment_method")
    val paymentMethod: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("order_items")
    val orderItems: List<OrderItemDetail>
)

data class OrderItemDetail(
    @SerializedName("id")
    val id: Int,

    @SerializedName("product_id")
    val productId: Int,

    @SerializedName("qty")
    val qty: Int,

    @SerializedName("price_at_purchase")
    val priceAtPurchase: String
)