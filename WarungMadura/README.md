# 🏪 Warung Madura - Sistem Kasir Digital

Aplikasi kasir berbasis Java Swing untuk Warung, tanpa dependency eksternal.

## ✅ Fitur
- 🔐 Login sistem (admin & kasir)
- 🛒 Kasir interaktif dengan grid produk bergambar
- 🔍 Pencarian & filter produk berdasarkan kategori
- 🧾 Perhitungan otomatis harga, total, dan kembalian
- 💳 Pemilihan metode pembayaran (Tunai, QRIS, Transfer Bank, Kartu Debit)
- 📋 Riwayat transaksi lengkap dengan tanggal & waktu
- 🖨️ Cetak struk (preview & print)
- 📦 CRUD Produk (tambah, edit, hapus + gambar)
- 📅 Jam digital real-time di sidebar

## 🏷️ Produk Default
| Kategori       | Produk                | Harga   |
|----------------|----------------------|---------|
| Makanan Ringan | Roti Aoka             | Rp 2.000|
| Makanan Ringan | Chitato               | Rp 5.000|
| Makanan Ringan | Biskuit Milkuat       | Rp 3.000|
| Minuman        | Aqua                  | Rp 3.000|
| Minuman        | Teh Botol Sosro       | Rp 5.000|
| Minuman        | Ultramilk             | Rp 7.000|
| Sembako        | Indomie               | Rp 4.000|
| Sembako        | Minyak Bimoli 2Liter  | Rp 25.000|
| Sembako        | Gulaku 1Kg            | Rp 18.000|

## 👤 Akun Default
| Username | Password | Role  |
|----------|----------|-------|
| admin    | admin123 | admin |
| kasir1   | kasir123 | kasir |

## 🚀 Cara Menjalankan

### Linux / macOS
```bash
# Pastikan JDK 11+ terinstall
sudo apt install openjdk-21-jdk   # Ubuntu/Debian

# Jalankan
./run.sh
```

### Windows
```
Klik dua kali run.bat
```

### Manual
```bash
mkdir out
javac -d out $(find src -name "*.java")
java -cp out warungmadura.Main
```

## 📁 Struktur Proyek
```
WarungMadura/
├── src/warungmadura/
│   ├── Main.java
│   ├── model/        (Product, CartItem, Transaction, User)
│   ├── controller/   (ProductDAO, TransactionDAO, UserDAO)
│   ├── view/         (LoginFrame, MainFrame, KasirPanel, dll)
│   └── util/         (DatabaseUtil, AppColors, FormatUtil)
├── data/             (dibuat otomatis saat pertama dijalankan)
├── run.sh
├── run.bat
└── README.md
```

## 📝 Catatan
- Data disimpan di folder `data/` sebagai file CSV
- Tidak memerlukan library eksternal apapun
- Memerlukan Java JDK 11 atau lebih baru
- Tampilan menggunakan Java Swing
