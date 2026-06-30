# 🎵 Album Manager

A desktop application developed for the **Object-Oriented Programming (OOP)** course using **Java** and **JavaFX**.

The application allows users to build and manage a personal music album library through an intuitive graphical interface. Albums can be added manually or imported directly from the **Last.fm API**, making the process faster while automatically retrieving album information, tracks, genres, release year, cover art, and artist data.

---

## ✨ Features

- 🎼 Manage a personal album collection
- ➕ Add albums manually
- 🌐 Import complete album information from the **Last.fm API**
- 👨‍🎤 Automatic artist creation and association
- 🎵 Track management for each album
- 🖼️ Album cover support
- 💾 Persistent storage using binary files
- 🔍 Search functionality
- 🎨 JavaFX graphical interface
- ⚠️ Custom exception handling and input validation

---

## 🏛️ Project Architecture

The project follows a simplified **MVC (Model-View-Controller)** architecture.

```
src/
├── controller/      # Business logic and controllers
├── model/           # Domain classes and entities
├── persistence/     # Binary file persistence
├── service/         # Last.fm API integration
├── util/            # Utilities and custom exceptions
├── view/            # JavaFX user interface
└── Main.java        # Application entry point
```

---

## 🛠 Technologies

- Java 21
- JavaFX 21
- JSON (org.json)
- Last.fm REST API
- Object-Oriented Programming
- MVC Architecture
- Binary File Persistence

---

## 🚀 Running the Project

### Requirements

- JDK 21
- JavaFX SDK 21
- VS Code (recommended) with the Java Extension Pack

### Steps

1. Clone the repository

```bash
git clone https://github.com/juliabechaire/Gerenciador-de-Albuns.git
```

2. Open the project in VS Code.

3. Configure the JavaFX SDK in your VM arguments (or use the provided launch configuration).

4. Run:

```
src/Main.java
```

---

## 📦 Data Persistence

All albums, artists and related information are stored locally in binary files, allowing the library to persist between application executions.

---

## 🌐 Last.fm Integration

The application communicates with the Last.fm REST API to:

- Search albums by name
- Import album metadata
- Retrieve album artwork
- Import track lists
- Retrieve genres (tags)
- Retrieve release information

The API responses are received in **JSON** format and converted into Java objects used throughout the application.

---

## 📚 OOP Concepts Applied

This project was developed to practice several Object-Oriented Programming concepts, including:

- Encapsulation
- Inheritance
- Polymorphism
- Abstraction
- Exception Handling
- Collections
- File I/O
- MVC Design Pattern

---

## 📸 Screenshots

<img width="1366" height="720" alt="image" src="https://github.com/user-attachments/assets/d856b0ed-1094-4d6e-86cf-9079ec28c64f" />
<img width="1366" height="720" alt="image" src="https://github.com/user-attachments/assets/2151544f-e224-46ab-847f-f0e4524e60b5" />
<img width="1366" height="720" alt="image" src="https://github.com/user-attachments/assets/ace4659d-99c5-48fc-93d8-106c13b0cab0" />
<img width="1366" height="720" alt="image" src="https://github.com/user-attachments/assets/a1d8eae8-7927-4c88-99af-130d197b0aa1" />
<img width="1366" height="720" alt="image" src="https://github.com/user-attachments/assets/861876ae-7c9f-479c-b372-bd7ff3d03792" />
<img width="1366" height="720" alt="image" src="https://github.com/user-attachments/assets/1649fee6-c5c3-4932-b664-801c5b4ab5e6" />
<img width="1366" height="720" alt="image" src="https://github.com/user-attachments/assets/2cd3019a-4cfb-4536-8f6f-9497d6ddb7b3" />





---

## 👩‍💻 Author

**Júlia Bechaire**

Federal University of Santa Maria (UFSM)

Computer Engineering

---

## 📄 License

This project was developed for educational purposes as part of the Object-Oriented Programming course at UFSM.
