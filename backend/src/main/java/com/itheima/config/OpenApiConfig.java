package com.itheima.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) 配置
 * 访问地址: http://localhost:8080/swagger-ui/index.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI 学情分析平台 API")
                        .description("基于 Spring Boot 3 + MyBatis-Plus 的智能学习辅助系统，集成 AI 成绩预测、学员聚类、情感分析与学习推荐")
                        .version("1.0.0")
                        .contact(new Contact().name("星阑"))
                        .license(new License().name("Apache 2.0")));
    }
}
