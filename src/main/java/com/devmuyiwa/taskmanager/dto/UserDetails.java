package com.devmuyiwa.taskmanager.dto;

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
public class UserDetails {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String avatar;
    private Instant createdAt;
    private Instant updatedAt;
} 