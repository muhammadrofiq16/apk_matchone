package com.project.matchone.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.matchone.R
import com.project.matchone.adapter.HistoryAdapter
import com.project.matchone.data.model.HistoryModel

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // 1. Inisialisasi RecyclerView
        val rvHistory = findViewById<RecyclerView>(R.id.recyclerViewHistory)

        // 2. Siapkan data dummy
        val dummyData = listOf(
            HistoryModel("ORD-001", "25 Mei 2026", "Rp 50.000", "Selesai"),
            HistoryModel("ORD-002", "24 Mei 2026", "Rp 120.000", "Diproses"),
            HistoryModel("ORD-003", "23 Mei 2026", "Rp 35.000", "Dibatalkan")
        )

        // 3. Pasang Adapter
        val adapter = HistoryAdapter(dummyData)
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = adapter
    }
}