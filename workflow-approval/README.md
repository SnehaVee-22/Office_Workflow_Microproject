# Office Workflow Approval System — Backend

Spring Boot 3.2 REST API with JWT authentication, role-based access control, and email notifications.

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+
- STS (Spring Tool Suite) or IntelliJ IDEA

### 1. Database Setup
```sql
CREATE DATABASE workflow_db;
```

### 2. Configure application.properties
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/workflow_db?...
spring.datasource.username=root
spring.datasource.password=yourpassword

# Gmail SMTP (for email notifications)
spring.mail.username=yourapp@gmail.com
spring.mail.password=your-app-password
app.mail.enabled=true
```

### 3. Run in STS
- Import as **Existing Maven Project**
- Right-click project → **Run As** → **Spring Boot App**
- API starts at **http://localhost:8080/api**

### 4. Run Tests
```bash
mvn test
```
Tests use a separate MySQL database (workflow_test_db).
Create it before running tests:
  CREATE DATABASE workflow_test_db;
Update src/test/resources/application.properties with your MySQL credentials.

---

## 🔐 Default Login Credentials (seeded on first run)

| Role     | Email                  | Password      |
|----------|------------------------|---------------|
| Admin    | admin@company.com      | Admin@123     |
| Manager  | bob@company.com        | Manager@123   |
| Employee | alice@company.com      | Employee@123  |

---

## 📁 Project Structure

```
src/main/java/com/workflow/approval/
├── WorkflowApprovalApplication.java
├── config/
│   ├── SecurityConfig.java        # Spring Security + JWT + CORS
│   ├── AsyncConfig.java           # Enables @Async for email
│   └── DataInitializer.java       # Seeds admin + sample data on startup
├── entity/
│   ├── User.java                  # Roles: ADMIN, MANAGER, EMPLOYEE
│   ├── Department.java
│   ├── RequestType.java
│   ├── Request.java               # Core workflow entity
│   └── Notification.java
├── repository/
│   ├── UserRepository.java
│   ├── DepartmentRepository.java
│   ├── RequestTypeRepository.java
│   ├── RequestRepository.java     # Custom JPQL queries
│   └── NotificationRepository.java
├── security/
│   ├── JwtAuthFilter.java         # Validates JWT on every request
│   └── CustomUserDetailsService.java
├── service/
│   ├── AuthService.java
│   ├── AdminService.java
│   ├── EmployeeService.java
│   ├── ManagerService.java
│   ├── NotificationService.java
│   └── EmailService.java          # Async HTML email (credentials + status)
├── controller/
│   ├── AuthController.java        # POST /auth/login
│   ├── AdminController.java       # /admin/**
│   ├── EmployeeController.java    # /employee/**
│   └── ManagerController.java     # /manager/**
├── dto/
│   ├── request/                   # LoginRequest, CreateEmployeeRequest, etc.
│   └── response/                  # AuthResponse, UserResponse, RequestResponse, etc.
├── exception/
│   ├── ResourceNotFoundException.java
│   ├── BadRequestException.java
│   └── GlobalExceptionHandler.java
└── util/
    ├── JwtUtil.java
    └── RequestIdGenerator.java

src/test/java/com/workflow/approval/
├── service/
│   ├── AuthServiceTest.java       (5 tests)
│   ├── AdminServiceTest.java      (9 tests)
│   ├── EmployeeServiceTest.java   (10 tests)
│   ├── ManagerServiceTest.java    (8 tests)
│   └── NotificationServiceTest.java (6 tests)
├── controller/
│   ├── AuthControllerTest.java    (5 tests)
│   └── ManagerControllerTest.java (6 tests)
└── security/
    └── JwtUtilTest.java           (8 tests)
