package com.devmuyiwa.taskmanager.repository;

import com.devmuyiwa.taskmanager.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @Query("SELECT DISTINCT p FROM Project p " +
           "JOIN p.workspace w " +
           "JOIN w.members wm " +
           "WHERE wm.user.id = :userId")
    Page<Project> findByTeamMembersUserId(@Param("userId") UUID userId, Pageable pageable);
}
