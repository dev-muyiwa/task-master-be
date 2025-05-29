package com.devmuyiwa.taskmanager.service;

import com.devmuyiwa.taskmanager.dto.TeamMemberDetails;
import com.devmuyiwa.taskmanager.dto.TeamResponse;
import com.devmuyiwa.taskmanager.enums.TaskStatus;
import com.devmuyiwa.taskmanager.enums.WorkspaceRole;
import com.devmuyiwa.taskmanager.exception.ResourceNotFoundException;
import com.devmuyiwa.taskmanager.model.Task;
import com.devmuyiwa.taskmanager.model.Team;
import com.devmuyiwa.taskmanager.model.TeamMember;
import com.devmuyiwa.taskmanager.model.User;
import com.devmuyiwa.taskmanager.model.Workspace;
import com.devmuyiwa.taskmanager.model.WorkspaceMember;
import com.devmuyiwa.taskmanager.repository.TeamMemberRepository;
import com.devmuyiwa.taskmanager.repository.TeamRepository;
import com.devmuyiwa.taskmanager.repository.UserRepository;
import com.devmuyiwa.taskmanager.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;

    @Transactional(readOnly = true)
    public List<TeamResponse> getUserTeams(UUID userId, UUID workspaceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", workspaceId));

        // Get user's workspace membership to check role
        WorkspaceMember workspaceMember = user.getWorkspaceMemberships().stream()
                .filter(m -> m.getWorkspace().getId().equals(workspaceId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User is not a member of this workspace"));

        // If user is owner or admin, return all teams in the workspace
        if (workspaceMember.getRole() == WorkspaceRole.OWNER || workspaceMember.getRole() == WorkspaceRole.ADMIN) {
            return workspace.getTeams().stream()
                    .map(team -> TeamResponse.builder()
                            .id(team.getId())
                            .name(team.getName())
                            .description(team.getDescription())
                            .workspaceId(workspace.getId())
                            .createdAt(team.getCreatedAt())
                            .updatedAt(team.getUpdatedAt())
                            .build())
                    .toList();
        }

        // For regular members, return only teams they are part of
        List<TeamMember> teamMembers = teamMemberRepository.findByWorkspaceMemberUserAndWorkspaceMemberWorkspace(user, workspace);
        return teamMembers.stream()
                .map(teamMember -> {
                    Team team = teamMember.getTeam();
                    return TeamResponse.builder()
                            .id(team.getId())
                            .name(team.getName())
                            .description(team.getDescription())
                            .workspaceId(workspace.getId())
                            .createdAt(team.getCreatedAt())
                            .updatedAt(team.getUpdatedAt())
                            .build();
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TeamMemberDetails> getTeamMembers(UUID teamId, UUID workspaceId, UUID userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));

        if (!team.getWorkspace().getId().equals(workspaceId)) {
            throw new ResourceNotFoundException("Team does not belong to the specified workspace");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Get user's workspace membership to check role
        WorkspaceMember workspaceMember = user.getWorkspaceMemberships().stream()
                .filter(m -> m.getWorkspace().getId().equals(workspaceId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User is not a member of this workspace"));

        // Check if user has access to the team
        boolean hasAccess = false;
        if (workspaceMember.getRole() == WorkspaceRole.OWNER || workspaceMember.getRole() == WorkspaceRole.ADMIN) {
            hasAccess = true;
        } else {
            hasAccess = team.getTeamMembers().stream()
                    .anyMatch(member -> member.getWorkspaceMember().getUser().getId().equals(userId));
        }

        if (!hasAccess) {
            throw new ResourceNotFoundException("User does not have access to this team");
        }

        return team.getTeamMembers().stream()
                .map(teamMember -> {
                    WorkspaceMember memberWorkspaceMember = teamMember.getWorkspaceMember();
                    User memberUser = memberWorkspaceMember.getUser();
                    
                    int completedTasks = (int) teamMember.getAssignedTasks().stream()
                            .filter(task -> TaskStatus.DONE.equals(task.getStatus()))
                            .count();
                    
                    int assignedTasks = teamMember.getAssignedTasks().size();

                    return TeamMemberDetails.builder()
                            .id(teamMember.getId())
                            .firstName(memberWorkspaceMember.getFirstName())
                            .lastName(memberWorkspaceMember.getLastName())
                            .email(memberUser.getEmail())
                            .avatar(memberWorkspaceMember.getAvatar())
                            .role(teamMember.getRole())
                            .joinedAt(memberWorkspaceMember.getJoinedAt())
                            .completedTasks(completedTasks)
                            .assignedTasks(assignedTasks)
                            .build();
                })
                .toList();
    }
}
