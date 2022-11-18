package com.alexander.bryksin.microservive.springwebfluxgrpc.interceptors;


import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class GlobalInterceptorConfiguration {

    @GrpcGlobalServerInterceptor
    public LogGrpcInterceptor logServerInterceptor() {
        return new LogGrpcInterceptor();
    }
}
