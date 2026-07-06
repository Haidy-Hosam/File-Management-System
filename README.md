# 🔐 Secure File Management System

> A secure enterprise-grade File Management System built with **ASP.NET Core** and **PostgreSQL** that enables organizations to securely upload, encrypt, store, and manage files with department-based access control.

---

## 📌 Overview

This project is designed as a secure backend solution for organizations where multiple departments share the same server while ensuring complete isolation of their data.

Instead of storing uploaded files directly inside the database, files are:

- Uploaded to the server
- Encrypted before storage
- Saved securely on disk
- Only their metadata is stored inside PostgreSQL

The system guarantees that each department can only access its own files while providing complete auditing and traceability of every file operation.

---

## ✨ Features

### 👤 Authentication & Authorization

- Secure User Authentication
- Password Hashing
- Role-Based Access Control (RBAC)
- Department-Based Authorization

Supported Roles:

- 👑 Admin
- 👨‍💼 Manager
- 👨‍💻 Employee

---

### 📂 File Management

- Upload Files
- Download Files
- View Files
- Soft Delete Files
- Department Isolation
- Secure File Storage

---

### 🔒 Security

- AES File Encryption
- SHA-256 File Integrity Verification
- Unique File Naming
- Secure File Paths
- Initialization Vector (IV) for every encrypted file
- Protection against duplicate file names

---

### 📑 Audit Trail

Every important action is logged:

- Upload
- Download
- View
- Delete

Including:

- User
- File
- Action
- Timestamp
- IP Address

---

## 🏗 System Architecture

```

Client

↓

ASP.NET Core Web API

↓

Business Logic

↓

Encryption Service

↓

File Storage

↓

PostgreSQL Database

```

---

## 🗄 Database Design

Main Entities

- Roles
- Departments
- Users
- Files
- FileAuditLogs

### Relationships

- One Role ➜ Many Users
- One Department ➜ Many Users
- One Department ➜ Many Files
- One User ➜ Many Uploaded Files
- One File ➜ Many Audit Logs

---

## 🔒 File Upload Workflow

```

User Uploads File

↓

Validate Extension

↓

Validate MIME Type

↓

(Optional) Validate File Signature

↓

Generate SHA-256 Hash

↓

Generate Encryption IV

↓

Encrypt File

↓

Store File on Server

↓

Save Metadata in PostgreSQL

↓

Create Audit Log

```

---

## 📦 Stored File Metadata

The database stores only metadata.

| Field | Description |
|---------|-------------|
| OriginalFileName | User uploaded filename |
| StoredFileName | Generated unique filename |
| FilePath | Physical file location |
| FileExtension | File extension |
| ContentType | MIME type |
| FileSize | File size |
| FileHash | SHA-256 integrity hash |
| EncryptionIV | AES Initialization Vector |
| UploadedBy | User who uploaded the file |
| DepartmentId | File owner department |
| UploadedAt | Upload timestamp |

---

## 🛡 Security Considerations

- Passwords are never stored in plain text.
- Uploaded files are encrypted before storage.
- Files are never stored inside the database.
- Only metadata is stored in PostgreSQL.
- SHA-256 is used to verify file integrity.
- Every encryption operation generates a unique IV.
- Every important action is logged for auditing.

---

## 🛠 Tech Stack

### Backend

- ASP.NET Core Web API
- C#
- Entity Framework Core

### Database

- PostgreSQL
- pgAdmin

### Security

- AES Encryption
- SHA-256 Hashing
- JWT Authentication

### Tools

- Visual Studio
- Git
- GitHub
- Postman

---

## 📈 Future Improvements

- File Versioning
- Search by File Metadata
- Full Text Search
- Email Notifications
- Cloud Storage Support
- Two-Factor Authentication
- File Preview
- Docker Deployment

---

## 🚀 Getting Started

Clone the repository

```bash
git clone https://github.com/yourusername/Secure-File-Management-System.git
```

Navigate to the project

```bash
cd Secure-File-Management-System
```

Restore packages

```bash
dotnet restore
```

Run the project

```bash
dotnet run
```

---

## 📋 Project Status

🚧 Under Development

---

## 👩‍💻 Developed By

**Haidy Hosam**

Computer Science Student

Backend Developer (.NET)

---

## ⭐ If you like this project

Give it a ⭐ on GitHub.
