package com.devmuyiwa.taskmanager.controller;

import com.devmuyiwa.taskmanager.common.dto.ApiResponse;
import com.devmuyiwa.taskmanager.dto.ProjectDetails;
import com.devmuyiwa.taskmanager.dto.ProjectFilterDTO;
import com.devmuyiwa.taskmanager.model.ProjectStatus;
import com.devmuyiwa.taskmanager.security.CustomUserDetails;
import com.devmuyiwa.taskmanager.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<ProjectDetails>>> getProjectsInWorkspace(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestHeader("X-Workspace-ID") @NotNull UUID workspaceId) {
        List<ProjectDetails> projects = projectService.getUserProjects(userDetails.getId(), workspaceId);
        return ResponseEntity.ok(ApiResponse.success(projects));
    }

//    @GetMapping("/")
//    public ResponseEntity<Page<ProjectDetails>> getMyProjects(
//            @AuthenticationPrincipal UserDetails userDetails,
//            @RequestParam(required = false) String title,
//            @RequestParam(required = false) String status,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "createdAt") String sortBy,
//            @RequestParam(defaultValue = "desc") String direction) {
//
//        UUID userId = UUID.fromString(userDetails.getUsername());
//
//        ProjectFilterDTO filter = new ProjectFilterDTO();
//        filter.setTitle(title);
//        if (status != null) {
//            try {
//                filter.setStatus(ProjectStatus.valueOf(status.toUpperCase()));
//            } catch (IllegalArgumentException e) {
//                // If status is invalid, we'll ignore it (treat as null)
//            }
//        }
//
//        // Create pageable with sorting
//        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
//        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
//
//        Page<ProjectDetails> projects = projectService.getProjectsByUserId(userId, filter, pageRequest);
//        return ResponseEntity.ok(projects);
//    }
}
