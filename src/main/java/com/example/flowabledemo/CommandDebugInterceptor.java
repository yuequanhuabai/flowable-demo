package com.example.flowabledemo;

import org.flowable.common.engine.impl.interceptor.AbstractCommandInterceptor;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandConfig;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.interceptor.CommandExecutor;
import org.flowable.common.engine.impl.persistence.entity.Entity;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * Command调试拦截器 - 观察Command内部的所有数据库操作
 */
public class CommandDebugInterceptor extends AbstractCommandInterceptor {
    
    @Override
    public <T> T execute(CommandConfig config, Command<T> command, CommandExecutor commandExecutor) {
        String commandName = command.getClass().getSimpleName();
        
        // 只监控Task相关的命令
        if (commandName.contains("Task") || commandName.contains("Complete")) {
            System.out.println("\n🔍 ===== 开始调试Command: " + commandName + " =====");
            
            try {
                // 执行前检查Agenda
                analyzeAgendaBeforeExecution();
                
                // 执行命令并获取CommandContext
                T result = next.execute(config, command, commandExecutor);
                
                // 执行后检查Agenda和数据库操作
                analyzeAgendaAfterExecution();
                analyzeCommandContext();
                
                System.out.println("🔍 ===== Command调试结束: " + commandName + " =====\n");
                
                return result;
                
            } catch (Exception e) {
                System.out.println("❌ Command执行异常: " + e.getMessage());
                throw e;
            }
        } else {
            return next.execute(config, command, commandExecutor);
        }
    }
    
