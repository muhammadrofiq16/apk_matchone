package com.project.matchone.ui.checkout

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.project.matchone.R
import com.project.matchone.data.network.ApiClient
import com.project.matchone.utils.SessionManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.Locale

class PaymentActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var tvTotalPayment: TextView
    private lateinit var btnConfirmPayment: MaterialButton
    private lateinit var btnSelectProof: MaterialButton
    private lateinit var ivProofPreview: ImageView

    // Payment method groups
    private lateinit var cardTransfer: LinearLayout
    private lateinit var cardEwallet: LinearLayout
    private lateinit var cardCod: LinearLayout

    // Radio buttons
    private lateinit var radioTransfer: View
    private lateinit var radioEwallet: View
    private lateinit var radioCod: View

    // Sub-option containers
    private lateinit var transferSubOptions: LinearLayout
    private lateinit var ewalletSubOptions: LinearLayout
    private lateinit var dividerTransfer: View
    private lateinit var dividerEwallet: View

    // Transfer sub-options
    private lateinit var optionBca: LinearLayout
    private lateinit var optionMandiri: LinearLayout
    private lateinit var radioBca: View
    private lateinit var radioMandiri: View

    // E-Wallet sub-options
    private lateinit var optionGopay: LinearLayout
    private lateinit var optionShopeepay: LinearLayout
    private lateinit var optionDana: LinearLayout
    private lateinit var radioGopay: View
    private lateinit var radioShopeepay: View
    private lateinit var radioDana: View

    // Proof section
    private lateinit var proofSection: LinearLayout

    private lateinit var sessionManager: SessionManager

    private var selectedPaymentMethod = ""
    private var selectedSubMethod = ""
    private var selectedImageUri: Uri? = null
    private var orderId = 0
    private var totalAmount = 0.0

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                ivProofPreview.setImageURI(uri)
                ivProofPreview.visibility = View.VISIBLE
                validateButton()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        sessionManager = SessionManager(this)

        orderId = intent.getIntExtra("EXTRA_ORDER_ID", 0)
        totalAmount = intent.getDoubleExtra("EXTRA_TOTAL_AMOUNT", 0.0)
        selectedPaymentMethod = intent.getStringExtra("EXTRA_PAYMENT_METHOD") ?: ""

        initViews()
        setupData()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvTotalPayment = findViewById(R.id.tvTotalPayment)
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment)
        btnSelectProof = findViewById(R.id.btnSelectProof)
        ivProofPreview = findViewById(R.id.ivProofPreview)

        // Payment method groups
        cardTransfer = findViewById(R.id.cardTransfer)
        cardEwallet = findViewById(R.id.cardEwallet)
        cardCod = findViewById(R.id.cardCod)

        // Radio views
        radioTransfer = findViewById(R.id.radioTransfer)
        radioEwallet = findViewById(R.id.radioEwallet)
        radioCod = findViewById(R.id.radioCod)

        // Sub-option containers
        transferSubOptions = findViewById(R.id.transferSubOptions)
        ewalletSubOptions = findViewById(R.id.ewalletSubOptions)
        dividerTransfer = findViewById(R.id.dividerTransfer)
        dividerEwallet = findViewById(R.id.dividerEwallet)

        // Transfer sub-options
        optionBca = findViewById(R.id.optionBca)
        optionMandiri = findViewById(R.id.optionMandiri)
        radioBca = findViewById(R.id.radioBca)
        radioMandiri = findViewById(R.id.radioMandiri)

        // E-Wallet sub-options
        optionGopay = findViewById(R.id.optionGopay)
        optionShopeepay = findViewById(R.id.optionShopeepay)
        optionDana = findViewById(R.id.optionDana)
        radioGopay = findViewById(R.id.radioGopay)
        radioShopeepay = findViewById(R.id.radioShopeepay)
        radioDana = findViewById(R.id.radioDana)

        // Proof section
        proofSection = findViewById(R.id.proofSection)
    }

    private fun setupData() {
        val localeID = Locale("in", "ID")
        val formatter = NumberFormat.getCurrencyInstance(localeID)
        tvTotalPayment.text = formatter.format(totalAmount).replace("Rp", "Rp ")

        validateButton()
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }

        // === Main payment method selection ===
        cardTransfer.setOnClickListener {
            selectMainMethod("transfer_bank")
        }

        cardEwallet.setOnClickListener {
            selectMainMethod("e_wallet")
        }

        cardCod.setOnClickListener {
            selectMainMethod("cod")
        }

        // === Transfer sub-options ===
        optionBca.setOnClickListener {
            selectSubMethod("bca")
        }

        optionMandiri.setOnClickListener {
            selectSubMethod("mandiri")
        }

        // === E-Wallet sub-options ===
        optionGopay.setOnClickListener {
            selectSubMethod("gopay")
        }

        optionShopeepay.setOnClickListener {
            selectSubMethod("shopeepay")
        }

        optionDana.setOnClickListener {
            selectSubMethod("dana")
        }

        // === Proof selection ===
        btnSelectProof.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // === Confirm payment ===
        btnConfirmPayment.setOnClickListener {
            uploadPayment()
        }
    }

    private fun selectMainMethod(method: String) {
        selectedPaymentMethod = method
        selectedSubMethod = ""

        // Reset all radio buttons
        resetAllRadios()

        // Collapse all sub-options
        transferSubOptions.visibility = View.GONE
        ewalletSubOptions.visibility = View.GONE
        dividerTransfer.visibility = View.GONE
        dividerEwallet.visibility = View.GONE

        when (method) {
            "transfer_bank" -> {
                radioTransfer.setBackgroundResource(R.drawable.bg_radio_selected)
                transferSubOptions.visibility = View.VISIBLE
                dividerTransfer.visibility = View.VISIBLE
                // Show proof section for bank transfer
                proofSection.visibility = View.VISIBLE
            }
            "e_wallet" -> {
                radioEwallet.setBackgroundResource(R.drawable.bg_radio_selected)
                ewalletSubOptions.visibility = View.VISIBLE
                dividerEwallet.visibility = View.VISIBLE
                // Show proof section for e-wallet
                proofSection.visibility = View.VISIBLE
            }
            "cod" -> {
                radioCod.setBackgroundResource(R.drawable.bg_radio_selected)
                // COD: no proof needed, hide proof section
                proofSection.visibility = View.GONE
                selectedImageUri = null // reset any selected proof
            }
        }

        validateButton()
    }

    private fun selectSubMethod(subMethod: String) {
        selectedSubMethod = subMethod

        // Reset all sub-radio buttons
        radioBca.setBackgroundResource(R.drawable.bg_radio_unselected)
        radioMandiri.setBackgroundResource(R.drawable.bg_radio_unselected)
        radioGopay.setBackgroundResource(R.drawable.bg_radio_unselected)
        radioShopeepay.setBackgroundResource(R.drawable.bg_radio_unselected)
        radioDana.setBackgroundResource(R.drawable.bg_radio_unselected)

        // Set selected sub-radio
        when (subMethod) {
            "bca" -> radioBca.setBackgroundResource(R.drawable.bg_radio_selected)
            "mandiri" -> radioMandiri.setBackgroundResource(R.drawable.bg_radio_selected)
            "gopay" -> radioGopay.setBackgroundResource(R.drawable.bg_radio_selected)
            "shopeepay" -> radioShopeepay.setBackgroundResource(R.drawable.bg_radio_selected)
            "dana" -> radioDana.setBackgroundResource(R.drawable.bg_radio_selected)
        }

        validateButton()
    }

    private fun resetAllRadios() {
        radioTransfer.setBackgroundResource(R.drawable.bg_radio_unselected)
        radioEwallet.setBackgroundResource(R.drawable.bg_radio_unselected)
        radioCod.setBackgroundResource(R.drawable.bg_radio_unselected)

        // Reset sub-radios too
        radioBca.setBackgroundResource(R.drawable.bg_radio_unselected)
        radioMandiri.setBackgroundResource(R.drawable.bg_radio_unselected)
        radioGopay.setBackgroundResource(R.drawable.bg_radio_unselected)
        radioShopeepay.setBackgroundResource(R.drawable.bg_radio_unselected)
        radioDana.setBackgroundResource(R.drawable.bg_radio_unselected)
    }

    private fun getPaymentTypeForBackend(): String {
        return when (selectedPaymentMethod) {
            "e_wallet" -> "e_wallet"
            "transfer_bank" -> "bank_transfer"
            "cod" -> "cash_on_delivery"
            else -> "bank_transfer"
        }
    }

    private fun validateButton() {
        val isCod = selectedPaymentMethod == "cod"
        val hasMethod = selectedPaymentMethod.isNotEmpty()
        val hasSubMethod = when (selectedPaymentMethod) {
            "transfer_bank", "e_wallet" -> selectedSubMethod.isNotEmpty()
            "cod" -> true
            else -> false
        }
        val hasProof = isCod || selectedImageUri != null

        val isValid = orderId > 0 &&
                totalAmount > 0 &&
                hasMethod &&
                hasSubMethod &&
                hasProof

        btnConfirmPayment.isEnabled = isValid
        btnConfirmPayment.alpha = if (isValid) 1f else 0.5f
    }

    private fun uploadPayment() {
        val token = sessionManager.fetchAuthToken()

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan, silakan login ulang", Toast.LENGTH_SHORT).show()
            return
        }

        if (orderId == 0) {
            Toast.makeText(this, "Order ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedPaymentMethod.isEmpty()) {
            Toast.makeText(this, "Pilih metode pembayaran dulu", Toast.LENGTH_SHORT).show()
            return
        }

        // For COD, no proof needed — submit directly
        if (selectedPaymentMethod == "cod") {
            submitCodPayment(token)
            return
        }

        val imageUri = selectedImageUri
        if (imageUri == null) {
            Toast.makeText(this, "Pilih bukti pembayaran dulu", Toast.LENGTH_SHORT).show()
            return
        }

        val imageFile = uriToFile(imageUri)

        val orderIdBody = orderId.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())

        val paymentTypeBody = getPaymentTypeForBackend()
            .toRequestBody("text/plain".toMediaTypeOrNull())

        val amountPaidBody = totalAmount.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())

        val imageRequestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())

        val imagePart = MultipartBody.Part.createFormData(
            "payment_proof",
            imageFile.name,
            imageRequestBody
        )

        btnConfirmPayment.isEnabled = false
        btnConfirmPayment.text = "Mengupload..."

        ApiClient.instance.uploadPayment(
            token = "Bearer $token",
            orderId = orderIdBody,
            paymentType = paymentTypeBody,
            amountPaid = amountPaidBody,
            paymentProof = imagePart
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                btnConfirmPayment.isEnabled = true
                btnConfirmPayment.text = "Konfirmasi Pesanan"

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@PaymentActivity,
                        "Bukti pembayaran berhasil dikirim. Menunggu konfirmasi kasir.",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                } else {
                    Toast.makeText(
                        this@PaymentActivity,
                        "Upload gagal: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                btnConfirmPayment.isEnabled = true
                btnConfirmPayment.text = "Konfirmasi Pesanan"

                Toast.makeText(
                    this@PaymentActivity,
                    "Gagal upload: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun submitCodPayment(token: String) {
        btnConfirmPayment.isEnabled = false
        btnConfirmPayment.text = "Memproses..."

        val orderIdBody = orderId.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())

        val paymentTypeBody = "cash_on_delivery"
            .toRequestBody("text/plain".toMediaTypeOrNull())

        val amountPaidBody = totalAmount.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())

        // For COD, send empty/dummy proof
        val emptyBytes = ByteArray(0)
        val emptyBody = okhttp3.RequestBody.create("image/*".toMediaTypeOrNull(), emptyBytes)
        val emptyPart = MultipartBody.Part.createFormData("payment_proof", "", emptyBody)

        ApiClient.instance.uploadPayment(
            token = "Bearer $token",
            orderId = orderIdBody,
            paymentType = paymentTypeBody,
            amountPaid = amountPaidBody,
            paymentProof = emptyPart
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                btnConfirmPayment.isEnabled = true
                btnConfirmPayment.text = "Konfirmasi Pesanan"

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@PaymentActivity,
                        "Pesanan COD berhasil! Silakan bayar di kasir.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@PaymentActivity,
                        "Gagal: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                btnConfirmPayment.isEnabled = true
                btnConfirmPayment.text = "Konfirmasi Pesanan"

                Toast.makeText(
                    this@PaymentActivity,
                    "Gagal: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun uriToFile(uri: Uri): File {
        val fileName = getFileName(uri)
        val file = File(cacheDir, fileName)

        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return file
    }

    private fun getFileName(uri: Uri): String {
        var result = "payment_proof_${System.currentTimeMillis()}.jpg"

        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index >= 0) {
                        result = it.getString(index)
                    }
                }
            }
        }

        return result
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.close_enter, R.anim.close_exit)
    }
}