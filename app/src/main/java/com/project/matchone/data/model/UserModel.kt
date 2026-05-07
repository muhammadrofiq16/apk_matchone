package com.project.matchone.data.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("email_verified_at") val emailVerifiedAt: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("points") val points: Int,
    @SerializedName("role") val role: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)
