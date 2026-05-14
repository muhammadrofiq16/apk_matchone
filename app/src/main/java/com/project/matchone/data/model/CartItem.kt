package com.project.matchone.data.model

import com.google.gson.annotations.SerializedName

// 1. Response Utama
data class CartListResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<CartItem>,

    @SerializedName("total")
    val total: Int,

    @SerializedName("count")
    val count: Int
)

// 2. Item dalam Keranjang
data class CartItem(
    val id: Int = 0,
    @SerializedName("product_id")
    val product_id: Int = 0,
    val quantity: Int = 0,
    @SerializedName("subtotal")
    val subtotal: Int = 0,
    val product: ProductDetail? = null
) {
    val name: String get() = product?.name ?: ""
    val image: String get() = product?.image ?: ""
}

// 3. Detail Produk
data class ProductDetail(
    val id: Int,
    val name: String,
    val price: String,
    val image: String?
)

// 4. Tambahkan ini agar error di ApiService.kt hilang!
data class CartSummary(
    @SerializedName("total_items")
    val totalItems: Int,
    @SerializedName("total_price")
    val totalPrice: Double
)