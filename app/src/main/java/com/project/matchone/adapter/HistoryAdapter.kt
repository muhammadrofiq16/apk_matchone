package com.project.matchone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.matchone.R
import com.project.matchone.data.model.TransactionModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class HistoryAdapter(
    private var transactions: List<TransactionModel>,
    private val onItemClick: (TransactionModel) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvInvoiceNumber: TextView = itemView.findViewById(R.id.tvInvoiceNumber)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvTotalPrice: TextView = itemView.findViewById(R.id.tvTotalPrice)
        val tvPaymentMethod: TextView = itemView.findViewById(R.id.tvPaymentMethod)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)

        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.tvInvoiceNumber.text = transaction.getDisplayInvoice()
        holder.tvStatus.text = transaction.getDisplayStatus()
        holder.tvDate.text = formatDate(transaction.createdAt)
        holder.tvTotalPrice.text = formatCurrency(transaction.getDisplayTotal())
        holder.tvPaymentMethod.text = formatPaymentMethod(transaction.paymentMethod)

        holder.itemView.setOnClickListener {
            onItemClick(transaction)
        }
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun updateData(newTransactions: List<TransactionModel>) {
        transactions = newTransactions
        notifyDataSetChanged()
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
}