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
            
            // Find the "Submit Leave Request" task that was just created for the current user
            List<Task> submitTasks = taskService.createTaskQuery()
                    .processInstanceId(processInstance.getId())
                    .taskAssignee(loggedInUser)
                    .taskName("Submit Leave Request")
                    .list();
            
            if (submitTasks.size() > 0) {
                Task submitTask = submitTasks.get(0);
                System.out.println("Found submit task: " + submitTask.getId() + " for user: " + loggedInUser);
                
                // Complete the submit task immediately with the provided leave request data
                Map<String, Object> taskVariables = new HashMap<>();
                taskVariables.putAll(leaveRequest); // Include all form data as task variables
                
                taskService.complete(submitTask.getId(), taskVariables);
                System.out.println("Completed submit task: " + submitTask.getId());
            } else {
                System.out.println("No submit task found for user: " + loggedInUser);
            }
            
            // Check if tasks were created after completing submit task
            List<Task> allTasks = taskService.createTaskQuery()
                    .processInstanceId(processInstance.getId())
                    .list();
            System.out.println("Tasks after completing submit task: " + allTasks.size());
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
            
            System.out.println("Getting running processes for user: " + loggedInUser);
            
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
                        
                        // Check if user has pending tasks for this process
                        List<Task> userTasks = taskService.createTaskQuery()
                                .processInstanceId(process.getId())
                                .taskAssignee(loggedInUser)
                                .list();
                        
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
                        
                        // Add status information
                        if (userTasks.size() > 0) {
                            processInfo.put("status", "Pending Action");
                            processInfo.put("currentTask", userTasks.get(0).getName());
                        } else {
                            processInfo.put("status", "Waiting Others");
                            
                            // Find who is currently handling the process
                            List<Task> allTasks = taskService.createTaskQuery()
                                    .processInstanceId(process.getId())
                                    .list();
                            
                            if (allTasks.size() > 0) {
                                processInfo.put("currentTask", allTasks.get(0).getName());
                                processInfo.put("currentAssignee", allTasks.get(0).getAssignee());
                            }
                        }
                        
                        System.out.println("Process " + process.getId() + " status: " + processInfo.get("status"));
                        
                        return processInfo;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(processList);
        } catch (Exception e) {
            System.out.println("Error getting running processes: " + e.getMessage());
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    @GetMapping("/completed-tasks")
    public ResponseEntity<List<Map<String, Object>>> getCompletedTasks(Principal principal) {
        try {
            String loggedInUser = principal.getName();
            
            System.out.println("Getting completed tasks for user: " + loggedInUser);
            
            // Get completed tasks for the logged-in user from history
            List<org.flowable.task.api.history.HistoricTaskInstance> completedTasks = historyService
                    .createHistoricTaskInstanceQuery()
                    .taskAssignee(loggedInUser)
                    .finished()
                    .orderByHistoricTaskInstanceEndTime()
                    .desc()
                    .list();
            
            System.out.println("Completed tasks for " + loggedInUser + ": " + completedTasks.size());
            
            // Debug: Show all completed tasks
//            for (org.flowable.task.api.history.HistoricTaskInstance task : completedTasks) {
//                System.out.println("Completed task: " + task.getId() + " - " + task.getName() +
//                                 " - Assignee: " + task.getAssignee() +
//                                 " - End Time: " + task.getEndTime());
//            }
            
            List<Map<String, Object>> taskList = completedTasks.stream()
                    .map(task -> {
                        Map<String, Object> taskInfo = new HashMap<>();
                        taskInfo.put("id", task.getId());
                        taskInfo.put("name", task.getName());
                        taskInfo.put("description", task.getDescription());
                        taskInfo.put("startTime", task.getStartTime());
                        taskInfo.put("endTime", task.getEndTime());
                        taskInfo.put("duration", task.getDurationInMillis());
                        taskInfo.put("processInstanceId", task.getProcessInstanceId());
                        
                        // Get process variables
                        List<HistoricVariableInstance> variables = historyService
                                .createHistoricVariableInstanceQuery()
                                .processInstanceId(task.getProcessInstanceId())
                                .list();
                        
                        Map<String, Object> variableMap = variables.stream()
                                .collect(Collectors.toMap(
                                    HistoricVariableInstance::getVariableName,
                                    HistoricVariableInstance::getValue
                                ));
                        taskInfo.put("variables", variableMap);
                        
                        // Add process status for better context
                        HistoricProcessInstance process = historyService
                                .createHistoricProcessInstanceQuery()
                                .processInstanceId(task.getProcessInstanceId())
                                .singleResult();

                        if (process != null) {
                            taskInfo.put("processDefinitionName", process.getProcessDefinitionName());
                            taskInfo.put("processStartTime", process.getStartTime());
                            taskInfo.put("processEndTime", process.getEndTime());
                            taskInfo.put("processFinished", process.getEndTime() != null);

                            // For Submit Leave Request tasks, add additional context
                            if ("Submit Leave Request".equals(task.getName())) {
                                taskInfo.put("isSubmitTask", true);
                                taskInfo.put("action", "submitted"); // User submitted a leave request
                                
                                // Add process final status if process is finished
                                if (process.getEndTime() != null) {
                                    Boolean approved = (Boolean) variableMap.get("approved");
                                    if (approved != null) {
                                        taskInfo.put("finalResult", approved ? "approved" : "rejected");
                                    }
                                }
                            } else if ("Manager Review".equals(task.getName())) {
                                taskInfo.put("isReviewTask", true);
                                taskInfo.put("action", variableMap.get("approved") != null ? 
                                    (Boolean.TRUE.equals(variableMap.get("approved")) ? "approved" : "rejected") : "reviewed");
                            }
                        }
                        
                        return taskInfo;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(taskList);
        } catch (Exception e) {
            System.out.println("Error getting completed tasks: " + e.getMessage());
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    @GetMapping("/process-progress/{processInstanceId}")
    public ResponseEntity<Map<String, Object>> getProcessProgress(
            @PathVariable String processInstanceId,
            Principal principal) {
        try {
            String loggedInUser = principal.getName();
            
            System.out.println("Getting process progress for: " + processInstanceId + " by user: " + loggedInUser);
            
            // Check if user is involved in this process
            HistoricProcessInstance process = historyService
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .involvedUser(loggedInUser)
                    .singleResult();
            
            if (process == null) {
                return ResponseEntity.status(403).build(); // User not involved in this process
            }
            
            Map<String, Object> progressInfo = new HashMap<>();
            progressInfo.put("processInstanceId", processInstanceId);
            progressInfo.put("processDefinitionName", process.getProcessDefinitionName());
            progressInfo.put("startTime", process.getStartTime());
            progressInfo.put("endTime", process.getEndTime());
            progressInfo.put("isFinished", process.getEndTime() != null);
            
            // Get all tasks in this process (completed and active)
            List<org.flowable.task.api.history.HistoricTaskInstance> allTasks = historyService
                    .createHistoricTaskInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .orderByHistoricTaskInstanceStartTime()
                    .asc()
                    .list();
            
            // Get current active tasks
            List<Task> activeTasks = taskService.createTaskQuery()
                    .processInstanceId(processInstanceId)
                    .list();
            
            List<Map<String, Object>> taskProgress = new ArrayList<>();
            
            // Add completed tasks
            for (org.flowable.task.api.history.HistoricTaskInstance task : allTasks) {
                if (task.getEndTime() != null) { // Completed task
                    Map<String, Object> taskInfo = new HashMap<>();
                    taskInfo.put("id", task.getId());
                    taskInfo.put("name", task.getName());
                    taskInfo.put("assignee", task.getAssignee());
                    taskInfo.put("startTime", task.getStartTime());
                    taskInfo.put("endTime", task.getEndTime());
                    taskInfo.put("duration", task.getDurationInMillis());
                    taskInfo.put("status", "completed");
                    taskProgress.add(taskInfo);
                }
            }
            
            // Add active tasks
            for (Task task : activeTasks) {
                Map<String, Object> taskInfo = new HashMap<>();
                taskInfo.put("id", task.getId());
                taskInfo.put("name", task.getName());
                taskInfo.put("assignee", task.getAssignee());
                taskInfo.put("startTime", task.getCreateTime());
                taskInfo.put("endTime", null);
                taskInfo.put("duration", null);
                taskInfo.put("status", "active");
                taskProgress.add(taskInfo);
            }
            
            progressInfo.put("tasks", taskProgress);
            
            // Get process variables
            List<HistoricVariableInstance> variables = historyService
                    .createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .list();
            
            Map<String, Object> variableMap = variables.stream()
                    .collect(Collectors.toMap(
                        HistoricVariableInstance::getVariableName,
                        HistoricVariableInstance::getValue
                    ));
            progressInfo.put("variables", variableMap);
            
            // Calculate progress percentage
            int totalTasks = taskProgress.size();
            long completedTasks = taskProgress.stream()
                    .mapToInt(task -> "completed".equals(task.get("status")) ? 1 : 0)
                    .sum();
            
            if (totalTasks > 0) {
                double progressPercentage = (double) completedTasks / totalTasks * 100;
                progressInfo.put("progressPercentage", Math.round(progressPercentage));
            } else {
                progressInfo.put("progressPercentage", 0);
            }
            
            return ResponseEntity.ok(progressInfo);
        } catch (Exception e) {
            System.out.println("Error getting process progress: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get process progress: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}