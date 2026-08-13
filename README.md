# 🔐 Secure File Management System

> A secure enterprise-grade File Management System backend built with **Java Spring Boot** and **PostgreSQL**. It enables organizations to securely upload, encrypt, store, and manage files with department-based access control, file forwarding, and approval workflows.

---

## 📌 Overview

This project is a secure backend solution for organizations where multiple departments share the same application while ensuring complete isolation and controlled access to their data. 

Instead of storing uploaded files directly inside the database, files are:
- Uploaded to the server in bulk or individually.
- Encrypted before storage using **AES Encryption**.
- Saved securely on disk (or designated storage paths).
- Tracked via metadata stored inside **PostgreSQL**.

The system guarantees that each department can manage its files, forward files, and participate in approval workflows with complete auditing and traceability.

---

## ✨ Key Features

### 👤 Authentication & Authorization
- **Secure JWT Authentication**: Stateless user authentication using JSON Web Tokens.
- **Dynamic Role-Based Access Control (RBAC)**: Manage roles and granular page-level permissions dynamically.
- **Security Levels**: Configurable security levels for files and users.
- **Department-Based Authorization**: Users are tied to specific departments, preventing unauthorized cross-department data access.

### 📂 File Management
- **Bulk Upload Support**: Efficiently upload multiple files at once.
- **File Trash & Soft Deletes**: Deleted files go to a designated "Trash" area for easy recovery.
- **File Searching & Status Tracking**: Search files by metadata and track their lifecycle.
- **File Types Management**: Define and restrict file uploads by registered file types.

### 🔄 Approval Workflow
- **Multi-step Approvals**: Files can require approvals from department managers.
- **Status Tracking**: Files pass through `PENDING`, `APPROVED`, and `REJECTED` states.
- **Department Interactions**: Files can be routed for approvals across different departments.

### 📨 File Forwarding & Notifications
- **File Forwarding**: Users can securely forward files to other users or departments.
- **Real-Time Notifications**: Server-Sent Events (SSE) provide real-time updates for file forwards and approvals.

### 🔒 Security
- **AES File Encryption**: Uploaded files are automatically encrypted locally on the server.
- **Secure File Storage**: Files are kept off the database; only metadata (original name, path, extension, hash) is persisted.

### 📊 Dashboard & Auditing
- **Dashboard Statistics**: Retrieve system statistics and aggregated data.
- **Audit Trails**: Core entities inherit from an `Audit` base class to automatically track creation and modification timestamps.

---

## 🏗 System Architecture

The application follows a standard layered architecture:

- **Controllers**: Handle incoming HTTP requests, input validation, and JWT security checks.
- **Services**: Contain business logic (encryption, approval routing, file forwarding).
- **Repositories**: Data Access Layer using Spring Data JPA.
- **Database**: Relational data stored in PostgreSQL.

---

## 🛠 Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.x**
  - Spring Web (REST APIs)
  - Spring Data JPA
  - Spring Security
  - Spring Validation
- **JWT (JSON Web Tokens)**: Authentication and session handling
- **Lombok**: Boilerplate code reduction
- **Maven**: Build and dependency management

### Database
- **PostgreSQL**: Primary relational database
- **H2 Database**: In-memory database used for testing

---

## 📁 Project Structure

```text
src/main/java/com/ADIB/FileSystem/
├── Business/
│   ├── dto/           # Data Transfer Objects (Requests & Responses)
│   ├── Enum/          # Enumerations (FILE_STATUS, NOTIFICATIONTYPE, etc.)
│   ├── event/         # Spring Application Events (Upload, Forward)
│   ├── Exceptions/    # Custom Business Exceptions & Handlers
│   ├── Model/         # JPA Entities (User, File, Department, Role, etc.)
│   └── service/       # Business Logic Services (FileService, AuthService, etc.)
├── config/            # Application Configurations (FileStorageProperties)
├── controller/        # REST API Endpoints
├── DataAccess/
│   ├── repository/    # Spring Data JPA Repositories
│   ├── specification/ # Dynamic Query Specifications
│   └── Validation/    # Custom Validation Annotations
├── exception/         # Global Exception Handler (Controller Advice)
├── mapper/            # Object Mapping Utilities
├── security/          # JWT Filters, UserDetails, and Security Configurations
└── uploads/ & Trash/  # Default directories for physical file storage
```

---

## 🔌 Core API Endpoints

### Authentication (`/api/auth`)
- `POST /register` - Register a new user
- `POST /login` - Authenticate and receive JWT tokens
- `POST /refresh` - Refresh an expired access token
- `POST /logout` - Logout user

### File Management (`/api/files`)
- `POST /bulk` - Upload multiple files simultaneously
- `PUT /{fileId}/status` - Update the approval status of a file
- `GET /trash` - List soft-deleted files
- `POST /{fileId}/forward` - Forward a file to another user/department

### Dashboards & Lookups
- `GET /api/dashboard/statistics` - Get general system stats
- `GET /api/files/forwarded/notifications` - Get unread SSE notifications

*(Note: Most endpoints require a valid `Bearer <JWT_TOKEN>` header and specific role permissions).*

---

## 🗄 Database Design

Key Entities include:
- **User**: Stores credentials, role, and department.
- **Department**: Logical groupings for users and files.
- **Role / Permission**: Defines what actions a user can perform.
- **File**: Stores metadata (name, size, path, extension, status).
- **FileDepartmentApproval**: Tracks file approval status by department managers.
- **FileForward**: Tracks file sharing between users.

---

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK) 17**
- **Maven 3.8+**
- **PostgreSQL 14+**

### 1. Database Setup
Ensure PostgreSQL is running and create a database named `ArchivingFileSystem_db`.

```sql
CREATE DATABASE "ArchivingFileSystem_db";
```

### 2. Environment Configuration
The application requires certain environment variables to be set (either in your environment or within an `application-config.properties` file). 

Required variables:
- `POSTGRES_PASSWORD`: The password for your `postgres` database user.
- `JWT_SECRET`: A secure, base64-encoded secret key used for signing JWTs.
- `FILE_STORAGE_UPLOAD_DIR` (Optional): Path to store uploads (defaults to `./uploads`).
- `FILE_STORAGE_TRASH_DIR` (Optional): Path to store deleted files (defaults to `./trash`).

### 3. Build & Run
Clone the repository, navigate to the root directory, and run the following Maven commands:

```bash
# Clean and compile the project
./mvnw clean install -DskipTests

# Run the application
./mvnw spring-boot:run
```

By default, the server will start and connect to the local PostgreSQL database at `jdbc:postgresql://localhost:5432/ArchivingFileSystem_db`.

---

## 👨‍💻 Development Workflow

1. Start the PostgreSQL service on your machine.
2. Verify that your IDE (IntelliJ IDEA, Eclipse, etc.) is using JDK 17.
3. Configure the required environment variables in your IDE's Run Configuration.
4. Run the application from `FileSystemApplication.java`.
5. The application relies on Spring Boot DevTools for hot-reloading during local development.

---

## 📈 Future Improvements
- Externalize the hardcoded AES encryption key to a secure vault or environment variable.
- Implement integration tests utilizing Testcontainers for isolated PostgreSQL testing.
- Add comprehensive API documentation using Swagger/OpenAPI (springdoc-openapi).
- Introduce a scheduled task to automatically purge files from the `Trash` directory after a retention period.
