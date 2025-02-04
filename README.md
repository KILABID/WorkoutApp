# WorkoutApp

## English Version

WorkoutApp is a fitness application that uses pose estimation to detect whether the user's workout pose is correct or incorrect. The app provides voice notifications when the pose is incorrect and counts repetitions when the pose is correct.

### Main Features

- **Pose Estimation**: Uses MediaPipe to detect user poses in real-time.
- **Voice Notifications**: Alerts the user if the pose is incorrect.
- **Repetition Counter**: Tracks movements like push-ups, sit-ups, and squats based on joint angle detection.
- **Simple & Intuitive UI**: Easy-to-use interface for better user experience.

### How to Run the Project

1. **Clone Repository**:
   ```sh
   git clone https://github.com/KILABID/WorkoutApp.git
   ```
2. **Open in Android Studio**
3. **Sync Gradle**
4. **Run the Application**
   - Use an emulator or a physical device with an active front camera.

### Firebase Authentication Setup

To enable Firebase Authentication in your project, follow these steps:

1. **Set Up Firebase Project**:

   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project and register your app (Android package name must match your project)
   - Download the `google-services.json` file and place it in the `app/` directory

### Technologies Used

- **Kotlin** for Android development
- **Jetpack Compose** for UI
- **MediaPipe** for pose estimation
- **Firebase Authentication** for user authentication
- **Room Database** (if data storage is included)

### Main Dependencies

Make sure to check the `build.gradle.kts` file for dependencies used, especially:

```kotlin
implementation("com.google.mediapipe:solution-core:<version>")
implementation("com.google.firebase:firebase-auth-ktx:<latest_version>")
```

### Contribution

If you want to contribute, please submit a pull request or report issues through the Issues section.


---

Happy training with WorkoutApp! 💪

## Versi Indonesia

WorkoutApp adalah aplikasi olahraga yang menggunakan fitur pose estimation untuk mendeteksi apakah pose workout pengguna sudah benar atau salah. Aplikasi ini memberikan notifikasi suara ketika pose salah dan menghitung jumlah repetisi ketika pose benar.

### Fitur Utama

- **Pose Estimation**: Menggunakan MediaPipe untuk mendeteksi pose pengguna secara real-time.
- **Notifikasi Suara**: Memberi tahu pengguna jika pose tidak sesuai.
- **Penghitungan Repetisi**: Menghitung jumlah gerakan seperti push-up, sit-up, dan squat berdasarkan deteksi sudut sendi.
- **UI Sederhana & Intuitif**: Tampilan yang mudah digunakan untuk pengalaman pengguna yang lebih baik.

### Cara Menjalankan Proyek

1. **Clone Repository**:
   ```sh
   git clone https://github.com/KILABID/WorkoutApp.git
   ```
2. **Buka di Android Studio**
3. **Sinkronisasi Gradle**
4. **Jalankan Aplikasi**
   - Gunakan emulator atau perangkat fisik dengan kamera depan aktif.

### Setup Firebase Authentication

Untuk mengaktifkan Firebase Authentication di proyek Anda, ikuti langkah berikut:

1. **Siapkan Proyek Firebase**:

   - Buka [Firebase Console](https://console.firebase.google.com/)
   - Buat proyek baru dan daftarkan aplikasi (sesuaikan package name)
   - Unduh file `google-services.json` dan letakkan di folder `app/`

### Teknologi yang Digunakan

- **Kotlin** untuk pengembangan aplikasi Android
- **Jetpack Compose** untuk UI
- **MediaPipe** untuk pose estimation
- **Firebase Authentication** untuk autentikasi pengguna
- **Room Database** (jika ada penyimpanan data)

### Dependencies Utama

Pastikan untuk memeriksa file `build.gradle.kts` untuk dependensi yang digunakan, terutama:

```kotlin
implementation("com.google.mediapipe:solution-core:<version>")
implementation("com.google.firebase:firebase-auth-ktx:<latest_version>")
```

### Kontribusi

Jika ingin berkontribusi, silakan buat pull request atau laporkan masalah melalui Issues.

---

Selamat berlatih dengan WorkoutApp! 💪

