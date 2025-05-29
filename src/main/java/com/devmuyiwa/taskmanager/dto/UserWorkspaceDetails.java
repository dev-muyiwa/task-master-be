package com.devmuyiwa.taskmanager.dto;

import com.devmuyiwa.taskmanager.enums.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWorkspaceDetails {
    // User details
    private UUID userId;
    private String email;
    private Instant userCreatedAt;
    private Instant userUpdatedAt;

    // Workspace member details
    private UUID workspaceId;
    private String workspaceName;
    private String firstName;
    private String lastName;
    private String avatar;
    private WorkspaceRole role;
    private Instant joinedAt;
} 