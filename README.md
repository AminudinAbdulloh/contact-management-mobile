# Contact Management Android App

<div align="center">
  <img src="/app/src/main/res/drawable/contact_management_icon.png" alt="Contact Management Logo" width="120" height="120">

<h3>Modern Contact & Address Management Application</h3>

  <p>
    <a href="#features">Features</a> •
    <a href="#prerequisites">Prerequisites</a> •
    <a href="#installation">Installation</a> •
    <a href="#configuration">Configuration</a> •
    <a href="#backend-api">Backend API</a>
  </p>
</div>

---

## 1. About

Contact Management adalah aplikasi Android modern untuk mengelola kontak dan alamat dengan antarmuka yang intuitif dan desain material yang elegan. Aplikasi ini menyediakan fitur lengkap untuk manajemen kontak termasuk pencarian, pagination, dan manajemen multiple alamat per kontak.

## Features

### 🔐 User Authentication
- ✅ User registration dengan validasi
- ✅ Secure login system
- ✅ Session management otomatis
- ✅ Profile management
- ✅ Change password
- ✅ Auto logout pada token expired

### 👥 Contact Management
- ✅ Create, Read, Update, Delete (CRUD) kontak
- ✅ Search kontak by name, email, atau phone
- ✅ Pagination untuk list kontak
- ✅ Detail informasi kontak
- ✅ Validasi input field
- ✅ Confirmation dialog sebelum delete

### 🏠 Address Management
- ✅ Multiple addresses per contact
- ✅ Complete address fields (street, city, province, country, postal code)
- ✅ Add, edit, dan delete addresses
- ✅ Address list view pada contact detail

### 🎨 Modern UI/UX
- ✅ Material Design components
- ✅ Gradient backgrounds
- ✅ Custom dialogs (loading, success, error, confirmation)
- ✅ Responsive layouts
- ✅ Smooth animations dan transitions
- ✅ User-friendly error messages

## Prerequisites

Sebelum memulai, pastikan Anda telah menginstal:

