package com.qcadoo.swagger.internal;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicate;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class ApplicationSwaggerConfig {

    @Bean
    public Docket customDocket() {
        return new Docket(DocumentationType.SWAGGER_2).select().paths(paths()).build()
                .forCodeGeneration(true);
    }

    private Predicate<String> paths() {
        return or(regex("/integration.*"), regex("/rest.*"), regex("/scada.*"), regex("/warehouse.*"),
                regex("/productionTrackingsTerminal.*"));
    }
}
