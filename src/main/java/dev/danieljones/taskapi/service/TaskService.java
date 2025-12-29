package dev.danieljones.taskapi.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.danieljones.taskapi.exception.ResourceNotFoundException;
import dev.danieljones.taskapi.model.Task;
import dev.danieljones.taskapi.model.User;
import dev.danieljones.taskapi.model.Status;
import dev.danieljones.taskapi.model.Priority;
import dev.danieljones.taskapi.repository.TaskRepository;

@Service
@Transactional
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    // Create a new task
    public Task createTask(Task task, User user) {
        task.setUser(user);
        
        // Set defaults if not provided
        if (task.getStatus() == null) {
            task.setStatus(Status.TODO);
        }
        if (task.getPriority() == null) {
            task.setPriority(Priority.MEDIUM);
        }
        
        return taskRepository.save(task);
    }
    
    // Get all tasks for a user
    public List<Task> getUserTasks(Long userId) {
        return taskRepository.findByUserId(userId);
    }
    
    // Get all tasks with optional filters
    public List<Task> getUserTasksFiltered(Long userId, Status status, Priority priority) {
        // No filters - return all tasks
        if (status == null && priority == null) {
            return taskRepository.findByUserId(userId);
        }
        
        // Both filters
        if (status != null && priority != null) {
            return taskRepository.findByUserIdAndStatusAndPriority(userId, status, priority);
        }
        
        // Status filter only
        if (status != null) {
            return taskRepository.findByUserIdAndStatus(userId, status);
        }
        
        // Priority filter only
        return taskRepository.findByUserIdAndPriority(userId, priority);
    }
    
    // Get a specific task by ID
    public Task getTaskById(Long taskId, Long userId) {
        return taskRepository.findByIdAndUserId(taskId, userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Task not found with id: " + taskId
            ));
    }
    
    // Get overdue tasks for a user
    public List<Task> getOverdueTasks(Long userId) {
        return taskRepository.findOverdueTasks(userId, LocalDate.now());
    }
    
    // Get tasks due within a date range
    public List<Task> getTasksDueBetween(Long userId, LocalDate startDate, LocalDate endDate) {
        return taskRepository.findTasksDueBetween(userId, startDate, endDate);
    }
    
    // Update an existing task
    public Task updateTask(Long taskId, Task updatedTask, User user) {
        Task existingTask = taskRepository.findByIdAndUserId(taskId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Task not found with id: " + taskId
            ));
        
        // Update fields
        if (updatedTask.getTitle() != null) {
            existingTask.setTitle(updatedTask.getTitle());
        }
        
        if (updatedTask.getDescription() != null) {
            existingTask.setDescription(updatedTask.getDescription());
        }
        
        if (updatedTask.getStatus() != null) {
            existingTask.setStatus(updatedTask.getStatus());
        }
        
        if (updatedTask.getPriority() != null) {
            existingTask.setPriority(updatedTask.getPriority());
        }
        
        if (updatedTask.getDueDate() != null) {
            existingTask.setDueDate(updatedTask.getDueDate());
        }
        
        return taskRepository.save(existingTask);
    }
    
    // Mark task as completed
    public Task completeTask(Long taskId, User user) {
        Task task = getTaskById(taskId, user.getId());
        task.setStatus(Status.COMPLETED);
        return taskRepository.save(task);
    }
    
    // Delete a task
    public void deleteTask(Long taskId, User user) {
        Task task = taskRepository.findByIdAndUserId(taskId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Task not found with id: " + taskId
            ));
        
        taskRepository.delete(task);
    }
    
    // Get task statistics for a user
    public TaskStats getTaskStats(Long userId) {
        long totalTasks = taskRepository.countByUserIdAndStatus(userId, null);
        long todoTasks = taskRepository.countByUserIdAndStatus(userId, Status.TODO);
        long inProgressTasks = taskRepository.countByUserIdAndStatus(userId, Status.IN_PROGRESS);
        long completedTasks = taskRepository.countByUserIdAndStatus(userId, Status.COMPLETED);
        long overdueTasks = taskRepository.findOverdueTasks(userId, LocalDate.now()).size();
        
        return new TaskStats(totalTasks, todoTasks, inProgressTasks, completedTasks, overdueTasks);
    }
    
    // Inner class for stats (you could also make this a separate DTO)
    public static class TaskStats {
        private long totalTasks;
        private long todoTasks;
        private long inProgressTasks;
        private long completedTasks;
        private long overdueTasks;
        
        public TaskStats(long totalTasks, long todoTasks, long inProgressTasks, 
                        long completedTasks, long overdueTasks) {
            this.totalTasks = totalTasks;
            this.todoTasks = todoTasks;
            this.inProgressTasks = inProgressTasks;
            this.completedTasks = completedTasks;
            this.overdueTasks = overdueTasks;
        }
        
        // Getters
        public long getTotalTasks() { return totalTasks; }
        public long getTodoTasks() { return todoTasks; }
        public long getInProgressTasks() { return inProgressTasks; }
        public long getCompletedTasks() { return completedTasks; }
        public long getOverdueTasks() { return overdueTasks; }
    }
}