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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing user information and workspace access")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final WorkspaceService workspaceService;

    @Operation(
        summary = "Get current user's workspace details",
        description = "Retrieves the current user's details within a specific workspace context. " +
                     "Requires a valid JWT token and X-Workspace-ID header.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved user workspace details",
                content = @Content(schema = @Schema(implementation = UserWorkspaceDetails.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Invalid request or user is not a member of the workspace",
                content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing JWT token",
                content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
        }
    )
    @GetMapping("/me")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<UserWorkspaceDetails>> getCurrentUser(
            @Parameter(description = "The authenticated user's details", hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "The ID of the workspace to get user details for", required = true)
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

    @Operation(
        summary = "Get current user's workspaces",
        description = "Retrieves a list of all workspaces the current user is a member of. " +
                     "Requires a valid JWT token.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved user's workspaces",
                content = @Content(schema = @Schema(implementation = WorkspaceResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing JWT token",
                content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
        }
    )
    @GetMapping("/me/workspaces")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<WorkspaceResponse>>> getUserWorkspaces(
            @Parameter(description = "The authenticated user's details", hidden = true)
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