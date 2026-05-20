package com.freelance.util;

import com.freelance.model.User;
import com.freelance.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

/**
 * Utility class for extracting security information from SecurityContext.
 * Provides convenient methods to get current authenticated user.
 */
public class SecurityUtil {

    private SecurityUtil() {
        // Utility class - should not be instantiated
    }

    /**
     * Get current authenticated username from security context.
     * @return the username (email in this case)
     * @throws ResponseStatusException with 401 if not authenticated
     */
    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return auth.getName();
    }

    /**
     * Get current authenticated user's ID.
     * Note: This requires the user repository to be passed in.
     * @param userRepository the user repository
     * @return the current user
     * @throws ResponseStatusException with 401 if not authenticated or user not found
     */
    public static User getCurrentUser(UserRepository userRepository) {
        String email = getCurrentUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found in database"));
    }

    /**
     * Check if current user is authenticated.
     * @return true if authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && 
               !"anonymousUser".equals(auth.getPrincipal());
    }

    /**
     * Get current user's role.
     * @return the role name (e.g., ROLE_CLIENT, ROLE_FREELANCER)
     */
    public static String getCurrentRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "ANONYMOUS";
        }
        return auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .findFirst()
                .orElse("ANONYMOUS");
    }

    /**
     * Verify that current user owns a specific resource (by checking their ID).
     * @param resourceUserId the ID of the user that owns the resource
     * @throws ResponseStatusException with 403 if user doesn't own the resource
     */
    public static void verifyResourceOwnership(Long resourceUserId, UserRepository userRepository) {
        User currentUser = getCurrentUser(userRepository);
        if (!currentUser.getId().equals(resourceUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You do not have permission to access or modify this resource.");
        }
    }

    /**
     * Verify that current user has a specific role.
     * @param requiredRole the required role
     * @throws ResponseStatusException with 403 if user doesn't have the role
     */
    public static void verifyRole(String requiredRole) {
        String currentRole = getCurrentRole();
        if (!currentRole.equalsIgnoreCase(requiredRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Access denied. This action requires " + requiredRole + " role.");
        }
    }
}
