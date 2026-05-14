package com.project.matchone.utils

import com.project.matchone.data.model.CartItem
import com.project.matchone.data.model.CartListResponse
import com.project.matchone.utils.CartResponse
import com.project.matchone.data.network.ApiClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartRepository(private val token: String) {

    private fun bearer() = "Bearer $token"

    fun getCart(
        onSuccess: (List<CartItem>) -> Unit,
        onError: (String) -> Unit
    ) {
        ApiClient.instance.getCart(bearer())
            .enqueue(object : Callback<CartListResponse> {
                override fun onResponse(
                    call: Call<CartListResponse>,
                    response: Response<CartListResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        onSuccess(response.body()!!.data)
                    } else {
                        onError("Gagal memuat keranjang (${response.code()})")
                    }
                }

                override fun onFailure(
                    call: Call<CartListResponse>,
                    t: Throwable
                ) {
                    onError("Koneksi bermasalah: ${t.message}")
                }
            })
    }

    fun addToCart(
        productId: Int,
        quantity: Int = 1,
        onSuccess: (CartResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        ApiClient.instance.addToCart(bearer(), productId, quantity)
            .enqueue(object : Callback<CartResponse> {
                override fun onResponse(
                    call: Call<CartResponse>,
                    response: Response<CartResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        onSuccess(response.body()!!)
                    } else {
                        onError("Gagal menambah ke keranjang (${response.code()})")
                    }
                }

                override fun onFailure(
                    call: Call<CartResponse>,
                    t: Throwable
                ) {
                    onError("Koneksi bermasalah: ${t.message}")
                }
            })
    }

    fun updateCart(
        cartId: Int,
        newQuantity: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        ApiClient.instance.updateCart(bearer(), cartId, newQuantity)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        onError("Gagal update keranjang (${response.code()})")
                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody>,
                    t: Throwable
                ) {
                    onError("Koneksi bermasalah: ${t.message}")
                }
            })
    }

    fun deleteItem(
        cartId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        ApiClient.instance.deleteCartItem(bearer(), cartId)
            .enqueue(object : Callback<CartListResponse> {
                override fun onResponse(
                    call: Call<CartListResponse>,
                    response: Response<CartListResponse>
                ) {
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        onError("Gagal hapus item (${response.code()})")
                    }
                }

                override fun onFailure(
                    call: Call<CartListResponse>,
                    t: Throwable
                ) {
                    onError("Koneksi bermasalah: ${t.message}")
                }
            })
    }

    fun clearCart(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        ApiClient.instance.clearCart(bearer())
            .enqueue(object : Callback<CartListResponse> {
                override fun onResponse(
                    call: Call<CartListResponse>,
                    response: Response<CartListResponse>
                ) {
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        onError("Gagal bersihkan keranjang (${response.code()})")
                    }
                }

                override fun onFailure(
                    call: Call<CartListResponse>,
                    t: Throwable
                ) {
                    onError("Koneksi bermasalah: ${t.message}")
                }
            })
    }
}