package com.classhub.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;

/**
 * Spring MVC interceptor that enforces @RequireRole on controller methods.
 *
 * The FirebaseAuthFilter runs first and attaches the "role" attribute.
 * This interceptor reads that attribute and checks it against the annotation.
 */
@Component
public class RoleEnforcementInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {

        if (!(handler instanceof HandlerMethod method)) return true;

        RequireRole annotation = method.getMethodAnnotation(RequireRole.class);
        if (annotation == null) return true; // no restriction on this endpoint

        String callerRole = (String) request.getAttribute("role");
        boolean permitted = callerRole != null &&
                Arrays.asList(annotation.value()).contains(callerRole);

        if (!permitted) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"error\": \"Access denied. Required role(s): " +
                Arrays.toString(annotation.value()) + "\"}"
            );
            return false;
        }

        return true;
    }
}
