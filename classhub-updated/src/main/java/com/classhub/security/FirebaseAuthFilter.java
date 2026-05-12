package com.classhub.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Verifies the Firebase ID token on every protected request and attaches
 * the caller's UID and role as request attributes for downstream use.
 *
 * Request attributes set after successful verification:
 *   - "uid"  : String  — Firebase UID of the authenticated user
 *   - "role" : String  — role custom claim (STUDENT, TEACHER, or ADMIN)
 */
@Component
public class FirebaseAuthFilter extends OncePerRequestFilter {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/register",
            "/api/auth/verify"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                      "Missing or invalid Authorization header.");
            return;
        }

        String idToken = authHeader.substring(7);

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

            request.setAttribute("uid", decodedToken.getUid());

            Object roleClaim = decodedToken.getClaims().get("role");
            request.setAttribute("role", roleClaim != null ? roleClaim.toString() : "");

            filterChain.doFilter(request, response);

        } catch (FirebaseAuthException e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                      "Invalid or expired Firebase token.");
        }
    }

    private void sendError(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
