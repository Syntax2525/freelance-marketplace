package com.freelance.util;

import com.freelance.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Utility class for role-based authorization validation.
 * Provides methods to check user roles and throw appropriate exceptions.
 */
public class RoleGuard {

    public static final String ROLE_FREELANCER = "ROLE_FREELANCER";
    public static final String ROLE_CLIENT = "ROLE_CLIENT";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    private RoleGuard() {
        // Utility class
    }

    /**
     * Require user to have a specific role.
     * @param user the user to check
     * @param requiredRole the required role (e.g., ROLE_CLIENT)
     * @throws ResponseStatusException with 403 if role doesn't match
     */
    public static void requireRole(User user, String requiredRole) {
        if (user == null || !hasRole(user, requiredRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Access denied. This action requires " + requiredRole + " role.");
        }
    }

    /**
     * Require user to have one of several roles.
     * @param user the user to check
     * @param requiredRoles the allowed roles
     * @throws ResponseStatusException with 403 if none match
     */
    public static void requireAnyRole(User user, String... requiredRoles) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. User not found.");
        }

        for (String role : requiredRoles) {
            if (hasRole(user, role)) {
                return; // User has at least one of the required roles
            }
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Access denied. User does not have required permissions.");
    }

    /**
     * Check if user has a specific role.
     * @param user the user to check
     * @param role the role to verify
     * @return true if user has the role, false otherwise
     */
    public static boolean hasRole(User user, String role) {
        return user != null && role != null && user.getRoleName() != null
                && user.getRoleName().equalsIgnoreCase(role);
    }

    /**
     * Check if user is a freelancer.
     * @param user the user to check
     * @return true if user is a freelancer
     */
    public static boolean isFreelancer(User user) {
        return hasRole(user, ROLE_FREELANCER);
    }

    /**
     * Check if user is a client.
     * @param user the user to check
     * @return true if user is a client
     */
    public static boolean isClient(User user) {
        return hasRole(user, ROLE_CLIENT);
    }

    /**
     * Check if user is an admin.
     * @param user the user to check
     * @return true if user is an admin
     */
    public static boolean isAdmin(User user) {
        return hasRole(user, ROLE_ADMIN);
    }

    /**
     * Prevent freelancer from performing client-only action.
     * @param user the user to check
     * @throws ResponseStatusException with 403 if user is freelancer
     */
    public static void forbidFreelancer(User user) {
        if (isFreelancer(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "This action is not permitted for freelancers.");
        }
    }

    /**
     * Prevent client from performing freelancer-only action.
     * @param user the user to check
     * @throws ResponseStatusException with 403 if user is client
     */
    public static void forbidClient(User user) {
        if (isClient(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "This action is not permitted for clients.");
        }
    }

    /**
     * Prevent non-admin from performing admin-only action.
     * @param user the user to check
     * @throws ResponseStatusException with 403 if user is not admin
     */
    public static void requireAdmin(User user) {
        if (!isAdmin(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "This action requires admin privileges.");
        }
    }

    /**
     * Prevent admin from performing user action (should not act as client or freelancer).
     * @param user the user to check
     * @throws ResponseStatusException with 403 if user is admin
     */
    public static void forbidAdmin(User user) {
        if (isAdmin(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Administrators cannot perform this action.");
        }
    }
}
