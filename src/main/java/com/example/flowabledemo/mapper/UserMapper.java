package com.example.flowabledemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.flowabledemo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户数据访问接口
 * OAuth2演示项目的简化用户数据操作
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查找用户
     * 用于登录验证
     */
    @Select("SELECT id, username, password, enabled, created_at, updated_at FROM users WHERE username = #{username}")
    User findByUsername(String username);

    /**
     * 检查用户名是否存在
     * 用于注册时的重复性检查（如果需要注册功能）
     */
    @Select("SELECT COUNT(1) > 0 FROM users WHERE username = #{username}")
    boolean existsByUsername(String username);

    /**
     * 根据用户名查找启用的用户
     * 只返回启用状态的用户
     */
    @Select("SELECT id, username, password, enabled, created_at, updated_at FROM users WHERE username = #{username} AND enabled = true")
    User findByUsernameAndEnabled(String username);

    /**
     * 统计启用的用户数量
     * 用于系统状态监控
     */
    @Select("SELECT COUNT(1) FROM users WHERE enabled = true")
    long countEnabledUsers();

    /**
     * 根据用户名更新最后登录时间（如果需要跟踪登录时间）
     * 注：当前表结构中没有last_login字段，这个方法作为扩展示例
     */
    // @Update("UPDATE users SET updated_at = NOW() WHERE username = #{username}")
    // int updateLastLoginTime(String username);
}