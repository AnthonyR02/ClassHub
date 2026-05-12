package org.example.classhub;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Handles the two-step Firebase login flow:
 * First step - exchange email+password for a Firebase ID token
 * Second step - sends that token to Spring Boot backend to get the user profile
 */
public class FirebaseAuthClient {

    // Get this from Firebase Console → Project Settings → General → Web API Key
    private static final String FIREBASE_API_KEY = "AIzaSyDeTUF_bA-J2kgtrMBd7bACKU5HfZCrxvg";
    private static final String FIREBASE_SIGNIN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + FIREBASE_API_KEY;
    private static final String BACKEND_VERIFY_URL = "http://localhost:8080/api/auth/verify";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Step 1: Signs in with Firebase and returns an ID token.
     * Throws an exception with a readable message if credentials are wrong.
     */
    public String signIn(String email, String password) throws Exception {
        String body = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
                email, password
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(FIREBASE_SIGNIN_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        JsonNode json = mapper.readTree(response.body());

        if (response.statusCode() != 200) {
            // Firebase returns a readable error message
            String errorMessage = json.path("error").path("message").asText("Invalid credentials");
            throw new Exception(errorMessage);
        }


        SessionManager.setIdToken(json.get("idToken").asText());
        return json.get("idToken").asText();
    }

    /**
     * Step 2: Sends the ID token to your Spring Boot backend to verify
     * and get back the full user profile.
     */
    public JsonNode verifyWithSpring(String idToken) throws Exception {
        String body = String.format("{\"idToken\":\"%s\"}", idToken);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BACKEND_VERIFY_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Backend verification failed");
        }

        JsonNode user = mapper.readTree(response.body());

        // store in session
        SessionManager.login(user.path("id").asText(), user.path("fullName").asText(), user.path("role").asText(), user.path("email").asText()
        );

        return user;
    }

    // Get all assignments for a user
    public JsonNode getAssignments(String userId, String idToken) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/assignments/user/" + userId))
                .header("Authorization", "Bearer " + idToken)
                .GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Failed to load assignments");
        }
        return mapper.readTree(response.body());
    }

    // Mark an assignment complete
    public void markAssignmentComplete(String assignmentId, String idToken) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/assignments/" + assignmentId + "/complete"))
                .header("Authorization", "Bearer " + idToken)
                .method("PATCH", HttpRequest.BodyPublishers.noBody()).build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public JsonNode getGpaSummary(String userId, String idToken) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/gpa/summary/" + userId))
                .header("Authorization", "Bearer " + idToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Failed to load GPA summary");
        }
        return mapper.readTree(response.body());
    }
}