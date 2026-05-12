package com.classhub.security;

import java.lang.annotation.*;

/**
 * Restricts a controller method to one or more roles.
 *
 * Usage:
 *   @RequireRole("TEACHER")
 *   @RequireRole({"TEACHER", "ADMIN"})
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    String[] value();
}
