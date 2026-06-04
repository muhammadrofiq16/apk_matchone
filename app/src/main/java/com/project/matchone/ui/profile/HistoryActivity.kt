package com.project.matchone.ui.profile

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.matchone.data.model.OrderListResponse
import com.project.matchone.data.model.TransactionModel
import com.project.matchone.data.network.ApiClient
import com.project.matchone.ui.checkout.StrukActivity
import com.project.matchone.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class HistoryActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    private lateinit var rootLayout: LinearLayout
    private lateinit var chipContainer: LinearLayout
    private lateinit var contentContainer: LinearLayout
    private lateinit var progressBar: ProgressBar

    private var allTransactions: List<TransactionModel> = emptyList()
    private var currentFilter: String = "all"

    private val greenColor = 0xFF315D33.toInt()
    private val darkTextColor = 0xFF1F1F1F.toInt()
    private val grayTextColor = 0xFF888888.toInt()
    private val lightBgColor = 0xFFF7F8F3.toInt()
    private val borderColor = 0xFFE0E0E0.toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        buildLayout()
        loadTransactions()
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }

    private fun buildLayout() {
        rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(lightBgColor)
            setPadding(dp(20), dp(34), dp(20), dp(20))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val btnBack = TextView(this).apply {
            text = "‹"
            textSize = 46f
            setTextColor(darkTextColor)
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(dp(45), dp(55))
            setOnClickListener { finish() }
        }

        val tvTitle = TextView(this).apply {
            text = "Riwayat Pesanan"
            textSize = 25f
            setTextColor(greenColor)
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val spacer = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(45), dp(55))
        }

        headerLayout.addView(btnBack)
        headerLayout.addView(tvTitle)
        headerLayout.addView(spacer)

        val horizontalScroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(20)
            }
        }

        chipContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        horizontalScroll.addView(chipContainer)

        progressBar = ProgressBar(this).apply {
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(
                dp(45),
                dp(45)
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = dp(40)
            }
        }

        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            ).apply {
                topMargin = dp(20)
            }
        }

        contentContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        scrollView.addView(contentContainer)

        rootLayout.addView(headerLayout)
        rootLayout.addView(horizontalScroll)
        rootLayout.addView(progressBar)
        rootLayout.addView(scrollView)

        setContentView(rootLayout)

        renderFilterChips()
    }

    private fun renderFilterChips() {
        chipContainer.removeAllViews()

        chipContainer.addView(createChip("Semua", "all"))
        chipContainer.addView(createChip("Selesai", "completed"))
        chipContainer.addView(createChip("Diproses", "processing"))
        chipContainer.addView(createChip("Dibatalkan", "cancelled"))
    }

    private fun createChip(text: String, filter: String): TextView {
        val isSelected = currentFilter == filter

        return TextView(this).apply {
            this.text = text
            textSize = 16f
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            setPadding(dp(26), dp(10), dp(26), dp(10))
            setTextColor(if (isSelected) 0xFFFFFFFF.toInt() else 0xFF555555.toInt())
            background = createRoundedDrawable(
                color = if (isSelected) greenColor else 0xFFFFFFFF.toInt(),
                strokeColor = if (isSelected) greenColor else borderColor,
                radius = dp(28).toFloat()
            )
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dp(55)
            ).apply {
                rightMargin = dp(10)
            }

            setOnClickListener {
                currentFilter = filter
                renderFilterChips()
                displayTransactions()
            }
        }
    }

    private fun loadTransactions() {
        val authToken = sessionManager.fetchAuthToken()

        if (authToken.isNullOrEmpty()) {
            Toast.makeText(
                this,
                "Token tidak ditemukan, silakan login ulang",
                Toast.LENGTH_SHORT
            ).show()
            showEmptyState()
            return
        }

        progressBar.visibility = View.VISIBLE

        val token = "Bearer $authToken"

        ApiClient.instance.getTransactions(token).enqueue(object : Callback<OrderListResponse> {

            override fun onResponse(
                call: Call<OrderListResponse>,
                response: Response<OrderListResponse>
            ) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    allTransactions = response.body()?.data ?: emptyList()
                    displayTransactions()
                } else {
                    Toast.makeText(
                        this@HistoryActivity,
                        "Gagal memuat riwayat pesanan: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    showEmptyState()
                }
            }

            override fun onFailure(call: Call<OrderListResponse>, t: Throwable) {
                progressBar.visibility = View.GONE

                Toast.makeText(
                    this@HistoryActivity,
                    "Koneksi bermasalah: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()

                showEmptyState()
            }
        })
    }

    private fun displayTransactions() {
        val filteredTransactions = when (currentFilter) {
            "completed" -> allTransactions.filter {
                it.status.equals("completed", true) ||
                        it.status.equals("paid", true)
            }

            "processing" -> allTransactions.filter {
                it.status.equals("pending", true) ||
                        it.status.equals("processing", true)
            }

            "cancelled" -> allTransactions.filter {
                it.status.equals("cancelled", true) ||
                        it.status.equals("canceled", true)
            }

            else -> allTransactions
        }

        if (filteredTransactions.isEmpty()) {
            showEmptyState()
        } else {
            showTransactionList(filteredTransactions)
        }
    }

    private fun showTransactionList(transactions: List<TransactionModel>) {
        contentContainer.removeAllViews()
        contentContainer.gravity = Gravity.NO_GRAVITY

        transactions.forEach { transaction ->
            contentContainer.addView(createTransactionCard(transaction))
        }
    }

    private fun createTransactionCard(transaction: TransactionModel): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = createRoundedDrawable(
                color = 0xFFFFFFFF.toInt(),
                strokeColor = 0xFFE6E6E6.toInt(),
                radius = dp(18).toFloat()
            )
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(14)
            }

            setOnClickListener {
                openOrderDetail(transaction)
            }
        }

        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val invoiceText = TextView(this).apply {
            text = transaction.getDisplayInvoice()
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(darkTextColor)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val statusText = TextView(this).apply {
            text = transaction.getDisplayStatus()
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(dp(12), dp(6), dp(12), dp(6))
            setTextColor(getStatusTextColor(transaction.status))
            background = createRoundedDrawable(
                color = getStatusBgColor(transaction.status),
                strokeColor = getStatusBgColor(transaction.status),
                radius = dp(18).toFloat()
            )
        }

        topRow.addView(invoiceText)
        topRow.addView(statusText)

        val dateText = TextView(this).apply {
            text = formatDate(transaction.createdAt)
            textSize = 13f
            setTextColor(grayTextColor)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(8)
            }
        }

        val paymentText = TextView(this).apply {
            text = "Metode: ${formatPaymentMethod(transaction.paymentMethod)}"
            textSize = 13f
            setTextColor(grayTextColor)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(4)
            }
        }

        val totalText = TextView(this).apply {
            text = formatCurrency(transaction.getDisplayTotal())
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(greenColor)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(10)
            }
        }

        card.addView(topRow)
        card.addView(dateText)
        card.addView(paymentText)
        card.addView(totalText)

        return card
    }

    private fun showEmptyState() {
        contentContainer.removeAllViews()
        contentContainer.gravity = Gravity.CENTER_HORIZONTAL
        contentContainer.setPadding(0, dp(170), 0, 0)

        val icon = TextView(this).apply {
            text = "🧾"
            textSize = 58f
            gravity = Gravity.CENTER
        }

        val title = TextView(this).apply {
            text = "Belum ada pesanan"
            textSize = 23f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(darkTextColor)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(16)
            }
        }

        val desc = TextView(this).apply {
            text = "Pesananmu akan muncul di sini\nsetelah kamu berhasil checkout."
            textSize = 17f
            setTextColor(grayTextColor)
            gravity = Gravity.CENTER
            setLineSpacing(dp(3).toFloat(), 1.0f)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(12)
            }
        }

        contentContainer.addView(icon)
        contentContainer.addView(title)
        contentContainer.addView(desc)
    }

    private fun openOrderDetail(transaction: TransactionModel) {
        val intent = Intent(this, StrukActivity::class.java).apply {
            putExtra("ORDER_ID", transaction.id)
            putExtra("INVOICE_NUMBER", transaction.getDisplayInvoice())
            putExtra("TOTAL_PRICE", transaction.getDisplayTotal().toString())
            putExtra("STATUS", transaction.getDisplayStatus())
            putExtra("PAYMENT_METHOD", transaction.paymentMethod ?: "-")
            putExtra("PAYMENT_SUB_METHOD", "")
            putExtra("CREATED_AT", transaction.createdAt ?: "")
            putExtra("ITEMS_SUMMARY", buildItemsSummary(transaction))
        }

        startActivity(intent)
    }

    private fun buildItemsSummary(transaction: TransactionModel): String {
        val items = transaction.orderItems

        if (items.isNullOrEmpty()) {
            return "-"
        }

        return items.joinToString("\n") { item ->
            val productName = item.product?.name ?: "Produk"
            val quantity = item.quantity ?: item.qty ?: 1
            "$productName x$quantity"
        }
    }

    private fun formatCurrency(value: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return formatter.format(value).replace("Rp", "Rp ")
    }

    private fun formatPaymentMethod(method: String?): String {
        return when (method?.lowercase()) {
            "belum_dipilih" -> "Belum dipilih"
            "bank_transfer" -> "Transfer Bank"
            "transfer_bank" -> "Transfer Bank"
            "e_wallet" -> "E-Wallet"
            "cash_on_delivery" -> "COD"
            "cod" -> "COD"
            else -> method ?: "-"
        }
    }

    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "-"

        return try {
            val inputFormat = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
                Locale.getDefault()
            ).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

            val outputFormat = SimpleDateFormat(
                "dd MMM yyyy, HH:mm",
                Locale("in", "ID")
            )

            val date = inputFormat.parse(dateString)
            if (date != null) outputFormat.format(date) else dateString
        } catch (e: Exception) {
            dateString
        }
    }

    private fun getStatusBgColor(status: String?): Int {
        return when (status?.lowercase()) {
            "pending" -> 0xFFFFF4D6.toInt()
            "processing" -> 0xFFE9E1FF.toInt()
            "paid" -> 0xFFDDEBFF.toInt()
            "completed" -> 0xFFDFF5E1.toInt()
            "cancelled", "canceled" -> 0xFFFFE0E0.toInt()
            else -> 0xFFEFEFEF.toInt()
        }
    }

    private fun getStatusTextColor(status: String?): Int {
        return when (status?.lowercase()) {
            "pending" -> 0xFF9A6A00.toInt()
            "processing" -> 0xFF5B3E99.toInt()
            "paid" -> 0xFF1D5EA8.toInt()
            "completed" -> 0xFF267A34.toInt()
            "cancelled", "canceled" -> 0xFFA83232.toInt()
            else -> 0xFF555555.toInt()
        }
    }

    private fun createRoundedDrawable(
        color: Int,
        strokeColor: Int,
        radius: Float
    ): android.graphics.drawable.GradientDrawable {
        return android.graphics.drawable.GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius
            setStroke(dp(1), strokeColor)
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}