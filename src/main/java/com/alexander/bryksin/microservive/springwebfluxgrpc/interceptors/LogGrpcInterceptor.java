package com.alexander.bryksin.microservive.springwebfluxgrpc.interceptors;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class LogGrpcInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        log.info("service: {}, method: {}, headers: {}", call.getMethodDescriptor().getServiceName(), call.getMethodDescriptor().getBareMethodName(), headers.toString());
        return next.startCall(call, headers);
    }
}
