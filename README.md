Android Developer Assignment
Objective:
Develop an Android application that integrates with APIs, processes data using Room Database, and
includes image handling with a PDF viewer.
Requirements:
API Integration
1. Get All Accounts API -> https://fssservices.bookxpert.co/api/Fillaccounts/nadc/2024-2025
-> Fetch the list of accounts from the provided API.
-> Display the retrieved accounts in a RecyclerView.
2. Report (PDF Viewer) ->
https://fssservices.bookxpert.co/GeneratedPDF/Companies/nadc/2024-2025/BalanceSheet.pdf
-> Display the PDF using Android's built-in PDF viewer or a third-party library.
3. Image Capture & Gallery Selection
-> Implement an option to capture an image using the camera.
-> Allow users to pick an image from the gallery.
-> Display the selected image in an ImageView.
Room Database Implementation
1. Save All Accounts
-> Store the account details retrieved from the API into a Room Database.
2. Alternate Names for Accounts
-> Add a field to store an alternate name for each account.
Example: If accountName = "Rammohan", allow the user to add alternateName = "mohan".
3. Speech-to-Text for Alternate Name Entry
-> Allow users to type or use voice input to enter the alternate name.4. Update & Delete Accounts
-> Implement update and delete operations for saved accounts.
-> Provide a UI (like a dialog or swipe action) to edit or remove accounts.
Technical Requirements:
-> Use Kotlin for development.
-> Modern UI/UX
-> Follow MVVM architecture.
-> Implement Retrofit for API calls.
-> Use Room Database for local storage.
-> Integrate Speech-to-Text for voice input.
-> Implement Glide or Coil for image loading.
-> Handle runtime permissions for camera and storage.
Deliverables:
-> Source code in a GitHub repository.
-> APK File.