    private void analyzeAgendaBeforeExecution() {
        try {
            CommandContext commandContext = org.flowable.common.engine.impl.context.Context.getCommandContext();
            if (commandContext != null) {
                Object agenda = getAgenda(commandContext);
                if (agenda != null) {
                    System.out.println("📋 执行前Agenda分析:");
                    analyzeAgenda(agenda, "执行前");
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️  分析执行前Agenda失败: " + e.getMessage());
        }
    }
    
    private void analyzeAgendaAfterExecution() {
        try {
            CommandContext commandContext = org.flowable.common.engine.impl.context.Context.getCommandContext();
            if (commandContext != null) {
                Object agenda = getAgenda(commandContext);
                if (agenda != null) {
                    System.out.println("📋 执行后Agenda分析:");
                    analyzeAgenda(agenda, "执行后");
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️  分析执行后Agenda失败: " + e.getMessage());
        }
    }
    
    private Object getAgenda(CommandContext commandContext) {
        try {
            // 尝试通过反射获取FlowableEngineAgenda
            Field[] fields = commandContext.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(commandContext);
                if (value != null && value.getClass().getName().contains("Agenda")) {
                    return value;
                }
            }
        } catch (Exception e) {
            System.out.println("获取Agenda失败: " + e.getMessage());
        }
        return null;
    }
    
    private void analyzeAgenda(Object agenda, String phase) {
        try {
            // 获取待执行操作数量
            Boolean isEmpty = getFieldValue(agenda, "isEmpty");
            if (isEmpty != null) {
                System.out.println("  🎯 " + phase + "Agenda是否为空: " + isEmpty);
            }
            
            // 尝试获取操作队列
            Collection<?> operations = getFieldValue(agenda, "operations");
            if (operations != null) {
                System.out.println("  📝 " + phase + "待执行操作数量: " + operations.size());
                for (Object operation : operations) {
                    System.out.println("    - " + operation.getClass().getSimpleName());
                }
            }
            
        } catch (Exception e) {
            System.out.println("分析Agenda详情失败: " + e.getMessage());
        }
    }
    
    private void analyzeCommandContext() {
        try {
            // 获取当前线程的CommandContext
            CommandContext commandContext = org.flowable.common.engine.impl.context.Context.getCommandContext();
            
            if (commandContext != null) {
                System.out.println("📊 CommandContext分析:");
                
                // 通过反射获取DbSqlSession
                Object dbSqlSession = getDbSqlSession(commandContext);
                
                if (dbSqlSession != null) {
                    analyzeDbSqlSession(dbSqlSession);
                } else {
                    System.out.println("⚠️  无法获取DbSqlSession");
                }
                
                // 分析EntityManager
                analyzeEntityManagers(commandContext);
            }
            
        } catch (Exception e) {
            System.out.println("⚠️  分析CommandContext时出错: " + e.getMessage());
        }
    }
    
    private Object getDbSqlSession(CommandContext commandContext) {
        try {
            // 通过反射获取DbSqlSession
            Field[] fields = commandContext.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(commandContext);
                if (value != null && value.getClass().getName().contains("DbSqlSession")) {
                    return value;
                }
            }
            
            // 尝试通过sessions获取
            Field sessionsField = findField(commandContext.getClass(), "sessions");
            if (sessionsField != null) {
                sessionsField.setAccessible(true);
                Map<?, ?> sessions = (Map<?, ?>) sessionsField.get(commandContext);
                for (Object session : sessions.values()) {
                    if (session.getClass().getName().contains("DbSqlSession")) {
                        return session;
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println("获取DbSqlSession失败: " + e.getMessage());
        }
        return null;
    }
    
    private void analyzeDbSqlSession(Object dbSqlSession) {
        try {
            System.out.println("🗄️  DbSqlSession操作分析:");
            
            // 分析插入操作
            Collection<?> insertedObjects = getFieldValue(dbSqlSession, "insertedObjects");
            if (insertedObjects != null && !insertedObjects.isEmpty()) {
                System.out.println("  📝 待插入对象 (" + insertedObjects.size() + "个):");
                for (Object obj : insertedObjects) {
                    System.out.println("    - " + obj.getClass().getSimpleName() + ": " + getEntityInfo(obj));
                }
            }
            
            // 分析更新操作
            Collection<?> updatedObjects = getFieldValue(dbSqlSession, "updatedObjects");
            if (updatedObjects != null && !updatedObjects.isEmpty()) {
                System.out.println("  🔄 待更新对象 (" + updatedObjects.size() + "个):");
                for (Object obj : updatedObjects) {
                    System.out.println("    - " + obj.getClass().getSimpleName() + ": " + getEntityInfo(obj));
                }
            }
            
            // 分析删除操作
            Collection<?> deletedObjects = getFieldValue(dbSqlSession, "deletedObjects");
            if (deletedObjects != null && !deletedObjects.isEmpty()) {
                System.out.println("  🗑️  待删除对象 (" + deletedObjects.size() + "个):");
                for (Object obj : deletedObjects) {
                    System.out.println("    - " + obj.getClass().getSimpleName() + ": " + getEntityInfo(obj));
                }
            }
            
        } catch (Exception e) {
            System.out.println("分析DbSqlSession失败: " + e.getMessage());
        }
    }
    
    private void analyzeEntityManagers(CommandContext commandContext) {
        try {
            System.out.println("🏭 EntityManager分析:");
            
            // 获取所有EntityManager
            Field[] fields = commandContext.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().contains("EntityManager") || field.getName().contains("Manager")) {
                    field.setAccessible(true);
                    Object manager = field.get(commandContext);
                    if (manager != null) {
                        System.out.println("  - " + field.getName() + ": " + manager.getClass().getSimpleName());
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println("分析EntityManager失败: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private <T> T getFieldValue(Object object, String fieldName) {
        try {
            Field field = findField(object.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                return (T) field.get(object);
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return null;
    }
    
    private Field findField(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }
    
    private String getEntityInfo(Object entity) {
        try {
            if (entity instanceof Entity) {
                String id = getFieldValue(entity, "id");
                String name = getFieldValue(entity, "name");
                if (id != null) {
                    return "ID=" + id + (name != null ? ", Name=" + name : "");
                }
            }
            return entity.toString();
        } catch (Exception e) {
            return entity.getClass().getSimpleName();
        }
    }
}