package com.devmuyiwa.taskmanager.service;

import com.devmuyiwa.taskmanager.dto.RegisterRequest;
import com.devmuyiwa.taskmanager.dto.UserWorkspaceDetails;
import com.devmuyiwa.taskmanager.dto.WorkspaceResponse;
import com.devmuyiwa.taskmanager.enums.WorkspaceRole;
import com.devmuyiwa.taskmanager.exception.ResourceNotFoundException;
import com.devmuyiwa.taskmanager.model.User;
import com.devmuyiwa.taskmanager.model.Workspace;
import com.devmuyiwa.taskmanager.model.WorkspaceMember;
import com.devmuyiwa.taskmanager.repository.WorkspaceMemberRepository;
import com.devmuyiwa.taskmanager.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public WorkspaceMember createWorkspaceWithOwner(User user, RegisterRequest request) {
        Workspace workspace = Workspace.builder()
                .name(request.getWorkspaceName())
                .description("Workspace created by " + user.getEmail())
                .build();

        WorkspaceMember workspaceMember = WorkspaceMember.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(WorkspaceRole.OWNER)
                .workspace(workspace)
                .user(user)
                .build();

        workspace.setMembers(java.util.List.of(workspaceMember));
        workspaceRepository.save(workspace);

        return workspaceMember;
    }

    @Transactional
    public WorkspaceMember addUserToWorkspace(User user, Workspace workspace, String firstName, String lastName, WorkspaceRole role) {
        WorkspaceMember workspaceMember = WorkspaceMember.builder()
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .workspace(workspace)
                .user(user)
                .build();

        workspace.getMembers().add(workspaceMember);
        workspaceRepository.save(workspace);
        return workspaceMember;
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponse> getUserWorkspaces(User user) {
        List<WorkspaceMember> memberships = workspaceMemberRepository.findAllByUserId(user.getId());
        return memberships.stream()
                .map(membership -> {
                    Workspace workspace = membership.getWorkspace();
                    return WorkspaceResponse.builder()
                            .id(workspace.getId())
                            .name(workspace.getName())
                            .description(workspace.getDescription())
                            .role(membership.getRole())
                            .joinedAt(membership.getJoinedAt())
                            .createdAt(workspace.getCreatedAt())
                            .updatedAt(workspace.getUpdatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserWorkspaceDetails getUserWorkspaceDetails(User user, UUID workspaceId) {
        WorkspaceMember membership = workspaceMemberRepository.findAllByUserId(user.getId()).stream()
                .filter(m -> m.getWorkspace().getId().equals(workspaceId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User is not a member of this workspace"));

        Workspace workspace = membership.getWorkspace();
        return UserWorkspaceDetails.builder()
                .userId(user.getId())
                .firstName(membership.getFirstName())
                .lastName(membership.getLastName())
                .avatar(membership.getAvatar())
                .role(membership.getRole())
                .email(user.getEmail())
                .userCreatedAt(user.getCreatedAt())
                .userUpdatedAt(user.getUpdatedAt())
                .workspaceId(workspace.getId())
                .workspaceName(workspace.getName())
                .joinedAt(membership.getJoinedAt())
                .build();
    }
} 