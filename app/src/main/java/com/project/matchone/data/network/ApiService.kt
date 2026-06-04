package com.project.matchone.data.network

import com.project.matchone.data.model.CartListResponse
import com.project.matchone.data.model.CartSummary
import com.project.matchone.data.model.CategoryResponse
import com.project.matchone.data.model.CheckoutResponse
import com.project.matchone.data.model.LoginResponse
import com.project.matchone.data.model.OrderDetailResponse
import com.project.matchone.data.model.OrderListResponse
import com.project.matchone.data.model.ProductResponse
import com.project.matchone.data.model.TransactionModel
import com.project.matchone.data.model.UserModel
import com.project.matchone.utils.CartResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // ========================= AUTH =========================

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

    // ========================= KATEGORI =========================

    @GET("api/categories")
    fun getCategories(): Call<CategoryResponse>

    // ========================= PRODUK =========================

    @GET("api/products")
    fun getProducts(): Call<ProductResponse>

    @GET("api/products")
    fun getProductsByCategory(
        @Query("category_id") categoryId: Int
    ): Call<ProductResponse>

    // ========================= KERANJANG =========================

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

    // ========================= CHECKOUT =========================

    @POST("api/checkout")
    fun checkoutCart(
        @Header("Authorization") token: String
    ): Call<CheckoutResponse>

    // ========================= PAYMENT =========================

    @Multipart
    @POST("api/payments")
    fun uploadPayment(
        @Header("Authorization") token: String,
        @Part("order_id") orderId: RequestBody,
        @Part("payment_type") paymentType: RequestBody,
        @Part("amount_paid") amountPaid: RequestBody,
        @Part paymentProof: MultipartBody.Part
    ): Call<ResponseBody>

    // ========================= RIWAYAT PESANAN / ORDERS =========================

    @GET("api/orders")
    fun getTransactions(
        @Header("Authorization") token: String
    ): Call<OrderListResponse>

    @GET("api/orders/{id}")
    fun getTransactionDetail(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<OrderDetailResponse>

    // Function cadangan kalau ada file lama yang masih memanggil TransactionModel langsung
    @GET("api/orders/{id}")
    fun getTransactionDetailRaw(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<TransactionModel>
}