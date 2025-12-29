package dev.danieljones.taskapi.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import dev.danieljones.taskapi.dto.TaskRequestDto;
import dev.danieljones.taskapi.dto.TaskResponseDto;
import dev.danieljones.taskapi.model.Task;
import dev.danieljones.taskapi.model.User;
import dev.danieljones.taskapi.model.Status;
import dev.danieljones.taskapi.model.Priority;
import dev.danieljones.taskapi.service.TaskService;
import dev.danieljones.taskapi.service.UserService;
import dev.danieljones.taskapi.security.JwtTokenProvider;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private UserService userService;
    
    // Get all tasks for the authenticated user with optional filters
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false, defaultValue = "false") boolean overdue) {
        
        // Extract username from token
        String token = authHeader.substring(7); // Remove "Bearer "
        User user = userService.findByUsername(
            new JwtTokenProvider().getUsernameFromToken(token)
        );
        
        List<Task> tasks;
        
        if (overdue) {
            tasks = taskService.getOverdueTasks(user.getId());
        } else {
            tasks = taskService.getUserTasksFiltered(user.getId(), status, priority);
        }
        
        List<TaskResponseDto> response = tasks.stream()
            .map(TaskResponseDto::fromEntity)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    // Get a specific task by ID
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7);
        User user = userService.findByUsername(
            new JwtTokenProvider().getUsernameFromToken(token)
        );
        
        Task task = taskService.getTaskById(id, user.getId());
        return ResponseEntity.ok(TaskResponseDto.fromEntity(task));
    }
    
    // Create a new task
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(
            @Valid @RequestBody TaskRequestDto taskDto,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7);
        User user = userService.findByUsername(
            new JwtTokenProvider().getUsernameFromToken(token)
        );
        
        Task task = taskDto.toEntity();
        Task createdTask = taskService.createTask(task, user);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(TaskResponseDto.fromEntity(createdTask));
    }
    
    // Update an existing task
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDto taskDto,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7);
        User user = userService.findByUsername(
            new JwtTokenProvider().getUsernameFromToken(token)
        );
        
        Task updatedTaskData = taskDto.toEntity();
        Task updatedTask = taskService.updateTask(id, updatedTaskData, user);
        
        return ResponseEntity.ok(TaskResponseDto.fromEntity(updatedTask));
    }
    
    // Mark task as completed
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponseDto> completeTask(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7);
        User user = userService.findByUsername(
            new JwtTokenProvider().getUsernameFromToken(token)
        );
        
        Task completedTask = taskService.completeTask(id, user);
        return ResponseEntity.ok(TaskResponseDto.fromEntity(completedTask));
    }
    
    // Delete a task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        User user = userService.findByUsername(
            new JwtTokenProvider().getUsernameFromToken(token)
        );

        taskService.deleteTask(id, user);
        return ResponseEntity.noContent().build();
    }
}