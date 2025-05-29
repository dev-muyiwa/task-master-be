package com.devmuyiwa.taskmanager.repository;

import com.devmuyiwa.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    @Query("SELECT DISTINCT t FROM Task t " +
           "LEFT JOIN t.assignee a " +
           "LEFT JOIN t.createdBy c " +
           "WHERE (a.workspaceMember.user.id = :userId OR c.workspaceMember.user.id = :userId)")
    List<Task> findByUserId(@Param("userId") UUID userId);
}
