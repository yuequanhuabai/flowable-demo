package com.example.flowabledemo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用戶角色關聯實體
 */
@Data
@TableName("user_roles")
public class UserRole {
    
    private Long userId;
    
    private Long roleId;
}