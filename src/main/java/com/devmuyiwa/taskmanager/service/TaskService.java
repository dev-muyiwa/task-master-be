package com.devmuyiwa.taskmanager.service;

import com.devmuyiwa.taskmanager.dto.TaskRequest;
import com.devmuyiwa.taskmanager.enums.TaskStatus;
import com.devmuyiwa.taskmanager.common.exception.ResourceNotFoundException;
import com.devmuyiwa.taskmanager.model.Task;
import com.devmuyiwa.taskmanager.model.User;
import com.devmuyiwa.taskmanager.repository.TaskRepository;
import com.devmuyiwa.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public List<Task> getTasksByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return taskRepository.findByUserId(userId);
    }

    public Task createTask(UUID userId, TaskRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
//                .assignee()
                .build();

        return taskRepository.save(task);
    }

    public Task updateTask(UUID userId, UUID taskId, TaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

//        if (!task.getUser().getId().equals(userId)) {
//            throw new AccessDeniedException("You don't have permission to update this task");
//        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        return taskRepository.save(task);
    }

    public void deleteTask(UUID userId, UUID taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

//        if (!task.getUser().getId().equals(userId)) {
//            throw new AccessDeniedException("You don't have permission to delete this task");
//        }

        taskRepository.delete(task);
    }
}
