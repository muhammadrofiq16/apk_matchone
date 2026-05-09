package com.project.matchone.data.model

import com.google.gson.annotations.SerializedName

data class CategoryModel(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String?,
    // Ini keren, karena kamu bisa akses produk langsung dari kategorinya
    @SerializedName("products") val products: List<ProductModel>?
)

data class CategoryResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val categories: List<CategoryModel>
)