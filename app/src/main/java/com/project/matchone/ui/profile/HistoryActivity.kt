package com.project.matchone.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.matchone.R
import com.project.matchone.adapter.HistoryAdapter
import com.project.matchone.data.model.HistoryModel
import com.project.matchone.data.model.OrderResponse
import com.project.matchone.data.network.ApiClient
import com.project.matchone.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val rvHistory = findViewById<RecyclerView>(R.id.recyclerViewHistory)
        rvHistory.layoutManager = LinearLayoutManager(this)

        val token = "Bearer ${SessionManager(this).fetchAuthToken()}"

        ApiClient.instance.getOrders(token).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(
                call: Call<OrderResponse>,
                response: Response<OrderResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

                    val historyList = response.body()!!.data.map { order ->
                        HistoryModel(
                            orderId = order.invoiceNumber,
                            date = order.createdAt.substring(0, 10),
                            totalAmount = formatRupiah.format(
                                order.totalPrice.toDoubleOrNull() ?: 0.0
                            ).replace("Rp", "Rp "),
                            status = when (order.status) {
                                "completed" -> "Selesai"
                                "paid"      -> "Dibayar"
                                "pending"   -> "Menunggu"
                                "cancelled" -> "Dibatalkan"
                                else        -> order.status
                            }
                        )
                    }

                    rvHistory.adapter = HistoryAdapter(historyList)

                } else {
                    Toast.makeText(
                        this@HistoryActivity,
                        "Gagal memuat riwayat (${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                Toast.makeText(
                    this@HistoryActivity,
                    "Koneksi bermasalah: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}