# ClassHub Backend — Firebase Edition

A Spring Boot backend for the ClassHub capstone (CSC325), rewritten to use **Firebase** exclusively:

- **Firestore** — all data storage (replaces H2 + Spring Data JPA)
- **Firebase Admin SDK** — server-side Firestore access

## Tech stack
- Java 17
- Spring Boot 3.2
- Spring Web + Validation
- Firebase Admin SDK 9.3 (Firestore)

## Setup

### 1. Create a Firebase project
1. Go to [console.firebase.google.com](https://console.firebase.google.com)
2. Create a project (or use an existing one)
3. Enable **Firestore Database** in Native mode

### 2. Download your service account key
1. Firebase Console → Project Settings → **Service Accounts**
2. Click **Generate new private key** → save the JSON file
3. Place it at:
   ```
   src/main/resources/firebase-service-account.json
   ```
   > ⚠️ Add `firebase-service-account.json` to `.gitignore` — never commit credentials.

### 3. Run
```bash
mvn spring-boot:run
```
App starts on `http://localhost:8080`.

## API (unchanged from H2 version)

All `userId`, `courseId`, `assignmentId` path params are now **String** (Firestore document IDs).

### Auth
```
POST /api/auth/register
POST /api/auth/login
```

### Courses
```
POST /api/courses
GET  /api/courses/user/{userId}
```

### Assignments
```
POST  /api/assignments
GET   /api/assignments/user/{userId}
PATCH /api/assignments/{assignmentId}/complete
```

### GPA
```
POST /api/gpa/records
GET  /api/gpa/summary/{userId}
```

### Dashboard
```
GET /api/dashboard/{userId}
```

## Example requests

### Register
```json
{
  "fullName": "Anthony Randazzo",
  "email": "anthony@classhub.com",
  "password": "test123",
  "role": "STUDENT"
}
```

### Create course (use the `id` from the register response)
```json
{
  "courseCode": "CSC325",
  "courseName": "Software Engineering",
  "credits": 3,
  "semester": "Spring 2026",
  "userId": "<user-id-string>"
}
```

### Create assignment
```json
{
  "title": "Build Backend Prototype",
  "dueDate": "2026-05-10",
  "priority": "HIGH",
  "courseId": "<course-id-string>",
  "userId": "<user-id-string>"
}
```

### Add grade record
```json
{
  "letterGrade": "A",
  "gradePoints": 4.0,
  "courseId": "<course-id-string>",
  "userId": "<user-id-string>"
}
```

## Firestore collections
| Collection      | Description                  |
|-----------------|------------------------------|
| `users`         | User accounts                |
| `courses`       | Courses per user             |
| `assignments`   | Tasks/assignments per user   |
| `gradeRecords`  | Grade entries per user       |

## Notes
- Passwords are plain-text — this is a prototype only.
- IDs are UUID strings generated server-side (no more auto-increment longs).
- Demo data is seeded into Firestore on first startup if no users exist.
