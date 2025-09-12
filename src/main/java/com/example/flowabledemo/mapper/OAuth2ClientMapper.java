package com.example.flowabledemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.flowabledemo.entity.OAuth2Client;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

/**
 * OAuth2客户端数据访问层
 * 负责客户端数据的CRUD操作
 */
@Mapper
public interface OAuth2ClientMapper extends BaseMapper<OAuth2Client> {

    /**
     * 根据客户端ID查找客户端
     */
    @Select("SELECT * FROM oauth2_client WHERE client_id = #{clientId} AND is_active = true")
    OAuth2Client findByClientId(String clientId);

    /**
     * 根据客户端名称查找客户端
     */
    @Select("SELECT * FROM oauth2_client WHERE client_name = #{clientName}")
    OAuth2Client findByClientName(String clientName);

    /**
     * 检查客户端ID是否已存在
     */
    @Select("SELECT COUNT(*) > 0 FROM oauth2_client WHERE client_id = #{clientId}")
    boolean existsByClientId(String clientId);

    /**
     * 检查客户端名称是否已存在
     */
    @Select("SELECT COUNT(*) > 0 FROM oauth2_client WHERE client_name = #{clientName}")
    boolean existsByClientName(String clientName);
}