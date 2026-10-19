# 🏦 Mobile-Based SACCO Management Android App

An Android application designed for **Savings and Credit Co-operative Societies (SACCOs)**. It empowers members to manage savings, apply for loans, buy share capital, and make instant deposits via **Safaricom M-Pesa STK Push**. Built with modern Android architecture, Firebase, Room DB, and Google Sign-In.

---

## ✨ Key Features

### 👤 Member Portal
* **🔐 2-Factor Authentication**: Email/Password + SHA-256 Hashed 4-Digit Transaction PIN verification.
* **🌐 Google Sign-In**: OAuth 2.0 onboarding using Google Accounts and Firebase Credentials.
* **📱 M-Pesa STK Push Integration**: Direct Safaricom Daraja API STK Push prompts for automated savings deposits and loan repayments.
* **💰 Savings Account**: Real-time balance tracking, deposit history, and monthly contribution stats.
* **📝 Loan Management**: Loan applications with auto-calculated qualification limits (3x Savings multiplier rule) and loan repayment tracking.
* **📈 Share Capital**: Track SACCO share capital and minimum share requirements.
* **📋 Transaction History**: Real-time activity logs with filtering.

### 🛡️ Administrator Portal
* **👥 Member Approvals**: Review and approve/reject pending member registrations.
* **💳 Loan Approvals & Disbursement**: Approve or decline member loan applications.
* **📊 Visual Financial Reports**: Interactive bar charts (MPAndroidChart) showing total savings vs. active loan breakdown.
* **📢 Announcements**: Publish SACCO-wide notifications to all members.

---

## 🛠️ Tech Stack & Libraries

* **Language**: Java & Kotlin (Gradle KTS)
* **Architecture**: MVVM / Repository Pattern / ViewBinding
* **Database & Backend**:
  * **Firebase Authentication** (Email/Password & Google Sign-In)
  * **Firebase Cloud Firestore** (User Profiles & Security PIN Hashing)
  * **Room Persistence Library** (Offline-first Local SQLite Caching)
* **Networking**:
  * **Retrofit 2** + **Gson Converter** (M-Pesa Daraja REST API)
  * **OkHttp 4** (Logging Interceptors)
* **UI & Data Visualization**:
  * **Material Design 3** Components
  * **MPAndroidChart** (Financial breakdown charts)
  * **Glide** (Image loading)

---

## 🚀 Getting Started

### Prerequisites
* **Android Studio** (2024.1 or newer)
* **JDK 11** or higher
* Android Device or Emulator running **Android 7.0 (API 24+)** with **Google Play Services**

### Setup Instructions
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/winstonekelly18-glitch/-mpesa-sacco-management-system.git
   cd -mpesa-sacco-management-system
   ```
2. **Firebase Setup**:
   * Add your `google-services.json` configuration file into the `app/` folder.
   * Enable **Email/Password** and **Google Sign-In** in the Firebase Auth console.
3. **Build & Run**:
   * Sync Gradle project in Android Studio.
   * Select an emulator or connected device and click **Run (Shift + F10)**.

---

## 🔐 Security Standards
* Security PINs are hashed using **SHA-256 with Salt** before storing in Firestore.
* Asynchronous database operations offloaded from the UI main thread using `Executors`.

---

## 🤝 Authors & Credits
Designed and Developed by **Team Omega**.
