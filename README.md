## Testing Notes (May 1, 2026)

Today I set up and tested the ClassHub application locally on my machine.

### Setup
I was unable to run the project directly through IntelliJ due to missing 
dependencies, so I used the terminal instead. I installed Maven manually 
since Homebrew had issues with my macOS version. I also had to generate 
a new Firebase service account key because the existing one had an invalid 
JWT signature.

I ran mvn clean install -U in the terminal to build the project successfully,
then ran both the backend and frontend from the terminal.


### What I Tested
I ran both the backend and frontend at the same time and tested the following:

Login ✅
Dashboard ✅
Add Course ❌ (no action when clicking button)
Add Task ❌ (no action when clicking submit)
Tutor ⚠️ (UI loads, but verify functionality if needed)
Flashcard Maker ⚠️ (UI loads, not fully tested)
Grades Calculator ⚠️ (basic UI works, logic needs verification)
Dark Mode ✅
Logout ❌
<img width="577" height="677" alt="Screenshot 2026-05-01 at 8 53 12 PM" src="https://github.com/user-attachments/assets/2fce21d7-f928-4584-9012-1bd77f23f2f4" />
<img width="606" height="711" alt="Screenshot 2026-05-01 at 8 53 01 PM" src="https://github.com/user-attachments/assets/d96445af-ed4c-4ee8-b433-058ee240fbb2" />


