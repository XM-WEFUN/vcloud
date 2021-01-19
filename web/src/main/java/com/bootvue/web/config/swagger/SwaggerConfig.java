package com.bootvue.web.config.swagger;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ParameterType;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@ConditionalOnProperty(value = {"vcloud.swagger"}, havingValue = "true")
public class SwaggerConfig {

    @Bean(value = "privateApi")
    public Docket privateApi() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(
                        RequestHandlerSelectors.basePackage("com.bootvue.auth").negate()
                                .and(RequestHandlerSelectors.basePackage("com.bootvue.mq")).negate()
                                .and(RequestHandlerSelectors.basePackage("com.bootvue.scheduler")).negate()
                                .and(RequestHandlerSelectors.basePackage("com.bootvue"))
                )
                .paths(PathSelectors.any())
                .build().groupName("privateApi")
                .pathMapping("/admin")
                .genericModelSubstitutes(ResponseEntity.class)
                .useDefaultResponseMessages(false)
                .enableUrlTemplating(false)
                .globalRequestParameters(
                        Collections.singletonList(new RequestParameterBuilder()
                                .name("token")
                                .description("token信息")
                                .in(ParameterType.HEADER)
                                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                                .required(true)
                                .build()))
                .apiInfo(new ApiInfoBuilder()
                        .title("VCloud 接口文档")
                        .version("1.0.0")
                        .build())
                ;
    }

    @Bean(value = "publicApi")
    public Docket publicApi() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.bootvue.auth"))
                .paths(PathSelectors.any())
                .build().groupName("publicApi")
                .pathMapping("/auth")
                .genericModelSubstitutes(ResponseEntity.class)
                .useDefaultResponseMessages(false)
                .enableUrlTemplating(false)
                .globalRequestParameters(
                        Arrays.asList(new RequestParameterBuilder()
                                .name("appid").description("appid").in(ParameterType.QUERY)
                                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                                .required(true)
                                .build(), new RequestParameterBuilder()
                                .name("secret").description("secret").in(ParameterType.QUERY)
                                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                                .required(true)
                                .build()))
                .apiInfo(new ApiInfoBuilder()
                        .title("VCloud 接口文档")
                        .version("1.0.0")
                        .build())
                ;
    }
}

