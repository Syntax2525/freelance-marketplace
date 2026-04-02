package com.freelance.controller;

import com.freelance.dto.AdminOverviewDto;
import com.freelance.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AdminOverviewDto> overview() {
        return ResponseEntity.ok(adminService.getOverview());
    }
}
