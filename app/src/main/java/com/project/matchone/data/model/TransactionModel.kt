package com.project.matchone.data.model

import com.google.gson.annotations.SerializedName

data class OrderListResponse(
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: List<TransactionModel> = emptyList()
)

data class OrderDetailResponse(
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: TransactionModel? = null
)

data class TransactionModel(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("user_id")
    val userId: Int? = null,

    @SerializedName("invoice_number")
    val invoiceNumber: String? = null,

    @SerializedName("total_price")
    val totalPrice: String? = null,

    @SerializedName("total_amount")
    val totalAmount: String? = null,

    @SerializedName("total")
    val total: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("payment_method")
    val paymentMethod: String? = null,

    @SerializedName("notes")
    val notes: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null,

    @SerializedName("user")
    val user: OrderUserModel? = null,

    @SerializedName("order_items")
    val orderItems: List<OrderItemHistoryModel>? = null
) {
    fun getDisplayTotal(): Double {
        return totalPrice?.toDoubleOrNull()
            ?: totalAmount?.toDoubleOrNull()
            ?: total?.toDoubleOrNull()
            ?: 0.0
    }

    fun getDisplayInvoice(): String {
        return invoiceNumber ?: "INV-$id"
    }

    fun getDisplayStatus(): String {
        return when (status?.lowercase()) {
            "pending" -> "Diproses"
            "paid" -> "Dibayar"
            "processing" -> "Diproses"
            "completed" -> "Selesai"
            "cancelled" -> "Dibatalkan"
            else -> status ?: "-"
        }
    }
}

data class OrderUserModel(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("role")
    val role: String? = null,

    @SerializedName("avatar")
    val avatar: String? = null
)

data class OrderItemHistoryModel(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("order_id")
    val orderId: Int? = null,

    @SerializedName("product_id")
    val productId: Int? = null,

    @SerializedName("quantity")
    val quantity: Int? = null,

    @SerializedName("qty")
    val qty: Int? = null,

    @SerializedName("price")
    val price: String? = null,

    @SerializedName("subtotal")
    val subtotal: String? = null,

    @SerializedName("product")
    val product: ProductDetail? = null
)