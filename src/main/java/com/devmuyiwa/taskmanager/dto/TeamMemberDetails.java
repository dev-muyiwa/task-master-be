package com.devmuyiwa.taskmanager.dto;

import com.devmuyiwa.taskmanager.enums.MemberRole;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class TeamMemberDetails {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String avatar;
    private MemberRole role;
    private Instant joinedAt;
    private int completedTasks;
    private int assignedTasks;
} 