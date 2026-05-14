package org.example.classhub;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * All auth is now handled by the Spring Boot backend using JWT.
 * No Firebase client SDK calls — no rate limits, no propagation delays.
 */
public class FirebaseAuthClient {

    private static final String BASE = "http://localhost:8080";

    private final HttpClient   httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper     = new ObjectMapper();

    // ── Auth ─────────────────────────────────────────────────────────────────

    /**
     * Register: backend creates Firebase Auth account + Firestore profile + returns JWT.
     * Stores the JWT in SessionManager and returns the user node.
     */
    public JsonNode register(String fullName, String email, String password, String role) throws Exception {
        String safeName = fullName.replace("\\", "\\\\").replace("\"", "\\\"");
        String body = String.format(
            "{\"fullName\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}",
            safeName, email, password, role);

        HttpResponse<String> response = post("/api/auth/register", body, null);
        System.out.println("[Auth] register status=" + response.statusCode());
        System.out.println("[Auth] register body=" + response.body());

        if (response.statusCode() != 201) {
            JsonNode err = mapper.readTree(response.body());
            String msg = err.path("message").asText(err.path("error").asText("Registration failed"));
            throw new Exception(msg);
        }

        JsonNode result = mapper.readTree(response.body());
        storeSession(result);
        return result.path("user");
    }

    /**
     * Login: backend verifies password and returns JWT.
     * Stores the JWT in SessionManager and returns the user node.
     */
    public JsonNode login(String email, String password) throws Exception {
        String body = String.format(
            "{\"email\":\"%s\",\"password\":\"%s\"}", email, password);

        HttpResponse<String> response = post("/api/auth/login", body, null);
        System.out.println("[Auth] login status=" + response.statusCode());
        System.out.println("[Auth] login body=" + response.body());

        if (response.statusCode() != 200) {
            JsonNode err = mapper.readTree(response.body());
            String msg = err.path("message").asText(err.path("error").asText("Invalid email or password"));
            throw new Exception(msg);
        }

        JsonNode result = mapper.readTree(response.body());
        storeSession(result);
        return result.path("user");
    }

    private void storeSession(JsonNode result) {
        String token = result.path("token").asText("");
        JsonNode user = result.path("user");
        SessionManager.setIdToken(token);
        SessionManager.login(
            user.path("id").asText(),
            user.path("fullName").asText(),
            user.path("role").asText(),
            user.path("email").asText()
        );
    }

    // ── Courses ──────────────────────────────────────────────────────────────

    public JsonNode getCourses(String userId, String token) throws Exception {
        HttpResponse<String> r = get("/api/courses/user/" + userId, token);
        if (r.statusCode() != 200) throw new Exception("Failed to load courses");
        return mapper.readTree(r.body());
    }

    public JsonNode createCourse(String courseName, String courseCode,
                                 String semester, int credits, String token) throws Exception {
        String body = String.format(
            "{\"courseName\":\"%s\",\"courseCode\":\"%s\",\"semester\":\"%s\",\"credits\":%d}",
            courseName, courseCode, semester, credits);
        HttpResponse<String> r = post("/api/courses", body, token);
        if (r.statusCode() != 200 && r.statusCode() != 201) {
            JsonNode err = mapper.readTree(r.body());
            throw new Exception(err.path("message").asText("Failed to create course"));
        }
        return mapper.readTree(r.body());
    }

    // ── Assignments ──────────────────────────────────────────────────────────

    public JsonNode getAssignments(String userId, String token) throws Exception {
        HttpResponse<String> r = get("/api/assignments/user/" + userId, token);
        if (r.statusCode() != 200) throw new Exception("Failed to load assignments");
        return mapper.readTree(r.body());
    }

    public void markAssignmentComplete(String assignmentId, String token) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/api/assignments/" + assignmentId + "/complete"))
                .header("Authorization", "Bearer " + token)
                .method("PATCH", HttpRequest.BodyPublishers.noBody()).build();
        httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    }

    public JsonNode createAssignment(String title, String courseId,
                                     String dueDate, String token) throws Exception {
        String body = String.format(
            "{\"title\":\"%s\",\"courseId\":\"%s\",\"dueDate\":\"%s\",\"priority\":\"MEDIUM\",\"description\":\"\"}",
            title, courseId, dueDate);
        HttpResponse<String> r = post("/api/assignments", body, token);
        if (r.statusCode() != 200 && r.statusCode() != 201)
            throw new Exception("Failed to create assignment");
        return mapper.readTree(r.body());
    }

    // ── Grades / GPA ─────────────────────────────────────────────────────────

    public JsonNode getGpaSummary(String userId, String token) throws Exception {
        HttpResponse<String> r = get("/api/gpa/summary/" + userId, token);
        if (r.statusCode() != 200) throw new Exception("Failed to load GPA summary");
        return mapper.readTree(r.body());
    }

    public JsonNode getGradeRecords(String userId, String token) throws Exception {
        HttpResponse<String> r = get("/api/gpa/records/" + userId, token);
        if (r.statusCode() != 200) throw new Exception("Failed to load grade records");
        return mapper.readTree(r.body());
    }

    public JsonNode addGradeRecord(String courseId, String letterGrade,
                                   double gradePoints, String token) throws Exception {
        String body = String.format(
            "{\"courseId\":\"%s\",\"letterGrade\":\"%s\",\"gradePoints\":%.1f}",
            courseId, letterGrade, gradePoints);
        HttpResponse<String> r = post("/api/gpa/records", body, token);
        if (r.statusCode() != 200 && r.statusCode() != 201)
            throw new Exception("Failed to save grade");
        return mapper.readTree(r.body());
    }

    // ── Notes ────────────────────────────────────────────────────────────────

    public JsonNode getNotes(String userId, String token) throws Exception {
        HttpResponse<String> r = get("/api/notes/user/" + userId, token);
        if (r.statusCode() != 200) throw new Exception("Failed to load notes");
        return mapper.readTree(r.body());
    }

    public JsonNode saveNote(String noteId, String title, String content, String token) throws Exception {
        String safeTitle   = title.replace("\\", "\\\\").replace("\"", "\\\"");
        String safeContent = content.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
        String body = String.format("{\"title\":\"%s\",\"content\":\"%s\"}", safeTitle, safeContent);
        boolean isNew = noteId.isEmpty();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/api/notes" + (isNew ? "" : "/" + noteId)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .method(isNew ? "POST" : "PUT", HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> r = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() != 200 && r.statusCode() != 201) throw new Exception("Failed to save note");
        return mapper.readTree(r.body());
    }

    public void deleteNote(String noteId, String token) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/api/notes/" + noteId))
                .header("Authorization", "Bearer " + token)
                .DELETE().build();
        httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    }

    // ── HTTP helpers ─────────────────────────────────────────────────────────

    private HttpResponse<String> get(String path, String token) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path))
                .GET();
        if (token != null) b.header("Authorization", "Bearer " + token);
        return httpClient.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String path, String body, String token) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body));
        if (token != null) b.header("Authorization", "Bearer " + token);
        return httpClient.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }
}
