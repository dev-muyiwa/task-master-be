package com.devmuyiwa.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing user authentication details")
public class AuthResponse {
    @Schema(
        description = "Unique identifier of the user",
        example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private UUID userId;

    @Schema(
        description = "User's email address",
        example = "john.doe@example.com"
    )
    private String email;

    @Schema(
        description = "JWT token to be used for subsequent API calls. " +
                     "Include this token in the Authorization header as 'Bearer <token>'",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String token;
} 