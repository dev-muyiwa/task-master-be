package com.devmuyiwa.taskmanager.repository;

import com.devmuyiwa.taskmanager.model.TeamMember;
import com.devmuyiwa.taskmanager.model.User;
import com.devmuyiwa.taskmanager.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {
    List<TeamMember> findByWorkspaceMemberUserAndWorkspaceMemberWorkspace(User user, Workspace workspace);
} 