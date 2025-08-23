package com.example.flowabledemo;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProcessController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;
    
    @Autowired
    private HistoryService historyService;

    @PostMapping("/start-leave-request")
    public ResponseEntity<Map<String, Object>> startLeaveRequest(
            @RequestBody Map<String, Object> leaveRequest, 
            Principal principal) {
        try {
            String loggedInUser = principal.getName();
            
            System.out.println("Starting leave request process for user: " + loggedInUser);
            
            // Set up process variables
            Map<String, Object> variables = new HashMap<>();
            variables.put("requester", loggedInUser);
            variables.put("leaveType", leaveRequest.get("leaveType"));
            variables.put("startDate", leaveRequest.get("startDate"));
            variables.put("endDate", leaveRequest.get("endDate"));
            variables.put("reason", leaveRequest.get("reason"));
            variables.put("days", leaveRequest.get("days"));
            
            System.out.println("Process variables: " + variables);
            
            // Start the process
            var processInstance = runtimeService.startProcessInstanceByKey("simpleLeaveProcess", variables);
            
            System.out.println("Process started with ID: " + processInstance.getId());
            
            // Check if tasks were created
            List<Task> allTasks = taskService.createTaskQuery().list();
            System.out.println("Total tasks in system: " + allTasks.size());
            for (Task task : allTasks) {
                System.out.println("Task: " + task.getId() + " - " + task.getName() + " - Assignee: " + task.getAssignee());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("processInstanceId", processInstance.getId());
            response.put("message", "Leave request process started successfully");
            response.put("requester", loggedInUser);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Failed to start process: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to start process: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<Map<String, Object>>> getTasks(Principal principal) {
        try {
            String loggedInUser = principal.getName();
            
            System.out.println("Getting tasks for user: " + loggedInUser);
            
            // First, let's see all tasks in the system
            List<Task> allTasks = taskService.createTaskQuery().list();
            System.out.println("Total tasks in system: " + allTasks.size());
            for (Task task : allTasks) {
                System.out.println("All Task: " + task.getId() + " - " + task.getName() + " - Assignee: " + task.getAssignee());
            }
            
            // Get tasks for the logged-in user
            List<Task> tasks = taskService.createTaskQuery()
                    .taskAssignee(loggedInUser)
                    .orderByTaskCreateTime()
                    .desc()
                    .list();
            
            System.out.println("Tasks assigned to " + loggedInUser + ": " + tasks.size());
            
            List<Map<String, Object>> taskList = tasks.stream()
                    .map(task -> {
                        Map<String, Object> taskInfo = new HashMap<>();
                        taskInfo.put("id", task.getId());
                        taskInfo.put("name", task.getName());
                        taskInfo.put("description", task.getDescription());
                        taskInfo.put("createTime", task.getCreateTime());
                        taskInfo.put("processInstanceId", task.getProcessInstanceId());
                        
                        // Get process variables
                        Map<String, Object> processVariables = taskService.getVariables(task.getId());
                        taskInfo.put("variables", processVariables);
                        
                        return taskInfo;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(taskList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    @PostMapping("/complete-task/{taskId}")
    public ResponseEntity<Map<String, Object>> completeTask(
            @PathVariable String taskId, 
            @RequestBody Map<String, Object> taskData,
            Principal principal) {
        try {
            // Check if task exists and belongs to the user
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .taskAssignee(principal.getName())
                    .singleResult();
            
            if (task == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Task not found or not assigned to you");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Process task data - convert string "true"/"false" to boolean for approved field
            if (taskData.containsKey("approved")) {
                Object approvedValue = taskData.get("approved");
                if (approvedValue instanceof String) {
                    taskData.put("approved", Boolean.parseBoolean((String) approvedValue));
                }
            }
            
            // Complete the task with provided data
            taskService.complete(taskId, taskData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Task completed successfully");
            response.put("taskId", taskId);
            response.put("taskName", task.getName());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to complete task: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/process-history")
    public ResponseEntity<List<Map<String, Object>>> getProcessHistory(Principal principal) {
        try {
            String loggedInUser = principal.getName();
            
            // Get completed process instances where user was involved
            List<HistoricProcessInstance> historicProcesses = historyService
                    .createHistoricProcessInstanceQuery()
                    .involvedUser(loggedInUser)
                    .finished()
                    .orderByProcessInstanceEndTime()
                    .desc()
                    .list();
            
            List<Map<String, Object>> processHistory = historicProcesses.stream()
                    .map(process -> {
                        Map<String, Object> processInfo = new HashMap<>();
                        processInfo.put("processInstanceId", process.getId());
                        processInfo.put("processDefinitionName", process.getProcessDefinitionName());
                        processInfo.put("startTime", process.getStartTime());
                        processInfo.put("endTime", process.getEndTime());
                        processInfo.put("duration", process.getDurationInMillis());
                        
                        // Get process variables
                        List<HistoricVariableInstance> variables = historyService
                                .createHistoricVariableInstanceQuery()
                                .processInstanceId(process.getId())
                                .list();
                        
                        Map<String, Object> variableMap = variables.stream()
                                .collect(Collectors.toMap(
                                    HistoricVariableInstance::getVariableName,
                                    HistoricVariableInstance::getValue
                                ));
                        processInfo.put("variables", variableMap);
                        
                        return processInfo;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(processHistory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    @GetMapping("/running-processes")
    public ResponseEntity<List<Map<String, Object>>> getRunningProcesses(Principal principal) {
        try {
            String loggedInUser = principal.getName();
            
            // Get running process instances where user is involved
            List<HistoricProcessInstance> runningProcesses = historyService
                    .createHistoricProcessInstanceQuery()
                    .involvedUser(loggedInUser)
                    .unfinished()
                    .orderByProcessInstanceStartTime()
                    .desc()
                    .list();
            
            List<Map<String, Object>> processList = runningProcesses.stream()
                    .map(process -> {
                        Map<String, Object> processInfo = new HashMap<>();
                        processInfo.put("processInstanceId", process.getId());
                        processInfo.put("processDefinitionName", process.getProcessDefinitionName());
                        processInfo.put("startTime", process.getStartTime());
                        
                        // Get current variables
                        List<HistoricVariableInstance> variables = historyService
                                .createHistoricVariableInstanceQuery()
                                .processInstanceId(process.getId())
                                .list();
                        
                        Map<String, Object> variableMap = variables.stream()
                                .collect(Collectors.toMap(
                                    HistoricVariableInstance::getVariableName,
                                    HistoricVariableInstance::getValue
                                ));
                        processInfo.put("variables", variableMap);
                        
                        return processInfo;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(processList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

//    @GetMapping("/me")
//    public ResponseEntity<UserDetails> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
//        return ResponseEntity.ok(userDetails);
//    }
}