Testing Summary

The application was tested by running the JavaFX interface and interacting with the login system.

 What was tested
- Login functionality
- Input validation
- Navigation between screens

Results
- Valid login (admin / 1234) successfully navigates to the dashboard
- Invalid login displays an error message
- Application does not crash with incorrect input

Issues Found
- The system does not properly validate empty input fields
- Login uses hardcoded credentials (no backend authentication)

Note
This application does not currently include a backend or database. Authentication is handled locally in the frontend.