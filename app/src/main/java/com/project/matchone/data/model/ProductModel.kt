package com.project.matchone.data.model

import com.google.gson.annotations.SerializedName

data class ProductModel(
    @SerializedName("id") val id: Int,
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("price") val price: String,
    @SerializedName("image") val image: String?,

    // UBAH KE BOOLEAN: Karena JSON dari Laravel mengirim true/false
    @SerializedName("is_available") val isAvailable: Boolean?
)

data class ProductResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("data") val products: List<ProductModel>
)