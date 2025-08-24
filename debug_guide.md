# CompleteTaskCmd 调试断点指南

## 🎯 核心断点位置

### 1. Command入口点
```java
// 在 CompleteTaskCmd.execute() 方法设置断点
org.flowable.engine.impl.cmd.CompleteTaskCmd.execute(CommandContext commandContext)
```

### 2. 任务完成核心逻辑
```java
// 在 TaskHelper.completeTask() 方法设置断点
org.flowable.engine.impl.util.TaskHelper.completeTask(TaskEntity task, Map<String, Object> variables, CommandContext commandContext)
```

### 3. 数据库操作观察点
```java
// EntityManager 批量操作观察
org.flowable.common.engine.impl.persistence.entity.AbstractEntityManager.insert()
org.flowable.common.engine.impl.persistence.entity.AbstractEntityManager.update()  
org.flowable.common.engine.impl.persistence.entity.AbstractEntityManager.delete()

// 具体的Entity Manager
org.flowable.task.service.impl.persistence.entity.TaskEntityManagerImpl
org.flowable.engine.impl.persistence.entity.ExecutionEntityManagerImpl
org.flowable.engine.impl.persistence.entity.ActivityInstanceEntityManagerImpl
```

## 📊 关键观察变量

### 在 CompleteTaskCmd.execute() 中观察:
- `commandContext` - 命令上下文，包含所有EntityManager
- `commandContext.getDbSqlSession()` - SQL会话，包含待执行的操作
- `commandContext.getDbSqlSession().getInsertedObjects()` - 待插入对象
- `commandContext.getDbSqlSession().getUpdatedObjects()` - 待更新对象  
- `commandContext.getDbSqlSession().getDeletedObjects()` - 待删除对象

### 在 TaskHelper.completeTask() 中观察:
- `task` - 被完成的任务实体
- `execution` - 流程执行实例
- `processDefinition` - 流程定义
- `activityImpl` - 当前活动节点

## 🔧 MyBatis SQL拦截器观察
在 MyBatis 层面可以观察到实际的SQL执行。