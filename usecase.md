# Skenario Demo Project Financial Tracker

Dokumen ini berisi panduan langkah demi langkah untuk mendemokan aplikasi Financial Tracker saat presentasi. Skenario ini mencakup alur Admin (pengelolaan kategori) dan alur User (transaksi keuangan).

## Persiapan

1.  Pastikan **MySQL Database** berjalan.
2.  Pastikan **Redis Server** berjalan.
3.  Jalankan aplikasi Spring Boot:
    ```bash
    ./mvnw spring-boot:run
    ```
4.  Siapkan **Postman** atau tools sejenis untuk testing API.

---

## Skenario 1: Admin Flow (Setup Awal)

_Tujuan: Menunjukkan fitur Role-Based Access Control dan Management Data Master._

### 1. Admin Login

- **Endpoint**: `POST /auth/login`
- **Body**:
  ```json
  {
    "username": "admin",
    "password": "adminPassword"
  }
  ```
- **Hasil**: Copy **Access Token** dari response. Gunakan token ini untuk langkah selanjutnya (Auth Type: Bearer Token).

### 2. Create Categories (Redis Caching Demo)

- **Endpoint**: `POST /api/categories`
- **Header**: `Authorization: Bearer <ADMIN_TOKEN>`
- **Body**:
  ```json
  {
    "name": "Gaji",
    "type": "INCOME",
    "description": "Pendapatan bulanan"
  }
  ```
- _Ulangi untuk kategori lain_: "Makanan" (EXPENSE), "Transport" (EXPENSE).
- **Poin Demo**: Jelaskan bahwa kategori ini disimpan di database dan akan di-cache.

### 3. View All Categories (Cache Hit)

- **Endpoint**: `GET /api/categories`
- **Header**: `Authorization: Bearer <ADMIN_TOKEN>`
- **Aksi**:
  1.  Request pertama -> Data diambil dari DB (Perhatikan log console jika ada logging).
  2.  Request kedua -> Data diambil dari Redis (Response lebih cepat).

### 4. View Users (Admin Only)

- **Endpoint**: `GET /api/users`
- **Header**: `Authorization: Bearer <ADMIN_TOKEN>`
- **Hasil**: Menampilkan daftar semua user terdaftar.

---

## Skenario 2: User Journey (Fitur Utama)

_Tujuan: Mendemokan alur penggunaan aplikasi oleh user biasa._

### 1. Register New User

- **Endpoint**: `POST /auth/register`
- **Body**:
  ```json
  {
    "username": "user_demo",
    "email": "demo@example.com",
    "password": "password123",
    "role": "USER"
  }
  ```

### 2. User Login

- **Endpoint**: `POST /auth/login`
- **Body**: Gunakan kredensial user baru.
- **Hasil**: Copy **Access Token** user baru.

### 3. Add Income (Pemasukan)

- **Endpoint**: `POST /api/transactions`
- **Header**: `Authorization: Bearer <USER_TOKEN>`
- **Body**:
  ```json
  {
    "amount": 5000000,
    "type": "INCOME",
    "description": "Gaji Bulan Januari",
    "categoryId": 1,
    "date": "2024-01-25"
  }
  ```
  _(Pastikan categoryId sesuai dengan ID kategori "Gaji" yang dibuat Admin)_

### 4. Add Expense (Pengeluaran)

- **Endpoint**: `POST /api/transactions`
- **Header**: `Authorization: Bearer <USER_TOKEN>`
- **Body**:
  ```json
  {
    "amount": 50000,
    "type": "EXPENSE",
    "description": "Makan Siang",
    "categoryId": 2,
    "date": "2024-01-26"
  }
  ```

### 5. View Summary (Rekap Keuangan)

- **Endpoint**: `GET /api/summary`
- **Header**: `Authorization: Bearer <USER_TOKEN>`
- **Hasil**:
  - **Total Income**: 5.000.000
  - **Total Expense**: 50.000
  - **Balance**: 4.950.000
- **Poin Demo**: Menunjukkan otomatisasi perhitungan saldo.

### 6. Transaction History

- **Endpoint**: `GET /api/transactions`
- **Header**: `Authorization: Bearer <USER_TOKEN>`
- **Hasil**: List transaksi yang baru dibuat.

---

## Skenario 3: Keamanan & Error Handling

_Tujuan: Menunjukkan robust-ness aplikasi._

### 1. Akses Tanpa Token

- Coba akses `GET /api/users` tanpa Header Authorization.
- **Hasil**: `401 Unauthorized`.

### 2. Akses Terlarang (Forbidden)

- Gunakan **Token User Biasa** untuk akses endpoint Admin `GET /api/users`.
- **Hasil**: `403 Forbidden` (User tidak boleh lihat list semua user).

### 3. Refresh Token

- **Endpoint**: `POST /auth/refresh`
- **Body**:
  ```json
  {
    "refreshToken": "<REFRESH_TOKEN_DARI_LOGIN>"
  }
  ```
- **Hasil**: Mendapatkan Access Token baru.
