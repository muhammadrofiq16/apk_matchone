package com.project.matchone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.matchone.R
import com.project.matchone.data.model.HistoryModel

class HistoryAdapter(private val historyList: List<HistoryModel>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // ID di bawah ini sudah disamakan persis dengan isi item_history.xml kamu
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvTotalAmount: TextView = view.findViewById(R.id.tvTotalAmount)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = historyList[position]

        holder.tvOrderId.text = item.orderId
        holder.tvDate.text = item.date
        holder.tvTotalAmount.text = item.totalAmount
        holder.tvStatus.text = item.status

        // Logika warna status agar lebih keren
        when (item.status) {
            "Selesai" -> holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            "Dibatalkan" -> holder.tvStatus.setTextColor(android.graphics.Color.RED)
            "Diproses" -> holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
        }
    }

    override fun getItemCount(): Int = historyList.size
}