package com.example.flowabledemo;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProcessController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @PostMapping("/start-process")
    public String startProcessInstance(Principal principal) {
        // Get the username of the logged-in user
        String loggedInUser = principal.getName();

        // Use the username as the assignee for the first task
        Map<String, Object> variables = Collections.singletonMap("assignee", loggedInUser);
        runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);

        return "Process started for user " + loggedInUser;
    }

    @GetMapping("/tasks")
    public List<Map<String, String>> getTasks(Principal principal) {
        // Get tasks for the logged-in user
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(principal.getName()).list();
        return tasks.stream()
                .map(task -> Map.of(
                        "id", task.getId(),
                        "name", task.getName()
                ))
                .collect(Collectors.toList());
    }

    @PostMapping("/complete-task/{taskId}")
    public String completeTask(@PathVariable String taskId, @RequestBody(required = false) Map<String, Object> variables) {
        // Check if task exists
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return "Error: Task not found";
        }

        // Complete the task with variables
        taskService.complete(taskId, variables);
        return "Task " + taskId + " completed!";
    }

    // A simple endpoint to get the current user's info
    @GetMapping("/me")
    public ResponseEntity<UserDetails> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userDetails);
    }
}