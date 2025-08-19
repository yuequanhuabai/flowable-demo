package com.example.flowabledemo;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProcessController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @PostMapping("/start-process")
    public String startProcessInstance() {
        runtimeService.startProcessInstanceByKey("oneTaskProcess");
        return "Process started!";
    }

    @GetMapping("/tasks")
    public List<String> getTasks() {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee("demoUser").list();
        return tasks.stream()
                .map(task -> "Task ID: " + task.getId() + ", Task Name: " + task.getName())
                .collect(Collectors.toList());
    }

    @PostMapping("/complete-task/{taskId}")
    public String completeTask(@PathVariable String taskId) {
        taskService.complete(taskId);
        return "Task " + taskId + " completed!";
    }
}
