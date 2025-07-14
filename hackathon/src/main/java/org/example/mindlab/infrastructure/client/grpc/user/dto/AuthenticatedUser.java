package org.example.mindlab.infrastructure.client.grpc.user.dto;

import hackathon.auth.grpc.AuthResponse;
import lombok.Builder;

@Builder
public record AuthenticatedUser(Long userId, String email, String role) {

    static public AuthenticatedUser from(AuthResponse response) {
        return AuthenticatedUser.builder()
            .userId(response.getUserId())
            .email(response.getEmail())
            .role(response.getRole())
            .build();
    }
}

