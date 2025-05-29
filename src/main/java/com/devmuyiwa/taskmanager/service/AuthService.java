package com.devmuyiwa.taskmanager.service;

import com.devmuyiwa.taskmanager.dto.LoginRequest;
import com.devmuyiwa.taskmanager.dto.RegisterRequest;
import com.devmuyiwa.taskmanager.dto.AuthResponse;
import com.devmuyiwa.taskmanager.exception.AuthException;
import com.devmuyiwa.taskmanager.model.User;
import com.devmuyiwa.taskmanager.model.WorkspaceMember;
import com.devmuyiwa.taskmanager.security.CustomUserDetails;
import com.devmuyiwa.taskmanager.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final WorkspaceService workspaceService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userService.findByEmail(request.getEmail()).isPresent()) {
            throw new AuthException.EmailAlreadyExistsException(request.getEmail());
        }

        User user = userService.createUser(request);
        workspaceService.createWorkspaceWithOwner(user, request);

        String token = jwtUtil.generateToken(new CustomUserDetails(user));

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .token(token)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.user();
            String token = jwtUtil.generateToken(userDetails);

            return AuthResponse.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .token(token)
                    .build();
        } catch (Exception e) {
            throw new AuthException("Invalid email or password");
        }
    }
}
