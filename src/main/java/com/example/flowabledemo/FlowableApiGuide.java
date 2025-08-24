package com.example.flowabledemo;

import org.flowable.engine.*;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Flowable API 使用指南 - 专注基本功能
 * 
 * 🎯 核心功能：
 * 1. 发起流程
 * 2. 审批流程（同意/驳回）
 * 3. 流程完结
 * 4. 代办任务管理
 * 5. 业务参数传递
 */
@Service
public class FlowableApiGuide {
    
    @Autowired
    private RuntimeService runtimeService;    // 流程运行时服务
    
    @Autowired
    private TaskService taskService;          // 任务服务
    
    @Autowired
    private HistoryService historyService;    // 历史服务
    
    // ==================== 1. 发起流程 ====================
    
    /**
     * 发起请假流程
     * @param requester 发起人
     * @param leaveData 请假数据
     * @return 流程实例ID
     */
    public String startLeaveProcess(String requester, Map<String, Object> leaveData) {
        // 准备流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("requester", requester);           // 发起人
        variables.put("leaveType", leaveData.get("leaveType"));
        variables.put("startDate", leaveData.get("startDate"));
        variables.put("endDate", leaveData.get("endDate"));
        variables.put("reason", leaveData.get("reason"));
        variables.put("days", leaveData.get("days"));
        
        // 🎯 核心API：启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
            "simpleLeaveProcess",  // 流程定义Key (对应BPMN文件中的process id)
            variables              // 流程变量
        );
        
        return processInstance.getId();
    }
    
    // ==================== 2. 查询代办任务 ====================
    
    /**
     * 获取用户的代办任务
     * @param assignee 任务分配人
     * @return 任务列表
     */
    public List<Task> getPendingTasks(String assignee) {
        // 🎯 核心API：查询任务
        return taskService.createTaskQuery()
                .taskAssignee(assignee)           // 分配给特定用户
                .orderByTaskCreateTime().desc()   // 按创建时间倒序
                .list();                          // 获取列表
    }
    
    /**
     * 获取任务详情（包含流程变量）
     * @param taskId 任务ID
     * @return 任务信息和变量
     */
    public Map<String, Object> getTaskDetails(String taskId) {
        // 获取任务基本信息
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        
        // 获取流程变量
        Map<String, Object> variables = taskService.getVariables(taskId);
        
        // 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", task.getId());
        result.put("taskName", task.getName());
        result.put("assignee", task.getAssignee());
        result.put("createTime", task.getCreateTime());
        result.put("processInstanceId", task.getProcessInstanceId());
        result.put("variables", variables);  // 包含业务数据
        
        return result;
    }
    
    // ==================== 3. 审批流程 ====================
    
    /**
     * 完成审批任务（同意）
     * @param taskId 任务ID
     * @param approver 审批人
     * @param comment 审批意见
     */
    public void approveTask(String taskId, String approver, String comment) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", true);          // 审批结果
        variables.put("approver", approver);      // 审批人
        variables.put("approveComment", comment); // 审批意见
        variables.put("approveTime", new java.util.Date()); // 审批时间
        
        // 🎯 核心API：完成任务
        taskService.complete(taskId, variables);
    }
    
    /**
     * 完成审批任务（驳回）
     * @param taskId 任务ID
     * @param approver 审批人
     * @param comment 驳回意见
     */
    public void rejectTask(String taskId, String approver, String comment) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", false);         // 审批结果
        variables.put("approver", approver);      // 审批人
        variables.put("rejectComment", comment);  // 驳回意见
        variables.put("rejectTime", new java.util.Date()); // 驳回时间
        
        // 🎯 核心API：完成任务
        taskService.complete(taskId, variables);
    }
    
    // ==================== 4. 流程状态查询 ====================
    
    /**
     * 获取用户发起的运行中流程
     * @param requester 发起人
     * @return 运行中的流程列表
     */
    public List<ProcessInstance> getRunningProcesses(String requester) {
        // 🎯 核心API：查询运行中流程
        return runtimeService.createProcessInstanceQuery()
                .variableValueEquals("requester", requester)  // 按发起人过滤
                .orderByStartTime().desc()                     // 按开始时间倒序
                .list();
    }
    
    /**
     * 获取用户的已完成流程
     * @param requester 发起人
     * @return 已完成流程列表
     */
    public List<HistoricProcessInstance> getCompletedProcesses(String requester) {
        // 🎯 核心API：查询历史流程
        return historyService.createHistoricProcessInstanceQuery()
                .variableValueEquals("requester", requester)  // 按发起人过滤
                .finished()                                    // 只查已完成的
                .orderByProcessInstanceEndTime().desc()        // 按结束时间倒序
                .list();
    }
    
    /**
     * 检查流程是否完结
     * @param processInstanceId 流程实例ID
     * @return true=已完结，false=运行中
     */
    public boolean isProcessFinished(String processInstanceId) {
        // 查询运行中的流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        
        return processInstance == null;  // 如果查不到，说明已完结
    }
    
    // ==================== 5. 业务数据操作 ====================
    
    /**
     * 更新流程变量（业务数据）
     * @param processInstanceId 流程实例ID
     * @param variables 新的变量
     */
    public void updateProcessVariables(String processInstanceId, Map<String, Object> variables) {
        // 🎯 核心API：设置流程变量
        runtimeService.setVariables(processInstanceId, variables);
    }
    
    /**
     * 获取流程变量（业务数据）
     * @param processInstanceId 流程实例ID
     * @return 流程变量
     */
    public Map<String, Object> getProcessVariables(String processInstanceId) {
        // 🎯 核心API：获取流程变量
        return runtimeService.getVariables(processInstanceId);
    }
    
    // ==================== 6. 实用工具方法 ====================
    
    /**
     * 获取流程的当前节点信息
     * @param processInstanceId 流程实例ID
     * @return 当前活动节点列表
     */
    public List<String> getCurrentActivities(String processInstanceId) {
        return runtimeService.getActiveActivityIds(processInstanceId);
    }
    
    /**
     * 强制终止流程（紧急情况使用）
     * @param processInstanceId 流程实例ID
     * @param reason 终止原因
     */
    public void terminateProcess(String processInstanceId, String reason) {
        // 🎯 核心API：删除流程实例
        runtimeService.deleteProcessInstance(processInstanceId, reason);
    }
}