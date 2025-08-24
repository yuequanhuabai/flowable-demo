package com.example.flowabledemo;

/**
 * 调试断点指南 - 告诉你在IDE中应该在哪里设置断点
 * 
 * 🎯 调试目标：观察 taskService.complete() 内部的15个数据库操作
 */
public class DebugBreakpoints {
    
    /**
     * 🔴 断点1: Command入口 - 最重要的断点
     * 
     * 类：org.flowable.engine.impl.cmd.CompleteTaskCmd
     * 方法：execute(CommandContext commandContext)
     * 
     * 🔍 在这里观察的关键变量：
     * - commandContext: 命令上下文，包含所有操作
     * - commandContext.getSessions(): 包含DbSqlSession
     * - 右键commandContext → "Evaluate Expression" → 输入下面的表达式
     */
    public void breakpoint1_CommandEntry() {
        /*
         在IDE的 Evaluate Expression 中输入：
         
         // 观察DbSqlSession中的操作队列
         commandContext.getSessions().values()
         
         // 或者使用我们的调试拦截器输出
         */
    }
    
    /**
     * 🔴 断点2: 任务完成核心逻辑
     * 
     * 类：org.flowable.engine.impl.util.TaskHelper  
     * 方法：completeTask(TaskEntity task, Map<String,Object> variables, CommandContext commandContext)
     * 
     * 🔍 观察变量：
     * - task: 当前被完成的任务
     * - execution: 流程执行实例
     * - processDefinition: 流程定义
     */
    public void breakpoint2_TaskHelper() {
        /*
         关键观察点：
         - task.getId(): 当前任务ID
         - task.getName(): 任务名称
         - execution.getCurrentActivityId(): 当前节点
         - execution.getProcessInstanceId(): 流程实例ID
         */
    }
    
    /**
     * 🔴 断点3: 数据库操作入队
     * 
     * 类：org.flowable.common.engine.impl.persistence.entity.AbstractEntityManager
     * 方法：insert(Entity entity)
     * 方法：update(Entity entity)  
     * 方法：delete(Entity entity)
     * 
     * 🔍 观察变量：
     * - entity: 被操作的实体对象
     */
    public void breakpoint3_EntityManager() {
        /*
         每次数据库操作都会调用这些方法，可以看到：
         - TaskEntity 的插入/删除
         - ActivityInstanceEntity 的插入/更新
         - HistoricTaskInstanceEntity 的插入/更新
         - ExecutionEntity 的更新
         */
    }
    
    /**
     * 🔴 断点4: SQL执行观察
     * 
     * 类：org.flowable.common.engine.impl.persistence.AbstractManager
     * 方法：getDbSqlSession().insert(String statement, Object parameter)
     * 方法：getDbSqlSession().update(String statement, Object parameter)
     * 方法：getDbSqlSession().delete(String statement, Object parameter)
     * 
     * 🔍 观察变量：
     * - statement: SQL语句的MyBatis映射ID
     * - parameter: SQL参数对象
     */
    public void breakpoint4_SqlExecution() {
        /*
         可以看到具体的SQL操作：
         - "insertTask": 插入新任务
         - "deleteTask": 删除完成的任务  
         - "insertHistoricTaskInstance": 插入历史任务
         - "updateExecution": 更新执行实例
         */
    }
    
    /**
     * 🔴 断点5: 事务提交前的最终检查
     * 
     * 类：org.flowable.common.engine.impl.db.DbSqlSession
     * 方法：flush()
     * 
     * 🔍 观察变量：
     * - insertedObjects: 所有待插入的对象集合
     * - updatedObjects: 所有待更新的对象集合  
     * - deletedObjects: 所有待删除的对象集合
     */
    public void breakpoint5_TransactionFlush() {
        /*
         在事务提交前，可以看到所有操作的汇总：
         - insertedObjects.size(): 插入操作数量
         - updatedObjects.size(): 更新操作数量
         - deletedObjects.size(): 删除操作数量
         
         这里能看到完整的15个操作！
         */
    }
    
    /**
     * 💡 IDE调试技巧
     */
    public void debugTips() {
        /*
         1. 使用条件断点：
            右键断点 → Condition → 输入: task.getName().equals("Submit Leave Request")
            这样只在特定任务时才暂停
         
         2. 使用 Evaluate Expression：
            选中变量 → Alt+F8 → 输入复杂表达式
            例如：commandContext.getSessions().values().stream().findFirst()
         
         3. 查看调用栈：
            在断点暂停时，查看 Call Stack 窗口
            可以看到完整的调用链路
         
         4. Watch变量：
            右键变量 → Add to Watches
            可以持续观察变量的变化
         
         5. 线程视图：
            View → Tool Windows → Debug → Threads
            可以看到Flowable的内部线程执行
         */
    }
}