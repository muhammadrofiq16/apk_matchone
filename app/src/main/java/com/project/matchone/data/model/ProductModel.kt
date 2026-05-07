package com.project.matchone.data.model

import com.google.gson.annotations.SerializedName

data class ProductModel(
    @SerializedName("id") val id: Int,
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("price") val price: String, // Di JSON adalah String "40000.00"
    @SerializedName("image") val image: String?,
    @SerializedName("is_available") val isAvailable: Boolean? // Sesuai JSON: is_available
)

data class ProductResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("data") val products: List<ProductModel> // Map "data" ke "products"
)
