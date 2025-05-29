package com.devmuyiwa.taskmanager.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class TeamResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID workspaceId;
    private Instant createdAt;
    private Instant updatedAt;
} 