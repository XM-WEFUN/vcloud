package com.bootvue.web.config.swagger;

import com.bootvue.common.model.AppUser;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@ConditionalOnProperty(value = {"knife4j.enable"}, havingValue = "true")
@EnableKnife4j
@EnableOpenApi
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SwaggerConfig {

    private final OpenApiExtensionResolver openApiExtensionResolver;

    @Bean
    public Docket authApi() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.bootvue"))
                .paths(PathSelectors.any())
                .build()
                .ignoredParameterTypes(AppUser.class)
                .extensions(openApiExtensionResolver.buildSettingExtensions())
                ;
    }
}