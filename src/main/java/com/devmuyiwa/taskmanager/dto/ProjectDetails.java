package com.devmuyiwa.taskmanager.dto;

import com.devmuyiwa.taskmanager.model.ProjectStatus;
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
public class ProjectDetails {
    private UUID id;
    private String title;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private TeamDTO team;
    private ProjectStatus status;
    private long totalTasks;
    private long completedTasks;
    private Instant createdAt;
    private Instant updatedAt;
} 