package com.devmuyiwa.taskmanager.security;

import com.devmuyiwa.taskmanager.model.User;
import com.devmuyiwa.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${swagger.ui.username:admin}")
    private String swaggerUsername;

    @Value("${swagger.ui.password:admin}")
    private String swaggerPassword;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Check if it's a Swagger UI login attempt
        if (swaggerUsername.equals(username)) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(swaggerUsername)
                    .password(passwordEncoder.encode(swaggerPassword))
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_SWAGGER")))
                    .build();
        }

        // Regular application user login
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        return new CustomUserDetails(user);
    }

    public UserDetails loadUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        return new CustomUserDetails(user);
    }
}
