package com.example.flowabledemo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 数据库初始化器
 * 在应用启动时检查并创建OAuth2所需的数据库表
 */
@Component
@Order(1) // 确保在其他组件之前执行
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始初始化OAuth2数据库表...");

        try {
            // 检查并创建OAuth2授权表
            initializeOAuth2AuthorizationTable();

            // 检查并创建OAuth2授权同意表
            initializeOAuth2AuthorizationConsentTable();

            log.info("OAuth2数据库表初始化完成");
        } catch (Exception e) {
            log.error("OAuth2数据库表初始化失败", e);
            throw e;
        }
    }

    private void initializeOAuth2AuthorizationTable() {
        String tableName = "oauth2_authorization";

        if (!tableExists(tableName)) {
            log.info("创建表: {}", tableName);

            String createTableSql = """
                CREATE TABLE oauth2_authorization (
                    id VARCHAR(255) NOT NULL,
                    registered_client_id VARCHAR(255) NOT NULL,
                    principal_name VARCHAR(255) NOT NULL,
                    authorization_grant_type VARCHAR(100) NOT NULL,
                    authorized_scopes TEXT,
                    attributes TEXT,
                    state VARCHAR(500),
                    authorization_code_value TEXT,
                    authorization_code_issued_at TIMESTAMP,
                    authorization_code_expires_at TIMESTAMP,
                    authorization_code_metadata TEXT,
                    access_token_value TEXT,
                    access_token_issued_at TIMESTAMP,
                    access_token_expires_at TIMESTAMP,
                    access_token_metadata TEXT,
                    access_token_type VARCHAR(100),
                    access_token_scopes TEXT,
                    oidc_id_token_value TEXT,
                    oidc_id_token_issued_at TIMESTAMP,
                    oidc_id_token_expires_at TIMESTAMP,
                    oidc_id_token_metadata TEXT,
                    oidc_id_token_claims TEXT,
                    refresh_token_value TEXT,
                    refresh_token_issued_at TIMESTAMP,
                    refresh_token_expires_at TIMESTAMP,
                    refresh_token_metadata TEXT,
                    user_code_value TEXT,
                    user_code_issued_at TIMESTAMP,
                    user_code_expires_at TIMESTAMP,
                    user_code_metadata TEXT,
                    device_code_value TEXT,
                    device_code_issued_at TIMESTAMP,
                    device_code_expires_at TIMESTAMP,
                    device_code_metadata TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    PRIMARY KEY (id),
                    KEY idx_registered_client_id (registered_client_id),
                    KEY idx_principal_name (principal_name)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth2授权信息表'
                """;

            try {
                jdbcTemplate.execute(createTableSql);
                log.info("成功创建表: {}", tableName);
            } catch (DataAccessException e) {
                log.error("创建表失败: {}", tableName, e);
                throw e;
            }
        } else {
            log.info("表已存在，跳过创建: {}", tableName);
        }
    }

    private void initializeOAuth2AuthorizationConsentTable() {
        String tableName = "oauth2_authorization_consent";

        if (!tableExists(tableName)) {
            log.info("创建表: {}", tableName);

            String createTableSql = """
                CREATE TABLE oauth2_authorization_consent (
                    registered_client_id VARCHAR(255) NOT NULL,
                    principal_name VARCHAR(255) NOT NULL,
                    authorities TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    PRIMARY KEY (registered_client_id, principal_name),
                    KEY idx_principal_name (principal_name)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth2用户授权同意表'
                """;

            try {
                jdbcTemplate.execute(createTableSql);
                log.info("成功创建表: {}", tableName);
            } catch (DataAccessException e) {
                log.error("创建表失败: {}", tableName, e);
                throw e;
            }
        } else {
            log.info("表已存在，跳过创建: {}", tableName);
        }
    }

    private boolean tableExists(String tableName) {
        try {
            String sql = """
                SELECT COUNT(*) FROM information_schema.tables
                WHERE table_schema = DATABASE() AND table_name = ?
                """;

            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
            boolean exists = count != null && count > 0;

            log.debug("检查表是否存在: {} = {}", tableName, exists);
            return exists;
        } catch (DataAccessException e) {
            log.error("检查表是否存在时出错: {}", tableName, e);
            return false;
        }
    }
}