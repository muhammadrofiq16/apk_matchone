package com.project.matchone.data.network

import com.project.matchone.data.model.CategoryResponse
import com.project.matchone.data.model.LoginResponse
import com.project.matchone.data.model.ProductResponse
import com.project.matchone.data.model.UserModel
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // --- 1. AUTHENTICATION (Sesuai Route Laravel: /api/auth/login & /api/auth/register) ---

    @FormUrlEncoded
    @POST("api/auth/login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("api/auth/register")
    fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String?,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirm: String // Penting untuk validasi 'confirmed' di Laravel
    ): Call<LoginResponse>

    @POST("api/auth/logout")
    fun logoutUser(
        @Header("Authorization") token: String
    ): Call<Void>


    // --- 2. USER PROFILE ---

    @GET("api/user")
    fun getUserProfile(
        @Header("Authorization") token: String
    ): Call<UserModel>

    @FormUrlEncoded
    @PUT("api/user/update")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String
    ): Call<UserModel>


    // --- 3. CATEGORIES & PRODUCTS ---

    @GET("api/categories")
    fun getCategories(): Call<CategoryResponse>

    @GET("api/products")
    fun getProducts(): Call<ProductResponse>

    @GET("api/products")
    fun getProductsByCategory(
        @Query("category_id") categoryId: Int
    ): Call<ProductResponse>
    // --- 4. CART SYSTEM (Menyesuaikan route Laravel) ---

    // Di ApiService.kt
    @GET("api/cart")
    fun getCart(
        @Header("Authorization") token: String
    ): Call<List<com.project.matchone.data.model.CartItem>>
// ^ Pastikan ini List<CartItem>, bukan List<CartResponse>

    @FormUrlEncoded
    @POST("api/cart")
    fun addToCart(
        @Header("Authorization") token: String,
        @Field("product_id") productId: Int,
        @Field("quantity") quantity: Int
    ): Call<com.project.matchone.data.model.CartResponse>

    @FormUrlEncoded
    @PUT("api/cart/{id}")
    fun updateCart(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Field("quantity") quantity: Int
    ): Call<com.project.matchone.data.model.CartResponse>

    @DELETE("api/cart/{id}")
    fun deleteCartItem(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<com.project.matchone.data.model.CartResponse>

    @DELETE("api/cart")
    fun clearCart(
        @Header("Authorization") token: String
    ): Call<com.project.matchone.data.model.CartResponse>

    @GET("api/cart/summary")
    fun getCartSummary(
        @Header("Authorization") token: String
    ): Call<com.project.matchone.data.model.CartSummary>
}