```

---

## 🌐 API Endpoints

### Auth
| Method | Endpoint         | Body                              | Description        |
|--------|-----------------|-----------------------------------|--------------------|
| POST   | /auth/login     | `{email, password}`               | Login (all roles)  |

### Admin  (`Authorization: Bearer <token>`)
| Method | Endpoint                             | Description                    |
|--------|--------------------------------------|--------------------------------|
| GET    | /admin/employees                     | List all employees/managers    |
| POST   | /admin/employees                     | Create employee (sends email)  |
| PUT    | /admin/employees/{id}                | Update employee                |
| PATCH  | /admin/employees/{id}/toggle-active  | Activate / Deactivate          |
| GET    | /admin/departments                   | List departments               |
| POST   | /admin/departments                   | Create department              |
| DELETE | /admin/departments/{id}              | Delete department              |
| GET    | /admin/request-types                 | List request types             |
| POST   | /admin/request-types                 | Create request type            |
| DELETE | /admin/request-types/{id}            | Delete request type            |
| GET    | /admin/dashboard/stats               | Dashboard statistics           |

### Employee
| Method | Endpoint                              | Description                     |
|--------|---------------------------------------|---------------------------------|
| GET    | /employee/requests                    | My requests                     |
| POST   | /employee/requests                    | Submit new request              |
| PUT    | /employee/requests/{id}               | Edit pending request            |
| PATCH  | /employee/requests/{id}/cancel        | Cancel pending request          |
| GET    | /employee/requests/search?requestId=  | Search by Request ID            |
| GET    | /employee/notifications               | My notifications                |
| PATCH  | /employee/notifications/{id}/read     | Mark notification as read       |

### Manager
| Method | Endpoint                              | Description                      |
|--------|---------------------------------------|----------------------------------|
| GET    | /manager/requests/pending             | Pending requests (assigned only) |
| PATCH  | /manager/requests/{id}/approve        | Approve with remarks             |
| PATCH  | /manager/requests/{id}/reject         | Reject with remarks              |
| GET    | /manager/requests/history             | History with date filter         |
| GET    | /manager/dashboard/stats              | Dashboard statistics             |

---

## 🗄️ Database Schema (auto-created by Hibernate)

```
users          → id, employee_id, name, email, password, role, department, active, manager_id
departments    → id, name, description
request_types  → id, name, description
requests       → id, request_id, employee_id, manager_id, request_type, leave_type,
                 duration, leave_plan, start_date, end_date, software_name,
                 software_reason, description, status, manager_remarks, decided_at
notifications  → id, user_id, request_id, message, type, is_read, created_at
```

---

## ✅ User Stories Covered

| US#   | Feature                                              | Endpoint                           |
|-------|------------------------------------------------------|------------------------------------|
| US001 | Admin login with email + password                   | POST /auth/login                   |
| US002 | Admin creates employee (Employee_ID unique key)     | POST /admin/employees              |
| US003 | Admin assigns roles (Employee / Manager)            | POST/PUT /admin/employees          |
| US004 | Admin activate/deactivate by Employee_ID            | PATCH /admin/employees/{id}/toggle |
| US005 | Employee login with email + password                | POST /auth/login                   |
| US006 | Employee creates new request                        | POST /employee/requests            |
| US007 | Employee edits pending request                      | PUT /employee/requests/{id}        |
| US008 | Employee cancels pending request                    | PATCH /employee/requests/{id}/cancel|
| US009 | Employee views request by Request_ID               | GET /employee/requests/search      |
| US010 | Manager views assigned pending requests             | GET /manager/requests/pending      |
| US011 | Manager approves/rejects (full details required)   | PATCH /manager/requests/{id}/approve|
| US012 | Manager adds remarks (mandatory validation)         | remarks field required             |
| US013 | Manager views history filtered by date             | GET /manager/requests/history      |
| US014 | In-app notifications on approval events            | GET /employee/notifications        |

---

## 📧 Email Configuration Notes

The system sends two types of emails:
1. **Credentials email** — when admin creates/updates an employee
2. **Status notification** — when a request is approved or rejected

To use Gmail SMTP:
1. Enable 2-Factor Authentication on your Gmail account
2. Create an App Password at myaccount.google.com/apppasswords
3. Use the app password in `spring.mail.password`

To disable emails during development: set `app.mail.enabled=false`
