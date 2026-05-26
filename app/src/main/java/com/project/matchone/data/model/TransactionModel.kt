package com.project.matchone.data.model

import com.google.gson.annotations.SerializedName

data class TransactionModel(
    @SerializedName("id")
    val id: Int,

    @SerializedName("order_id")
    val orderId: String,

    @SerializedName("total_price")
    val totalPrice: Int,

    @SerializedName("status")
    val status: String, // misal: "pending", "success", "failed"

    @SerializedName("created_at")
    val createdAt: String
)