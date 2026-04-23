ClassHub – Academic Assignment & Deadline Manager

Overview

ClassHub is a classroom-focused application designed to help students manage assignments, tests, quizzes, and academic deadlines in one centralized platform.
The system improves organization, reduces missed deadlines, and gives users a clear view of both upcoming and completed work.

Problem Statement

Students often receive assignment and test information through multiple channels such as email, LMS platforms, and in-class announcements. This scattered communication can lead to confusion, missed deadlines, and poor time management.

ClassHub addresses this issue by bringing all academic tasks into one structured system that organizes, tracks, and helps students stay on top of their workload.

How It Works (Preview)

Below, instructions will be provided alongside GIF demonstrations to show how to use the system and navigate key features.
#  Testing Summary (Prakash Pyakurel)

The application was tested by running the JavaFX interface and interacting with the login system.

### What was tested?
- Login functionality  
- Input validation  
- Navigation between screens



<img width="1651" height="1028" alt="Screenshot 2026-04-16 at 8 47 25 PM" src="https://github.com/user-attachments/assets/8eeacad1-40c6-4b08-8c5f-900ad07d2c63" />


### Results
- Valid login (admin / 1234) successfully navigates to the dashboard  
- Invalid login displays an error message  
- Application does not crash with incorrect input
  https://github.com/user-attachments/assets/181d0bab-11a0-4a49-916c-0879d87e583c

###  Issues Found
- No validation for empty input fields  
- Login uses hardcoded credentials (no backend authentication)
- <img width="404" height="463" alt="Screenshot 2026-04-22 at 9 58 59 PM" src="https://github.com/user-attachments/assets/17d1769d-0216-4b7b-91d1-8de47c39776d" />


### Note
This version does not include backend integration. Authentication is handled locally

Yet to test the backend
