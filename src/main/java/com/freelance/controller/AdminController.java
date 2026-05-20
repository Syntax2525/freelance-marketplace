package com.freelance.controller;

import com.freelance.dto.AdminOverviewDto;
import com.freelance.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Get admin dashboard overview (ADMIN only)
     */
    @GetMapping("/overview")
    public ResponseEntity<AdminOverviewDto> overview() {
        return ResponseEntity.ok(adminService.getOverview());
    }

    /**
     * Get all users (ADMIN only)
     */
    @GetMapping("/users")
    public ResponseEntity<Object> getAllUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(adminService.getAllUsers(role, active));
    }

    /**
     * Activate/Deactivate user account (ADMIN only)
     */
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<Object> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam boolean active) {
        return ResponseEntity.ok(adminService.updateUserStatus(userId, active));
    }

    /**
     * Assign or change user role (ADMIN only)
     */
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<Object> changeUserRole(
            @PathVariable Long userId,
            @RequestParam String newRole) {
        return ResponseEntity.ok(adminService.changeUserRole(userId, newRole));
    }

    /**
     * Delete a user (ADMIN only)
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete a job (ADMIN only)
     */
    @DeleteMapping("/jobs/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId) {
        adminService.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }

    /**
     * View all messages (ADMIN only)
     */
    @GetMapping("/messages")
    public ResponseEntity<Object> getAllMessages(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getAllMessages(userId, page, size));
    }

    /**
     * Handle dispute (ADMIN only)
     */
    @PostMapping("/disputes/{jobId}/resolve")
    public ResponseEntity<Object> resolveDispute(
            @PathVariable Long jobId,
            @RequestParam String resolution) {
        return ResponseEntity.ok(adminService.resolveDispute(jobId, resolution));
    }

    /**
     * Generate system reports (ADMIN only)
     */
    @GetMapping("/reports/system")
    public ResponseEntity<Object> getSystemReport() {
        return ResponseEntity.ok(adminService.getSystemReport());
    }
}
