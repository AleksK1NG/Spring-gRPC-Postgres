package com.alexander.bryksin.microservive.springwebfluxgrpc.configuration;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(info = @Info(
        title = "Spring WebFlux, gRPC, PostgreSQL Microservice",
        description = "Spring WebFlux, gRPC, PostgreSQL Microservice example",
        contact = @Contact(
                name = "Alexander Bryksin",
                email = "alexander.bryksin@yandex.ru",
                url = "https://github.com/AleksK1NG"
        ),
        version = "1.0.0"
))
@Configuration
public class SwaggerOpenAPIConfiguration {
}
