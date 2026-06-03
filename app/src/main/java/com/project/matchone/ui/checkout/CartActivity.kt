package com.project.matchone.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.project.matchone.R
import com.project.matchone.data.model.CheckoutResponse
import com.project.matchone.data.network.ApiClient
import com.project.matchone.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class PaymentActivity : AppCompatActivity() {

    private lateinit var tvTotalPayment: TextView
    private lateinit var btnConfirmPayment: MaterialButton
    private lateinit var sessionManager: SessionManager

    private var totalAmount: Double = 0.0
    private var selectedPaymentMethod: String = ""

    // Semua card metode pembayaran
    private lateinit var cardDana: LinearLayout
    private lateinit var cardGopay: LinearLayout
    private lateinit var cardOvo: LinearLayout
    private lateinit var cardShopeepay: LinearLayout
    private lateinit var cardMidtrans: LinearLayout
    private lateinit var cardTransfer: LinearLayout
    private lateinit var cardCod: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        sessionManager = SessionManager(this)

        // Inisialisasi views
        tvTotalPayment   = findViewById(R.id.tvTotalPayment)
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment)

        cardDana        = findViewById(R.id.cardDana)
        cardGopay       = findViewById(R.id.cardGopay)
        cardOvo         = findViewById(R.id.cardOvo)
        cardShopeepay   = findViewById(R.id.cardShopeepay)
        cardMidtrans    = findViewById(R.id.cardMidtrans)
        cardTransfer    = findViewById(R.id.cardTransfer)
        cardCod         = findViewById(R.id.cardCod)

        // Ambil total dari CartActivity
        totalAmount = intent.getDoubleExtra("EXTRA_TOTAL_AMOUNT", 0.0)
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        tvTotalPayment.text = formatRupiah.format(totalAmount).replace("Rp", "Rp ")

        // Setup klik setiap kartu
        setupPaymentCard(cardDana,       "DANA")
        setupPaymentCard(cardGopay,      "GoPay")
        setupPaymentCard(cardOvo,        "OVO")
        setupPaymentCard(cardShopeepay,  "ShopeePay")
        setupPaymentCard(cardMidtrans,   "Midtrans")
        setupPaymentCard(cardTransfer,   "Transfer Bank")
        setupPaymentCard(cardCod,        "COD")

        // Tombol konfirmasi
        btnConfirmPayment.setOnClickListener { processCheckout() }

        // Tombol back
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun setupPaymentCard(card: LinearLayout, methodName: String) {
        card.setOnClickListener {
            // Reset semua card
            listOf(cardDana, cardGopay, cardOvo, cardShopeepay, cardMidtrans, cardTransfer, cardCod)
                .forEach { it.isSelected = false }

            // Pilih card ini
            card.isSelected = true
            selectedPaymentMethod = methodName

            // Aktifkan tombol konfirmasi
            btnConfirmPayment.isEnabled = true
            btnConfirmPayment.alpha = 1.0f
        }
    }

    private fun processCheckout() {
        if (selectedPaymentMethod.isEmpty()) {
            Toast.makeText(this, "Silakan pilih metode pembayaran!", Toast.LENGTH_SHORT).show()
            return
        }

        btnConfirmPayment.isEnabled = false
        btnConfirmPayment.text = "Memproses..."

        val token = "Bearer ${sessionManager.fetchAuthToken()}"

        ApiClient.instance.checkoutCart(token, selectedPaymentMethod)
            .enqueue(object : Callback<CheckoutResponse> {

                override fun onResponse(
                    call: Call<CheckoutResponse>,
                    response: Response<CheckoutResponse>
                ) {
                    btnConfirmPayment.isEnabled = true
                    btnConfirmPayment.text = "Konfirmasi Pembayaran"

                    if (response.isSuccessful && response.body() != null) {
                        val order = response.body()!!.order

                        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                        val itemsSummary = order.orderItems.joinToString("\n") { item ->
                            val subtotal = formatRupiah.format(
                                (item.priceAtPurchase.toDoubleOrNull() ?: 0.0) * item.qty
                            ).replace("Rp", "Rp ")
                            "• Produk #${item.productId}  x${item.qty}  →  $subtotal"
                        }

                        val intent = Intent(this@PaymentActivity, StrukActivity::class.java).apply {
                            putExtra("INVOICE_NUMBER", order.invoiceNumber)
                            putExtra("TOTAL_PRICE", order.totalPrice)
                            putExtra("STATUS", order.status)
                            putExtra("PAYMENT_METHOD", selectedPaymentMethod)
                            putExtra("CREATED_AT", order.createdAt)
                            putExtra("ITEMS_SUMMARY", itemsSummary)
                        }
                        startActivity(intent)
                        // Tutup CartActivity & PaymentActivity sekaligus
                        finishAffinity()

                    } else {
                        Toast.makeText(
                            this@PaymentActivity,
                            "Gagal checkout: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<CheckoutResponse>, t: Throwable) {
                    btnConfirmPayment.isEnabled = true
                    btnConfirmPayment.text = "Konfirmasi Pembayaran"
                    Toast.makeText(
                        this@PaymentActivity,
                        "Koneksi bermasalah: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}