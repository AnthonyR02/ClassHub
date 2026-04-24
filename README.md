ClassHub – Academic Assignment & Deadline Manager

Overview

ClassHub is a classroom-focused application designed to help students manage assignments, tests, quizzes, and academic deadlines in one centralized platform.
The system improves organization, reduces missed deadlines, and gives users a clear view of both upcoming and completed work.

Problem Statement

Students often receive assignment and test information through multiple channels such as email, LMS platforms, and in-class announcements. This scattered communication can lead to confusion, missed deadlines, and poor time management.

ClassHub addresses this issue by bringing all academic tasks into one structured system that organizes, tracks, and helps students stay on top of their workload.

How It Works (Preview)

Below, instructions will be provided alongside GIF demonstrations to show how to use the system and navigate key features.


ClassHub Backend Testing Report
API Testing Summary
On April 24, 2026, I tested the backend of the ClassHub application using Postman to ensure that all APIs are functioning correctly and that data is properly stored and retrieved from Firebase Firestore. The testing focused on core features such as user authentication, course management, and assignment creation.
 1. User Registration
•	Endpoint: POST /api/auth/register 
•	Result: Success (200 OK) 
Description:
A new user was successfully registered using the API. The system returned a unique user ID, and the user data was stored correctly in the users collection in Firebase Firestore.
2. User Login
•	Endpoint: POST /api/auth/login 
•	Result: Success (200 OK) 
Description:
The login API successfully authenticated the user using the provided credentials. The system returned the correct user information, confirming that data retrieval from Firebase is working properly.
 3. Create Course
•	Endpoint: POST /api/courses 
•	Result: Success (200 OK) 
Description:
A course was successfully created and linked to the user using the userId. The course data appeared correctly in the courses collection in Firebase Firestore.
 4. Get Courses
•	Endpoint: GET /api/courses/user/{userId} 
•	Result: Success (200 OK) 
Description:
The API returned all courses associated with the specified user. Multiple course records were retrieved correctly, confirming proper query functionality and data relationships.
5. Create Assignment
•	Endpoint: POST /api/assignments 
•	Result: Success (200 OK) 
Description:
An assignment was successfully created and linked to both the user and the course. The assignment data was stored in the assignments collection in Firebase Firestore.
 Issues Encountered
During testing, a 405 Method Not Allowed error was encountered when using an incorrect HTTP method for an endpoint. This issue was resolved by switching to the correct method (GET instead of POST), confirming proper API behavior.
Conclusion
All tested APIs are functioning correctly. The backend is successfully integrated with Firebase Firestore, and both data storage and retrieval operations are working as expected. The system supports full interaction between users, courses, and assignments, demonstrating a complete and functional backend workflow.




<img width="468" height="632" alt="image" src="https://github.com/user-attachments/assets/6b263f0c-1f0a-47cc-95a7-7882421b69ad" />
<img width="2048" height="1430" alt="image" src="https://github.com/user-attachments/assets/591daeeb-1035-4f69-9a5d-232d684a0697" />
<img width="1538" height="1464" alt="image" src="https://github.com/user-attachments/assets/399d4eae-2658-4199-9edb-40eae43a1768" />
<img width="1560" height="827" alt="Screenshot 2026-04-24 at 5 32 12 PM" src="https://github.com/user-attachments/assets/47e025e5-72cf-47aa-9915-417581b5ad26" />
