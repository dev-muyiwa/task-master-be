package com.devmuyiwa.taskmanager.dto;

import com.devmuyiwa.taskmanager.model.ProjectStatus;
import lombok.Data;

@Data
public class ProjectFilterDTO {
    private String title;
    private ProjectStatus status;
} 