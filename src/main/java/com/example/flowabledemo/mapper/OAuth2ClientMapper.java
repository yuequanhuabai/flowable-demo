package com.example.flowabledemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.flowabledemo.entity.OAuth2Client;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * OAuth2客戶端Mapper接口
 */
@Mapper
public interface OAuth2ClientMapper extends BaseMapper<OAuth2Client> {

    /**
     * 根據客戶端ID查詢客戶端
     */
    @Select("SELECT * FROM oauth2_client WHERE client_id = #{clientId} AND is_active = 1")
    OAuth2Client findByClientId(@Param("clientId") String clientId);

    /**
     * 檢查客戶端ID是否已存在
     */
    @Select("SELECT COUNT(1) FROM oauth2_client WHERE client_id = #{clientId}")
    Integer countByClientId(@Param("clientId") String clientId);

    /**
     * 查詢所有活躍的客戶端
     */
    @Select("SELECT * FROM oauth2_client WHERE is_active = 1 ORDER BY created_at DESC")
    List<OAuth2Client> findAllActiveClients();

    /**
     * 根據客戶端名稱模糊查詢
     */
    @Select("SELECT * FROM oauth2_client WHERE client_name LIKE CONCAT('%', #{clientName}, '%') AND is_active = 1")
    List<OAuth2Client> findByClientNameLike(@Param("clientName") String clientName);
}