package dev.danieljones.taskapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dev.danieljones.taskapi.model.Task;

@RestController
public class TaskController {
    @PostMapping("/tasks")
    ResponseEntity<String> addTask(@RequestBody Task task) {
        return ResponseEntity.ok("Task is valid");
    }
}
