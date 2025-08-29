# 🎯 Flowable API 使用测试指南

> **专注实际使用，掌握核心功能**

## 📋 API 端点总览[OAuth2_Principles_and_Implementation.md](../oauth2-docs/OAuth2_Principles_and_Implementation.md)

### 🚀 流程管理
```http
POST /api/tasks/start-process          # 发起新流程
GET  /api/tasks/process/{id}/status    # 查询流程状态
```

### 📝 任务管理
```http
GET  /api/tasks/pending                # 获取我的代办任务
GET  /api/tasks/{taskId}/details       # 获取任务详情
POST /api/tasks/{taskId}/approve       # 同意任务
POST /api/tasks/{taskId}/reject        # 驳回任务
```

### 📊 流程查询
```http
GET  /api/tasks/my-processes/running   # 我发起的运行中流程
GET  /api/tasks/my-processes/completed # 我发起的已完成流程
```

---

## 🔄 完整业务流程测试

### 第1步：用户登录
```http
POST http://localhost:8081/api/login
Content-Type: application/json

{
  "username": "user1",
  "password": "password"
}
```

### 第2步：发起请假流程
```http
POST http://localhost:8081/api/tasks/start-process
Content-Type: application/json

{
  "leaveType": "年假",
  "startDate": "2025-08-25",
  "endDate": "2025-08-26",
  "reason": "家庭旅行",
  "days": 2
}
```

**预期返回**:
```json
{
  "message": "流程发起成功",
  "processInstanceId": "process-123",
  "requester": "user1"
}
```

### 第3步：查看我发起的流程
```http
GET http://localhost:8081/api/tasks/my-processes/running
```

### 第4步：经理登录并查看代办任务
```http
POST http://localhost:8081/api/login
{
  "username": "manager",
  "password": "password"
}

GET http://localhost:8081/api/tasks/pending
```

**预期返回**:
```json
[
  {
    "taskId": "task-456",
    "taskName": "Manager Review",
    "processInstanceId": "process-123",
    "createTime": "2025-08-24T15:30:00",
    "businessData": {
      "requester": "user1",
      "leaveType": "年假",
      "startDate": "2025-08-25",
      "endDate": "2025-08-26",
      "reason": "家庭旅行",
      "days": 2
    }
  }
]
```

### 第5步：查看任务详情
```http
GET http://localhost:8081/api/tasks/task-456/details
```

### 第6步：审批任务（同意）
```http
POST http://localhost:8081/api/tasks/task-456/approve
Content-Type: application/json

{
  "comment": "同意请假申请，注意工作交接"
}
```

### 第7步：查看流程状态
```http
GET http://localhost:8081/api/tasks/process/process-123/status
```

**预期返回** (流程已完结):
```json
{
  "processInstanceId": "process-123",
  "isFinished": true,
  "status": "已完结"
}
```

---

## 🎯 核心功能验证清单

### ✅ 基本功能测试
- [ ] 用户可以成功登录
- [ ] 用户可以发起请假流程
- [ ] 经理可以看到代办任务
- [ ] 经理可以查看任务详情（包含业务数据）
- [ ] 经理可以同意任务
- [ ] 经理可以驳回任务
- [ ] 用户可以查看自己发起的流程状态
- [ ] 流程完成后状态正确更新

### ✅ 业务数据流转
- [ ] 流程变量正确传递（请假信息）
- [ ] 审批意见正确记录
- [ ] 业务数据在各个节点可见

### ✅ 权限控制
- [ ] 用户只能看到自己的任务和流程
- [ ] 经理只能处理分配给自己的任务
- [ ] 无权限访问他人任务时返回403

---

## 💡 实际使用技巧

### 1. **调试时关注的日志**
```
✅ 找到任务: Manager Review (流程实例ID: proc-789)
⏳ 即将调用 taskService.complete()
✅ taskService.complete() 执行完毕
```

### 2. **前端集成要点**
```javascript
// 确保带上 credentials 发送cookie
fetch('/api/tasks/pending', {
  credentials: 'include',
  headers: {
    'Content-Type': 'application/json'
  }
})
```

### 3. **业务数据处理**
```java
// 在流程中可以随时获取业务数据
Map<String, Object> variables = flowableApiGuide.getProcessVariables(processInstanceId);
String leaveType = (String) variables.get("leaveType");
Integer days = (Integer) variables.get("days");
```

### 4. **常见问题排查**
- Session丢失 → 检查cookie是否正确发送
- 任务找不到 → 检查用户权限和任务分配
- 流程卡住 → 检查BPMN流程定义是否正确

---

## 🎯 下一步学习重点

1. **熟练掌握基础API**: 重复测试上述流程直到熟练
2. **理解业务数据流转**: 观察变量在不同节点的变化
3. **掌握任务分配机制**: 理解assignee和candidateGroup
4. **学习复杂流程**: 分支、并行、子流程等
5. **集成前端页面**: 将API与实际业务页面结合

**记住**: 先会用，再深入！🚀