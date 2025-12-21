# 📱 Smart Invitation Scanner
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

> 🚧 **Status:** This project is currently under active development.  
> Features, UI, and architecture may change as new functionality is added.

Smart Invitation Scanner is a modern Android application for creating events, managing digital invitations, and validating guest entry using QR code scanning. It helps organizers handle events efficiently with a clean Material UI and a Firebase-powered backend.

---

## ✨ Features

### 🔐 Authentication
- Email & password signup/login (Firebase Auth)
- Email verification
- Secure logout
- Input validation

### 👤 Profile
- Fetch & display profile from Firestore
- Read-only profile view with edit mode
- Update name & phone
- Sign out from profile

### 🧭 Navigation
- Bottom navigation with fragments
- Center Floating Action Button
- Smooth transitions

### 📅 Event Creation
- Modern “Create Event” screen
- Cover image UI
- Event title, organizer, type, date & time, location, pricing, description
- Material DatePicker & TimePicker
- Save events to Firestore as Timestamp

### 📷 QR Scanner *(Planned)*
- Scan QR codes at entry
- Verify guests in Firestore
- Prevent duplicate entries
- Track check-ins

### 🌗 UI & Theme
- Material Components design
- Light & Dark mode support
- Responsive layouts

---

## 🛠 Tech Stack

- **Language:** Java
- **UI:** AndroidX, Material Components
- **Backend:** Firebase
    - Authentication
    - Cloud Firestore
- **Architecture:** Activities + Fragments
- **Tools:** Android Studio, Git, GitHub

---

## 📂 Project Structure

```agsl
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
```

---

## 🔑 Firebase Setup

1. Create a project in **Firebase Console**.
2. Add an Android app with your package name.
3. Download `google-services.json` → place it in `app/`.
4. Enable:
    - **Authentication → Email/Password**
    - **Cloud Firestore**

### Example Firestore Rules

```js
match /users/{uid} {
  allow read, write: if request.auth != null && request.auth.uid == uid;
}

match /events/{eventId} {
  allow read, write: if request.auth != null;
}
```
## ▶️ Getting Started
### Prerequisites
- Android Studio
- Android SDK 24+
- Java 8+

### Clone & Run
```
git clone https://github.com/your-username/smart-invitation-scanner.git
cd smart-invitation-scanner
```


Open in Android Studio, sync Gradle, connect Firebase, and run on a device/emulator.

## 🧪 Build

- Run from Android Studio ▶️
- Min SDK: 24
- Target SDK: Latest stable

## 🗺 Roadmap

- QR code generation for invitations
- QR scanner implementation
- Guest list & check-in tracking
- Firebase Storage for images
- Cloud Functions for bulk invites
- Push notifications (FCM)
- Admin analytics dashboard
- Role-based access (organizer/staff)

## 🤝 Contributing

Contributions are welcome!

1. Fork the repo
2. Create a branch (feature/your-feature)
3. Commit your changes
4. Open a Pull Request

## 📝 License

This project is licensed under the MIT License.
See the LICENSE
file for details.

## 👨‍💻 Author

Kundan Kumar
GitHub: https://github.com/i-kundankumar