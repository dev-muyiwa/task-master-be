package com.devmuyiwa.taskmanager.controller;

import com.devmuyiwa.taskmanager.common.dto.ApiResponse;
import com.devmuyiwa.taskmanager.dto.UserDetails;
import com.devmuyiwa.taskmanager.dto.UserWorkspaceDetails;
import com.devmuyiwa.taskmanager.dto.WorkspaceResponse;
import com.devmuyiwa.taskmanager.exception.ResourceNotFoundException;
import com.devmuyiwa.taskmanager.security.CustomUserDetails;
import com.devmuyiwa.taskmanager.service.AuthService;
import com.devmuyiwa.taskmanager.service.UserService;
import com.devmuyiwa.taskmanager.service.WorkspaceService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthService authService;
    private final WorkspaceService workspaceService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserWorkspaceDetails>> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestHeader(value = "X-Workspace-ID") @NotNull UUID workspaceId) {
        try {
            UserWorkspaceDetails details = workspaceService.getUserWorkspaceDetails(userDetails.user(), workspaceId);
            return ResponseEntity.ok(ApiResponse.success(details, "User workspace details retrieved successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to retrieve user details: " + e.getMessage()));
        }
    }

    @GetMapping("/me/workspaces")
    public ResponseEntity<ApiResponse<List<WorkspaceResponse>>> getUserWorkspaces(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<WorkspaceResponse> workspaces = workspaceService.getUserWorkspaces(userDetails.user());
            return ResponseEntity.ok(ApiResponse.success(workspaces, "Workspaces retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to retrieve workspaces: " + e.getMessage()));
        }
    }
} 