package com.devmuyiwa.taskmanager.service;

import com.devmuyiwa.taskmanager.dto.ProjectDetails;
import com.devmuyiwa.taskmanager.dto.ProjectFilterDTO;
import com.devmuyiwa.taskmanager.dto.TeamDTO;
import com.devmuyiwa.taskmanager.dto.TeamMemberDTO;
import com.devmuyiwa.taskmanager.exception.AuthException;
import com.devmuyiwa.taskmanager.exception.ResourceNotFoundException;
import com.devmuyiwa.taskmanager.model.Project;
import com.devmuyiwa.taskmanager.enums.TaskStatus;
import com.devmuyiwa.taskmanager.enums.WorkspaceRole;
import com.devmuyiwa.taskmanager.model.Team;
import com.devmuyiwa.taskmanager.model.TeamMember;
import com.devmuyiwa.taskmanager.model.User;
import com.devmuyiwa.taskmanager.model.Workspace;
import com.devmuyiwa.taskmanager.model.WorkspaceMember;
import com.devmuyiwa.taskmanager.repository.ProjectRepository;
import com.devmuyiwa.taskmanager.repository.TeamMemberRepository;
import com.devmuyiwa.taskmanager.repository.UserRepository;
import com.devmuyiwa.taskmanager.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional(readOnly = true)
    public List<ProjectDetails> getUserProjects(UUID userId, UUID workspaceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", workspaceId));

        // Get user's workspace membership to check role
        WorkspaceMember workspaceMember = user.getWorkspaceMemberships().stream()
                .filter(m -> m.getWorkspace().getId().equals(workspaceId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User is not a member of this workspace"));

        // If user is owner or admin, return all projects in the workspace
        if (workspaceMember.getRole() == WorkspaceRole.OWNER || workspaceMember.getRole() == WorkspaceRole.ADMIN) {
            return workspace.getProjects().stream()
                    .map(this::mapToProjectDetails)
                    .toList();
        }

        // For regular members, return only projects they have access to through their tasks
        return projectRepository.findByTeamMembersUserId(userId, null)
                .stream()
                .filter(project -> project.getWorkspace().getId().equals(workspaceId))
                .map(this::mapToProjectDetails)
                .toList();
    }

    private ProjectDetails mapToProjectDetails(Project project) {
        long totalTasks = project.getTasks().size();
        long completedTasks = project.getTasks().stream()
                .filter(task -> TaskStatus.DONE.equals(task.getStatus()))
                .count();

        return ProjectDetails.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .status(project.getStatus())
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

//    @Transactional(readOnly = true)
//    public List<ProjectDetails> getProjectsByUserId(UUID userId, ProjectFilterDTO filter) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new AuthException("User not found"));
//
//        return user.getTeamMemberships().stream()
//                .flatMap(teamMember -> teamMember.getTeam().getProjects().stream())
//                .filter(project -> matchesFilter(project, filter))
//                .map(this::mapToProjectDTO)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public Page<ProjectDetails> getProjectsByUserId(UUID userId, ProjectFilterDTO filter, Pageable pageable) {
//        if (!userRepository.existsById(userId)) {
//            throw new AuthException("User not found");
//        }
//
//        Page<Project> projectPage = projectRepository.findByTeamMembersUserId(userId, pageable);
//        List<ProjectDetails> filteredProjects = projectPage.getContent().stream()
//                .filter(project -> matchesFilter(project, filter))
//                .map(this::mapToProjectDTOWithProgress)
//                .collect(Collectors.toList());
//
//        return new PageImpl<>(
//            filteredProjects,
//            pageable,
//            projectPage.getTotalElements()
//        );
//    }

    private boolean matchesFilter(Project project, ProjectFilterDTO filter) {
        if (filter == null) {
            return true;
        }

        boolean matchesTitle = true;
        if (StringUtils.hasText(filter.getTitle())) {
            matchesTitle = project.getTitle().toLowerCase()
                    .contains(filter.getTitle().toLowerCase());
        }

        boolean matchesStatus = true;
        if (filter.getStatus() != null) {
            matchesStatus = project.getStatus() == filter.getStatus();
        }

        return matchesTitle && matchesStatus;
    }

//    private ProjectDetails mapToProjectDTOWithProgress(Project project) {
//        long totalTasks = project.getTasks().size();
//        long completedTasks = project.getTasks().stream()
//                .filter(task -> task.getStatus() == TaskStatus.DONE)
//                .count();
//
//        double progress = totalTasks > 0 ? (double) completedTasks / totalTasks : 0.0;
//
//        TeamDTO teamDTO = mapToTeamDTO(project.getTeam());
//
//        return ProjectDetails.builder()
//                .id(project.getId())
//                .title(project.getTitle())
//                .description(project.getDescription())
//                .startDate(project.getStartDate())
//                .endDate(project.getEndDate())
//                .team(teamDTO)
//                .status(project.getStatus())
//                .progress(progress)
//                .totalTasks(totalTasks)
//                .completedTasks(completedTasks)
//                .createdAt(project.getCreatedAt())
//                .updatedAt(project.getUpdatedAt())
//                .build();
//    }

//    private TeamDTO mapToTeamDTO(Team team) {
//        List<TeamMemberDTO> members = team.getTeamMembers().stream()
//                .map(teamMember -> {
//                    User user = teamMember.getUser();
//                    return TeamMemberDTO.builder()
//                            .id(user.getId())
//                            .firstName(user.getFirstName())
//                            .lastName(user.getLastName())
//                            .avatar(user.getAvatar())
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        return TeamDTO.builder()
//                .id(team.getId())
//                .name(team.getName())
//                .description(team.getDescription())
//                .members(members)
//                .build();
//    }

//    private ProjectDetails mapToProjectDTO(Project project) {
//        return ProjectDetails.builder()
//                .id(project.getId())
//                .title(project.getTitle())
//                .description(project.getDescription())
//                .startDate(project.getStartDate())
//                .endDate(project.getEndDate())
//                .teamId(project.getTeam().getId())
//                .teamName(project.getTeam().getName())
//                .status(project.getStatus())
//                .createdAt(project.getCreatedAt())
//                .updatedAt(project.getUpdatedAt())
//                .build();
//    }
}