- **Android Studio** Arctic Fox (2020.3.1) atau lebih baru
- **JDK** 8 atau lebih tinggi
- **Android SDK** dengan minimum API level 24 (Android 7.0)
- **Gradle** 7.0 atau lebih tinggi
- **Backend API** (lihat bagian [Backend API](#backend-api))

## Installation

### 1. Clone Repository

```bash
git clone https://github.com/your-username/contact-management-android.git
cd contact-management-android
```

### 2. Open di Android Studio

1. Launch Android Studio
2. Select **"Open an Existing Project"**
3. Navigate ke direktori yang telah di-clone
4. Click **"OK"**

### 3. Gradle Sync

Android Studio akan otomatis melakukan sync. Tunggu hingga proses selesai (biasanya beberapa menit pada run pertama).

## Configuration

### 🔧 Setup Backend API URL

**PENTING:** Aplikasi ini memerlukan backend API untuk berfungsi. Anda HARUS mengkonfigurasi API base URL sebelum menjalankan aplikasi.

#### Langkah-langkah:

1. **Buat atau edit file `local.properties`**

   Di root directory project, buat atau buka file `local.properties`:

   ```bash
   # Linux/Mac
   touch local.properties
   
   # Windows - buat manual atau via command prompt
   type nul > local.properties
   ```

2. **Tambahkan konfigurasi BASE_API_PATH**

   Buka `local.properties` dan tambahkan:

   ```properties
   BASE_API_PATH=http://your-backend-url/api/
   ```

#### 📌 Contoh Konfigurasi:

**Untuk Development dengan Android Emulator:**
```properties
BASE_API_PATH=http://10.0.2.2:3000/api/
```
> `10.0.2.2` adalah alias untuk `localhost` pada Android Emulator

**Untuk Development dengan Physical Device:**
```properties
BASE_API_PATH=http://192.168.1.100:3000/api/
```
> Ganti `192.168.1.100` dengan IP address komputer Anda di jaringan lokal

**Untuk Production:**
```properties
BASE_API_PATH=https://api.yourdomain.com/api/
```

#### ⚠️ Catatan Penting:

- File `local.properties` sudah ada di `.gitignore` (tidak di-commit ke Git)
- Selalu sertakan trailing slash (`/`) di akhir URL
- Pastikan backend API sudah running sebelum launch app
- Gunakan `http://10.0.2.2` untuk emulator, bukan `localhost`
- Untuk physical device, gunakan IP address lokal komputer Anda

### 🔍 Cara Menemukan IP Address Lokal:

**Windows:**
```cmd
ipconfig
```
Cari "IPv4 Address" pada network adapter yang aktif

**Linux/Mac:**
```bash
ifconfig
```
atau
```bash
ip addr show
```

## Backend API

### Repository Backend

Backend API tersedia di: **[Contact Management API](https://github.com/AminudinAbdulloh/contact-management-api)**

### Setup Backend

1. **Clone repository backend**
   ```bash
   git clone https://github.com/AminudinAbdulloh/contact-management-api.git
   cd contact-management-api
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Setup database**
   - Ikuti instruksi di README backend untuk setup database
   - Configure database connection di `.env`

4. **Run backend server**
   ```bash
   npm run dev
   ```

5. **Verify API berjalan**

   Test endpoint:
   ```bash
   curl http://localhost:3000/api/
   ```

### 📡 API Endpoints

Aplikasi Android menggunakan endpoints berikut:

#### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/users` | Register user baru |
| POST | `/users/login` | Login user |
| GET | `/users/current` | Get user info |
| PATCH | `/users/current` | Update user profile |
| DELETE | `/users/logout` | Logout user |

#### Contacts
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/contacts` | Create contact |
| GET | `/contacts` | Get contacts (with pagination) |
| GET | `/contacts/:id` | Get contact detail |
| PUT | `/contacts/:id` | Update contact |
| DELETE | `/contacts/:id` | Delete contact |

#### Addresses
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/contacts/:contactId/addresses` | Create address |
| GET | `/contacts/:contactId/addresses` | Get addresses |
| GET | `/contacts/:contactId/addresses/:addressId` | Get address detail |
| PUT | `/contacts/:contactId/addresses/:addressId` | Update address |
| DELETE | `/contacts/:contactId/addresses/:addressId` | Delete address |

## 📁 Project Structure

```
app/
├── src/main/
│   ├── java/com/example/contactmanagement/
│   │   ├── adapters/                    # RecyclerView Adapters
│   │   │   ├── AddressAdapter.java
│   │   │   └── ContactAdapter.java
│   │   │
│   │   ├── api/                         # Networking Layer
│   │   │   ├── ApiClient.java           # Retrofit client
│   │   │   ├── ApiService.java          # API endpoints
│   │   │   └── AuthInterceptor.java     # Token interceptor
│   │   │
│   │   ├── models/                      # Data Models
│   │   │   ├── Address.java
│   │   │   ├── Contact.java
│   │   │   ├── User.java
│   │   │   ├── ApiResponse.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── LoginResponse.java
│   │   │   └── ... (other models)
│   │   │
│   │   ├── utils/                       # Utility Classes
│   │   │   ├── DialogHelper.java        # Custom dialogs
│   │   │   └── SharedPrefManager.java   # Session management
│   │   │
│   │   ├── BaseActivity.java            # Base activity
│   │   ├── MyApplication.java           # Application class
│   │   │
│   │   ├── LoginActivity.java
│   │   ├── RegisterActivity.java
│   │   ├── ContactsActivity.java
│   │   ├── ContactDetailActivity.java
│   │   ├── CreateContactActivity.java
│   │   ├── EditContactActivity.java
│   │   ├── AddAddressActivity.java
│   │   ├── EditAddressActivity.java
│   │   └── ProfileActivity.java
│   │
│   ├── res/
│   │   ├── layout/                      # XML Layouts
│   │   ├── drawable/                    # Images & Icons
│   │   ├── values/
│   │   │   ├── colors.xml
│   │   │   ├── strings.xml
│   │   │   ├── dimens.xml
│   │   │   └── styles.xml
│   │   └── ...
│   │
│   └── AndroidManifest.xml
│
└── build.gradle
```

## 🛠 Technologies Used

### Core
- **Language**: Java
- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Architecture**: MVC Pattern

### Libraries
```gradle
// Networking
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'

// UI Components
implementation 'com.google.android.material:material:1.9.0'
implementation 'androidx.cardview:cardview:1.0.0'
implementation 'androidx.recyclerview:recyclerview:1.3.1'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

// JSON Parsing
implementation 'com.google.code.gson:gson:2.10.1'
```

## 📝 How to Use

### 1. Register Account
1. Buka aplikasi
2. Click "Sign Up"
3. Isi form registrasi
4. Click "Sign Up"

### 2. Login
1. Input username dan password
2. Click "Sign In"

### 3. Manage Contacts
- **Add Contact**: Click tombol "Create New Contact"
- **Search Contact**: Click "Search Contacts" dan isi kriteria
- **View Detail**: Click pada contact card
- **Edit Contact**: Click tombol "Edit" pada contact
- **Delete Contact**: Click tombol "Delete" dan konfirmasi

### 4. Manage Addresses
1. Buka contact detail
2. Click "Add Address"
3. Isi form address
4. Click "Save"

### 5. Update Profile
1. Click "Profile" di header
2. Update nama atau password
3. Click "Update"

## 🎨 Design Features

### Color Scheme
- Primary: Blue (#3B82F6)
- Secondary: Purple (#8B5CF6)
- Background: Dark gradient
- Success: Green (#10B981)
- Error: Red (#EF4444)

### UI Components
- Material Design 3
- Custom gradient backgrounds
- Rounded corners (12dp)
- Elevation and shadows
- Custom icons
- Smooth animations

## 🔒 Security Features

- Token-based authentication
- Secure session storage
- Auto logout on token expiry
- Input validation
- Error handling
- HTTPS support (production)

## 🐛 Troubleshooting

### Error: "Failed to connect"
- Pastikan backend API running
- Cek `BASE_API_PATH` di `local.properties`
- Untuk emulator, gunakan `http://10.0.2.2:port`
- Untuk physical device, pastikan satu network dengan backend

### Error: "401 Unauthorized"
- Token sudah expired, login ulang
- Pastikan backend authentication berfungsi

### Gradle Sync Failed
- Pastikan internet connection stabil
- Clean project: `Build > Clean Project`
- Rebuild project: `Build > Rebuild Project`
- Invalidate cache: `File > Invalidate Caches / Restart`

### App Crashes
- Check Logcat untuk error details
- Pastikan semua dependencies terinstall
- Clear app data di device settings

## 🤝 Contributing

Kontribusi selalu welcome! Ikuti langkah berikut:

1. Fork repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

### Coding Standards
- Follow Java naming conventions
- Add comments untuk complex logic
- Maintain consistent code formatting
- Write descriptive commit messages

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Developer

**Aminudin Abdulloh**
- GitHub: [@AminudinAbdulloh](https://github.com/AminudinAbdulloh)
- Backend API: [contact-management-api](https://github.com/AminudinAbdulloh/contact-management-api)

## 📞 Support

Jika Anda mengalami masalah atau memiliki pertanyaan:

- 📧 Email: aminabdulah01@gmail.com
- 🐛 [Create an Issue](https://github.com/your-username/contact-management-android/issues)
- 💬 [Discussions](https://github.com/your-username/contact-management-android/discussions)

## 🙏 Acknowledgments

- Material Design Icons
- Android Developer Documentation
- Retrofit Documentation
- Stack Overflow Community

---

<div align="center">
  <p>Made with ❤️ by Aminudin Abdulloh</p>
  <p>⭐ Star this repo if you find it helpful!</p>
</div>

## 📸 Screenshots

> **Note**: Tambahkan screenshots aplikasi Anda di sini untuk memberikan preview visual kepada developer lain.

### Login & Register
| Login | Register |
|-------|----------|
| ![Login](screenshots/login.png) | ![Register](screenshots/register.png) |

### Contacts Management
| Contacts List | Contact Detail | Create Contact |
|---------------|----------------|----------------|
| ![List](screenshots/contacts.png) | ![Detail](screenshots/detail.png) | ![Create](screenshots/create.png) |

### Address Management
| Add Address | Edit Address |
|-------------|--------------|
| ![Add](screenshots/add-address.png) | ![Edit](screenshots/edit-address.png) |

### Profile
| Profile | Edit Profile |
|---------|--------------|
| ![Profile](screenshots/profile.png) | ![Edit](screenshots/edit-profile.png) |

---

**⚡ Quick Start Checklist:**

- [ ] Clone repository
- [ ] Open di Android Studio
- [ ] Setup `local.properties` dengan `BASE_API_PATH`
- [ ] Clone dan run backend API
- [ ] Sync Gradle
- [ ] Run aplikasi
- [ ] Register account baru
- [ ] Mulai manage contacts! 🎉