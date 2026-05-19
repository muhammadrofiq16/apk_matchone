package com.project.matchone.data.model

import com.google.gson.annotations.SerializedName

data class CheckoutResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("transaction_id")
    val transactionId: Int,

    // Ini yang paling penting untuk membuka halaman pembayaran Midtrans
    @SerializedName("snap_token")
    val snapToken: String
)