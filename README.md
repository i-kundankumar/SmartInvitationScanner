# 📱 Smart Invitation Scanner

Smart Invitation Scanner is a modern Android application that helps event organizers create events, generate digital invitations with QR codes, and manage guest entry by scanning and verifying invitations in real time. The app is built with Firebase and Material Design, focusing on performance, security, and a clean user experience.

---

## 🚀 Features

### 🔐 Authentication
- Email & Password signup/login using Firebase Auth  
- Email verification before access  
- Secure logout  
- Input validation & error handling  

### 👤 Profile Management
- Fetch and display user profile from Firestore  
- Read-only profile view with edit mode  
- Update profile info (name, phone, avatar ready)  
- Sign out from profile screen  

### 🧭 Navigation
- Bottom navigation with multiple fragments  
- Floating Action Button for quick actions (scanner)  
- Smooth fragment switching  

### 📅 Event Creation
- Modern "Create Event" UI with Material Components  
- Cover photo picker (UI ready)  
- Event title, organizer, type, date & time, location, pricing, and description  
- Material DatePicker & TimePicker integration  
- Saves event date as Firestore `Timestamp`  

### 📷 QR Scanner (Planned)
- Scan QR codes at entry gate  
- Validate invitations against Firestore  
- Prevent duplicate entries  
- Track guest check-ins in real time  

### 🌙 Theming
- Light & Dark mode support  
- Adaptive colors using Material theming  

---

## 🛠 Tech Stack

- **Language:** Java  
- **UI:** AndroidX, Material Components (Material 3 styling)  
- **Architecture:** Activity + Fragments  
- **Backend:** Firebase  
  - Firebase Authentication  
  - Cloud Firestore  
  - (Planned) Firebase Storage  
  - (Planned) Cloud Functions  
- **Design:** ConstraintLayout, MaterialCardView, TextInputLayout  
- **Tools:** Android Studio, Git, GitHub  

---

## 📂 Project Structure

app/
├── activities/
│ ├── LoginActivity.java
│ ├── SignUpActivity.java
│ ├── MainActivity.java
│ └── CreateEventActivity.java
├── fragments/
│ ├── ProfileFragment.java
│ ├── MyEventsFragment.java
│ ├── DiscoverFragment.java
│ └── SettingsFragment.java
├── models/
│ ├── User.java
│ └── Event.java
└── utils/
└── NavigationUtils.java


---

## 🔑 Firebase Setup

1. Create a project at **Firebase Console**.
2. Add Android app with your package name.
3. Download `google-services.json` → place in `app/`.
4. Enable:
   - Authentication → Email/Password
   - Cloud Firestore

### Example Firestore Rules
```js
match /users/{uid} {
  allow read, write: if request.auth != null && request.auth.uid == uid;
}

match /events/{eventId} {
  allow read, write: if request.auth != null;
}
```

### ▶️ Getting Started
Prerequisites

Android Studio

Android SDK 24+

Java 8+

Steps
git clone https://github.com/your-username/smart-invitation-scanner.git
cd smart-invitation-scanner


Open in Android Studio, sync Gradle, connect Firebase, and run on emulator/device.

### 🧪 Build & Run

Run from Android Studio ▶️

Min SDK: 24

Target SDK: Latest stable

### 🗺 Roadmap

 QR code generation for invitations

 QR scanner implementation

 Guest list & check-in tracking

 Firebase Storage for images

 Cloud Functions for bulk invites

 Push notifications (FCM)

 Admin analytics

### 🤝 Contributing

Contributions are welcome!

Fork the repo

Create your branch (feature/my-feature)

Commit changes

Open a Pull Request

### 📝 License

This project is licensed under the MIT License.

### 👨‍💻 Author

Kundan Kumar
GitHub: https://github.com/i-kundankumar

⭐ If you find this project useful, please give it a star!
