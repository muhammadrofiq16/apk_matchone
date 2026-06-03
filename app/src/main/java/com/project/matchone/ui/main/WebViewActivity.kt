package com.project.matchone.ui.main

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.project.matchone.R

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val title   = intent.getStringExtra("TITLE") ?: "MatchOne"
        val url     = intent.getStringExtra("URL") ?: ""

        val tvTitle = findViewById<TextView>(R.id.tvWebTitle)
        val btnBack = findViewById<ImageButton>(R.id.btnWebBack)
        val webView = findViewById<WebView>(R.id.webView)

        tvTitle.text = title
        btnBack.setOnClickListener { finish() }

        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = false

        // Cek apakah URL punya konten lokal, kalau iya load konten langsung
        val localContent = getLocalContent(url)
        if (localContent != null) {
            webView.loadDataWithBaseURL(null, wrapHtml(title, localContent), "text/html", "UTF-8", null)
        } else {
            webView.loadUrl(url)
        }
    }

    private fun getLocalContent(url: String): String? = when {
        url.contains("privacy") -> """
            <h2>Kebijakan Privasi MatchOne</h2>
            <p><i>Terakhir diperbarui: Juni 2025</i></p>

            <p>MatchOne berkomitmen melindungi privasi setiap pengguna. Kebijakan ini menjelaskan bagaimana kami mengumpulkan, menggunakan, dan menjaga data Anda.</p>

            <h3>1. Data yang Kami Kumpulkan</h3>
            <p>Kami mengumpulkan data berikut saat Anda mendaftar dan menggunakan layanan:</p>
            <ul>
                <li>Nama lengkap</li>
                <li>Alamat email</li>
                <li>Nomor telepon</li>
                <li>Riwayat pesanan</li>
            </ul>

            <h3>2. Penggunaan Data</h3>
            <p>Data Anda digunakan untuk memproses pesanan, mengirim notifikasi terkait pesanan, dan meningkatkan kualitas layanan MatchOne. Kami tidak menjual atau membagikan data Anda kepada pihak ketiga untuk tujuan komersial.</p>

            <h3>3. Keamanan Data</h3>
            <p>Seluruh data disimpan dengan enkripsi dan hanya dapat diakses oleh sistem kami yang terotorisasi. Kami menerapkan standar keamanan industri untuk melindungi informasi Anda.</p>

            <h3>4. Hak Pengguna</h3>
            <p>Anda berhak mengakses, mengubah, atau menghapus data pribadi Anda kapan saja melalui halaman Profil atau dengan menghubungi kami.</p>

            <h3>5. Cookies & Pelacakan</h3>
            <p>Aplikasi MatchOne tidak menggunakan cookies pihak ketiga untuk melacak aktivitas Anda di luar aplikasi.</p>

            <h3>6. Perubahan Kebijakan</h3>
            <p>Kami dapat memperbarui kebijakan ini sewaktu-waktu. Perubahan signifikan akan diberitahukan melalui notifikasi di aplikasi.</p>

            <h3>7. Hubungi Kami</h3>
            <p>Pertanyaan seputar privasi dapat dikirim ke:<br><b>support@matchone.id</b></p>
        """.trimIndent()

        url.contains("terms") -> """
            <h2>Ketentuan Layanan MatchOne</h2>
            <p><i>Terakhir diperbarui: Juni 2025</i></p>

            <p>Dengan menggunakan aplikasi MatchOne, Anda menyatakan telah membaca, memahami, dan menyetujui seluruh ketentuan berikut.</p>

            <h3>1. Akun Pengguna</h3>
            <p>Anda bertanggung jawab penuh atas kerahasiaan akun dan kata sandi. Segala aktivitas yang terjadi di bawah akun Anda menjadi tanggung jawab Anda sepenuhnya.</p>

            <h3>2. Pemesanan & Pembayaran</h3>
            <p>Pesanan yang telah dikonfirmasi dan dibayar tidak dapat dibatalkan kecuali dalam kondisi tertentu sesuai kebijakan pembatalan kami. Semua transaksi diproses dengan aman.</p>

            <h3>3. Harga & Ketersediaan Produk</h3>
            <p>Harga produk dapat berubah sewaktu-waktu. MatchOne berhak menolak pesanan jika produk tidak tersedia atau terjadi kesalahan pada harga yang tertera.</p>

            <h3>4. Pengiriman</h3>
            <p>Estimasi waktu pengiriman bersifat perkiraan dan dapat berubah tergantung kondisi. MatchOne tidak bertanggung jawab atas keterlambatan yang disebabkan oleh faktor di luar kendali kami.</p>

            <h3>5. Larangan Penggunaan</h3>
            <p>Pengguna dilarang:</p>
            <ul>
                <li>Menggunakan platform untuk kegiatan ilegal atau penipuan</li>
                <li>Membuat akun palsu atau menyamar sebagai orang lain</li>
                <li>Melakukan tindakan yang merugikan pengguna lain</li>
            </ul>

            <h3>6. Penghentian Layanan</h3>
            <p>MatchOne berhak menangguhkan atau menghapus akun yang melanggar ketentuan ini tanpa pemberitahuan sebelumnya.</p>

            <h3>7. Perubahan Ketentuan</h3>
            <p>Kami berhak mengubah ketentuan ini kapan saja. Penggunaan layanan setelah perubahan dianggap sebagai persetujuan atas ketentuan yang baru.</p>

            <h3>8. Hubungi Kami</h3>
            <p>Pertanyaan dapat dikirim ke:<br><b>support@matchone.id</b></p>
        """.trimIndent()

        url.contains("help") -> """
            <h2>Pusat Bantuan MatchOne</h2>

            <h3>Cara Memesan</h3>
            <p>1. Buka menu <b>Katalog</b><br>
            2. Pilih produk yang diinginkan<br>
            3. Tambahkan ke keranjang<br>
            4. Buka keranjang dan klik <b>Checkout</b><br>
            5. Pilih metode pembayaran<br>
            6. Konfirmasi pesanan</p>

            <h3>Melihat Riwayat Pesanan</h3>
            <p>Buka menu <b>Profil → Riwayat Pesanan</b> untuk melihat status semua pesanan Anda.</p>

            <h3>Lupa Password</h3>
            <p>Hubungi kami di <b>support@matchone.id</b> untuk proses reset password.</p>

            <h3>Kontak Dukungan</h3>
            <p>Email: <b>support@matchone.id</b><br>
            Jam operasional: Senin–Jumat, 09.00–17.00 WIB</p>
        """.trimIndent()

        url.contains("about") -> """
            <div style="text-align:center; padding: 20px 0;">
                <p style="font-size:48px; margin:0;">☕</p>
                <h2 style="color:#2D5A27;">MatchOne</h2>
                <p style="color:#888; margin-top:0;">Versi 1.0.0</p>
            </div>

            <p>MatchOne adalah aplikasi pemesanan minuman premium yang menghadirkan pengalaman belanja yang mudah, cepat, dan menyenangkan.</p>
            <p style="text-align:center;"><i>"Your Daily Ritual, Refined."</i></p>

            <h3>Tim Kami</h3>
            <p>Dikembangkan dengan ❤️ oleh tim MatchOne untuk menghadirkan matcha terbaik ke tangan Anda.</p>

            <h3>Kontak</h3>
            <p>Email: <b>support@matchone.id</b></p>
        """.trimIndent()

        else -> null
    }

    private fun wrapHtml(title: String, body: String): String = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>$title</title>
            <style>
                body {
                    font-family: sans-serif;
                    color: #333;
                    padding: 16px;
                    line-height: 1.7;
                    font-size: 15px;
                }
                h2 { color: #2D5A27; font-size: 20px; margin-bottom: 4px; }
                h3 { color: #2D5A27; font-size: 16px; margin-top: 20px; margin-bottom: 6px; }
                p  { margin: 8px 0; }
                ul { padding-left: 20px; }
                li { margin-bottom: 4px; }
                i  { color: #888; font-size: 13px; }
            </style>
        </head>
        <body>$body</body>
        </html>
    """.trimIndent()
}