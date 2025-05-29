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
public class WorkspaceResponse {
    private UUID id;
    private String name;
    private String description;
    private WorkspaceRole role;
    private Instant joinedAt;
    private Instant createdAt;
    private Instant updatedAt;
} 