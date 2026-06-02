package com.project.matchone.ui.checkout

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.TextView
import android.widget.Toast
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.project.matchone.R
import com.project.matchone.ui.main.HomeActivity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class StrukActivity : AppCompatActivity() {

    private lateinit var strukContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_struk)

        strukContainer      = findViewById(R.id.strukContainer)
        val tvInvoice       = findViewById<TextView>(R.id.tvInvoiceNumber)
        val tvDate          = findViewById<TextView>(R.id.tvDate)
        val tvStatusBadge   = findViewById<TextView>(R.id.tvStatusBadge)
        val tvPayment       = findViewById<TextView>(R.id.tvPaymentMethod)
        val tvTotal         = findViewById<TextView>(R.id.tvTotal)
        val tvSubtotal      = findViewById<TextView>(R.id.tvSubtotal)
        val tvItems         = findViewById<TextView>(R.id.tvItems)
        val btnDownload     = findViewById<MaterialButton>(R.id.btnDownload)
        val btnShare        = findViewById<MaterialButton>(R.id.btnShare)
        val btnHome         = findViewById<MaterialButton>(R.id.btnBackToHome)

        // Ambil data dari Intent
        val invoiceNumber   = intent.getStringExtra("INVOICE_NUMBER") ?: "-"
        val totalPrice      = intent.getStringExtra("TOTAL_PRICE") ?: "0"
        val status          = intent.getStringExtra("STATUS") ?: "-"
        val paymentMethod   = intent.getStringExtra("PAYMENT_METHOD") ?: "-"
        val createdAt       = intent.getStringExtra("CREATED_AT") ?: "-"
        val itemsSummary    = intent.getStringExtra("ITEMS_SUMMARY") ?: "-"

        // Format rupiah
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        val totalFormatted = formatRupiah.format(
            totalPrice.toDoubleOrNull() ?: 0.0
        ).replace("Rp", "Rp ")

        // Format tanggal
        val formattedDate = try {
            val sdfIn = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val sdfOut = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id"))
            val date = sdfIn.parse(createdAt)
            sdfOut.format(date!!)
        } catch (e: Exception) {
            createdAt.substring(0, 10)
        }

        // Set data ke view
        tvInvoice.text      = invoiceNumber
        tvDate.text         = formattedDate
        tvPayment.text      = if (paymentMethod == "belum_dipilih") "Belum Dipilih" else paymentMethod
        tvTotal.text        = totalFormatted
        tvSubtotal.text     = totalFormatted
        tvItems.text        = itemsSummary

        tvStatusBadge.text  = when (status) {
            "pending"   -> "⏳ Menunggu Pembayaran"
            "paid"      -> "✓ Sudah Dibayar"
            "completed" -> "✓ Pesanan Selesai"
            "cancelled" -> "✕ Dibatalkan"
            else        -> status
        }

        tvStatusBadge.setBackgroundColor(
            when (status) {
                "pending"               -> android.graphics.Color.parseColor("#FFF3CD")
                "paid", "completed"     -> android.graphics.Color.parseColor("#D4EDDA")
                "cancelled"             -> android.graphics.Color.parseColor("#FFCCCC")
                else                    -> android.graphics.Color.parseColor("#D4EDDA")
            }
        )

        tvStatusBadge.setTextColor(
            when (status) {
                "pending"               -> android.graphics.Color.parseColor("#856404")
                "paid", "completed"     -> android.graphics.Color.parseColor("#2D5A27")
                "cancelled"             -> android.graphics.Color.parseColor("#CC0000")
                else                    -> android.graphics.Color.parseColor("#2D5A27")
            }
        )

        btnDownload.setOnClickListener { downloadStruk() }
        btnShare.setOnClickListener { shareStruk() }
        btnHome.setOnClickListener {
            val i = Intent(this, HomeActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }
    }

    private fun captureStruk(): Bitmap {
        val bitmap = Bitmap.createBitmap(
            strukContainer.width,
            strukContainer.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        strukContainer.draw(canvas)
        return bitmap
    }

    private fun downloadStruk() {
        val bitmap = captureStruk()
        val filename = "MatchOne_Struk_${System.currentTimeMillis()}.png"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/MatchOne")
            }
            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                contentResolver.openOutputStream(it)?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                }
                Toast.makeText(this,
                    "Struk disimpan ke Galeri/MatchOne!", Toast.LENGTH_SHORT).show()
            }
        } else {
            val dir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES)
            val file = java.io.File(dir, filename)
            java.io.FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            Toast.makeText(this,
                "Struk disimpan!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareStruk() {
        val bitmap = captureStruk()
        val filename = "MatchOne_Share_${System.currentTimeMillis()}.png"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/MatchOne")
        }
        val uri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            contentResolver.openOutputStream(it)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT,
                    "Struk Pembayaran MatchOne\nYour Daily Ritual, Refined. 🍵")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Bagikan Struk via"))
        }
    }
}