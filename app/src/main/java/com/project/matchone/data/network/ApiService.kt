package com.project.matchone.data.network

import com.project.matchone.data.model.CartListResponse
import com.project.matchone.data.model.CartSummary
import com.project.matchone.data.model.CategoryResponse
import com.project.matchone.data.model.LoginResponse
import com.project.matchone.data.model.ProductResponse
import com.project.matchone.data.model.UserModel
import com.project.matchone.utils.CartResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

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
        @Field("password_confirmation") passwordConfirm: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("api/auth/google")
    fun googleLogin(
        @Field("google_token") googleToken: String
    ): Call<LoginResponse>

    @POST("api/auth/logout")
    fun logoutUser(
        @Header("Authorization") token: String
    ): Call<Void>

    @GET("api/auth/profile")
    fun getUserProfile(
        @Header("Authorization") token: String
    ): Call<UserModel>

    @FormUrlEncoded
    @PUT("api/auth/profile")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Field("name") name: String,
        @Field("phone") phone: String
    ): Call<UserModel>

    @GET("api/categories")
    fun getCategories(): Call<CategoryResponse>

    @GET("api/products")
    fun getProducts(): Call<ProductResponse>

    @GET("api/products")
    fun getProductsByCategory(
        @Query("category_id") categoryId: Int
    ): Call<ProductResponse>

    @GET("api/cart")
    fun getCart(
        @Header("Authorization") token: String
    ): Call<CartListResponse>

    @FormUrlEncoded
    @POST("api/cart")
    fun addToCart(
        @Header("Authorization") token: String,
        @Field("product_id") productId: Int,
        @Field("qty") qty: Int
    ): Call<CartResponse>

    @FormUrlEncoded
    @PUT("api/cart/{id}")
    fun updateCart(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Field("qty") qty: Int
    ): Call<ResponseBody>

    @DELETE("api/cart/{id}")
    fun deleteCartItem(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<CartListResponse>

    @DELETE("api/cart")
    fun clearCart(
        @Header("Authorization") token: String
    ): Call<CartListResponse>

    @GET("api/cart/summary")
    fun getCartSummary(
        @Header("Authorization") token: String
    ): Call<CartSummary>

    @POST("api/checkout")
    fun checkoutCart(
        @Header("Authorization") token: String
    ): Call<com.project.matchone.data.model.CheckoutResponse>

    @GET("api/transactions")
    fun getTransactions(
        @Header("Authorization") token: String
    ): Call<List<com.project.matchone.data.model.TransactionModel>>
}