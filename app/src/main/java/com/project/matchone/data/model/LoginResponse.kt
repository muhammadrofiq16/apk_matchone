package com.project.matchone.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: UserModel?,
    @SerializedName("token") val token: String?
)
