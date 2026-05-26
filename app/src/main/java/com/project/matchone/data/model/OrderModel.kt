package com.project.matchone.data.model

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<OrderModel>
)

data class OrderModel(
    @SerializedName("id") val id: Int,
    @SerializedName("invoice_number") val invoiceNumber: String,
    @SerializedName("total_price") val totalPrice: String,
    @SerializedName("status") val status: String,
    @SerializedName("payment_method") val paymentMethod: String?,
    @SerializedName("created_at") val createdAt: String
)