package com.devmuyiwa.taskmanager.controller;

import com.devmuyiwa.taskmanager.common.dto.ApiResponse;
import com.devmuyiwa.taskmanager.dto.TeamMemberDetails;
import com.devmuyiwa.taskmanager.dto.TeamResponse;
import com.devmuyiwa.taskmanager.security.CustomUserDetails;
import com.devmuyiwa.taskmanager.service.TeamService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<TeamResponse>>> getTeamsInWorkspace(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestHeader("X-Workspace-ID") @NotNull UUID workspaceId) {
        List<TeamResponse> teams = teamService.getUserTeams(userDetails.getId(), workspaceId);
        return ResponseEntity.ok(ApiResponse.success(teams));
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<ApiResponse<List<TeamMemberDetails>>> getTeamMembers(
            @PathVariable @Valid UUID teamId,
            @RequestHeader("X-Workspace-ID") @NotNull UUID workspaceId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<TeamMemberDetails> members = teamService.getTeamMembers(teamId, workspaceId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(members));
    }
}
