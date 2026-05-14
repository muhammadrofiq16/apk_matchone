package com.project.matchone.utils

import com.google.gson.annotations.SerializedName
import com.project.matchone.data.model.CartItem

data class CartResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: CartItem
)