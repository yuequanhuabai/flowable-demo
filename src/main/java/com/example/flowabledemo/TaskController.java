package com.example.flowabledemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.flowable.task.api.Task;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.history.HistoricProcessInstance;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务管理控制器 - 专注于实际业务功能
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private FlowableApiGuide flowableApiGuide;
    
    // ==================== 代办任务管理 ====================
    
    /**
     * 获取我的代办任务列表
     */
    @GetMapping("/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingTasks(Principal principal) {
        try {
            String username = principal.getName();
            
            List<Task> tasks = flowableApiGuide.getPendingTasks(username);
            
            List<Map<String, Object>> taskList = tasks.stream().map(task -> {
                Map<String, Object> taskInfo = new HashMap<>();
                taskInfo.put("taskId", task.getId());
                taskInfo.put("taskName", task.getName());
                taskInfo.put("processInstanceId", task.getProcessInstanceId());
                taskInfo.put("createTime", task.getCreateTime());
                
                // 获取流程变量（业务数据）
                Map<String, Object> variables = flowableApiGuide.getTaskDetails(task.getId());
                taskInfo.put("businessData", variables.get("variables"));
                
                return taskInfo;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(taskList);
            
        } catch (Exception e) {
            System.out.println("获取代办任务失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    /**
     * 获取任务详情
     */
    @GetMapping("/{taskId}/details")
    public ResponseEntity<Map<String, Object>> getTaskDetails(@PathVariable String taskId, Principal principal) {
        try {
            Map<String, Object> taskDetails = flowableApiGuide.getTaskDetails(taskId);
            
            // 检查任务是否属于当前用户
            String assignee = (String) taskDetails.get("assignee");
            if (!principal.getName().equals(assignee)) {
                return ResponseEntity.status(403).body(Map.of("error", "无权限访问此任务"));
            }
            
            return ResponseEntity.ok(taskDetails);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "获取任务详情失败: " + e.getMessage()));
        }
    }
    
    // ==================== 审批操作 ====================
    
    /**
     * 同意任务
     */
    @PostMapping("/{taskId}/approve")
    public ResponseEntity<Map<String, Object>> approveTask(
            @PathVariable String taskId,
            @RequestBody Map<String, String> requestBody,
            Principal principal) {
        try {
            String comment = requestBody.getOrDefault("comment", "同意");
            
            flowableApiGuide.approveTask(taskId, principal.getName(), comment);
            
            return ResponseEntity.ok(Map.of("message", "审批成功", "action", "approved"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "审批失败: " + e.getMessage()));
        }
    }
    
    /**
     * 驳回任务
     */
    @PostMapping("/{taskId}/reject")
    public ResponseEntity<Map<String, Object>> rejectTask(
            @PathVariable String taskId,
            @RequestBody Map<String, String> requestBody,
            Principal principal) {
        try {
            String comment = requestBody.getOrDefault("comment", "驳回");
            
            flowableApiGuide.rejectTask(taskId, principal.getName(), comment);
            
            return ResponseEntity.ok(Map.of("message", "驳回成功", "action", "rejected"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "驳回失败: " + e.getMessage()));
        }
    }
    
    // ==================== 流程状态查询 ====================
    
    /**
     * 获取我发起的运行中流程
     */
    @GetMapping("/my-processes/running")
    public ResponseEntity<List<Map<String, Object>>> getMyRunningProcesses(Principal principal) {
        try {
            String username = principal.getName();
            
            List<ProcessInstance> processes = flowableApiGuide.getRunningProcesses(username);
            
            List<Map<String, Object>> processList = processes.stream().map(process -> {
                Map<String, Object> processInfo = new HashMap<>();
                processInfo.put("processInstanceId", process.getId());
                processInfo.put("processDefinitionName", process.getProcessDefinitionName());
                processInfo.put("startTime", process.getStartTime());
                processInfo.put("businessKey", process.getBusinessKey());
                
                // 获取流程变量
                Map<String, Object> variables = flowableApiGuide.getProcessVariables(process.getId());
                processInfo.put("businessData", variables);
                
                // 获取当前节点
                List<String> currentActivities = flowableApiGuide.getCurrentActivities(process.getId());
                processInfo.put("currentActivities", currentActivities);
                
                return processInfo;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(processList);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    /**
     * 获取我发起的已完成流程
     */
    @GetMapping("/my-processes/completed")
    public ResponseEntity<List<Map<String, Object>>> getMyCompletedProcesses(Principal principal) {
        try {
            String username = principal.getName();
            
            List<HistoricProcessInstance> processes = flowableApiGuide.getCompletedProcesses(username);
            
            List<Map<String, Object>> processList = processes.stream().map(process -> {
                Map<String, Object> processInfo = new HashMap<>();
                processInfo.put("processInstanceId", process.getId());
                processInfo.put("processDefinitionName", process.getProcessDefinitionName());
                processInfo.put("startTime", process.getStartTime());
                processInfo.put("endTime", process.getEndTime());
                processInfo.put("duration", process.getDurationInMillis());
                processInfo.put("businessKey", process.getBusinessKey());
                
                return processInfo;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(processList);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // ==================== 流程操作 ====================
    
    /**
     * 发起新流程
     */
    @PostMapping("/start-process")
    public ResponseEntity<Map<String, Object>> startProcess(
            @RequestBody Map<String, Object> requestData,
            Principal principal) {
        try {
            String processInstanceId = flowableApiGuide.startLeaveProcess(principal.getName(), requestData);
            
            return ResponseEntity.ok(Map.of(
                "message", "流程发起成功",
                "processInstanceId", processInstanceId,
                "requester", principal.getName()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "流程发起失败: " + e.getMessage()));
        }
    }
    
    /**
     * 检查流程状态
     */
    @GetMapping("/process/{processInstanceId}/status")
    public ResponseEntity<Map<String, Object>> getProcessStatus(@PathVariable String processInstanceId) {
        try {
            boolean isFinished = flowableApiGuide.isProcessFinished(processInstanceId);
            
            Map<String, Object> status = new HashMap<>();
            status.put("processInstanceId", processInstanceId);
            status.put("isFinished", isFinished);
            status.put("status", isFinished ? "已完结" : "运行中");
            
            if (!isFinished) {
                List<String> currentActivities = flowableApiGuide.getCurrentActivities(processInstanceId);
                status.put("currentActivities", currentActivities);
            }
            
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "查询流程状态失败: " + e.getMessage()));
        }
    }
}