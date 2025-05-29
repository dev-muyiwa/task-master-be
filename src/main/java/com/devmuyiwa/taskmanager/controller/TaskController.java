package com.devmuyiwa.taskmanager.controller;

import com.devmuyiwa.taskmanager.dto.TaskRequest;
import com.devmuyiwa.taskmanager.model.Task;
import com.devmuyiwa.taskmanager.security.CustomUserDetails;
import com.devmuyiwa.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getTasks(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(taskService.getTasksByUserId(userDetails.getId()));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TaskRequest taskRequest) {
        return ResponseEntity.ok(taskService.createTask(userDetails.getId(), taskRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @Valid @RequestBody TaskRequest taskRequest) {
        return ResponseEntity.ok(taskService.updateTask(userDetails.getId(), id, taskRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id) {
        taskService.deleteTask(userDetails.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
