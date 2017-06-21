package net.expertio.server;

import io.grpc.MethodDescriptor;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import io.opentracing.contrib.OperationNameConstructor;
import io.opentracing.contrib.ServerTracingInterceptor;
import io.opentracing.util.GlobalTracer;
import net.expertio.service.ProfileService;
import net.expertio.service.UserService;

import java.io.IOException;

public class UserServer {

  static final String PREFIX = "testing-";

  public static void main(String[] args) throws IOException, InterruptedException {
    int servicePort = 8076;

    ServerTracingInterceptor tracingInterceptor =
        new ServerTracingInterceptor.Builder(GlobalTracer.get())
            .withOperationName(
                new OperationNameConstructor() {
                  @Override
                  public <ReqT, RespT> String constructOperationName(
                      MethodDescriptor<ReqT, RespT> method) {
                    return PREFIX + method.getFullMethodName();
                  }
                })
            .withTracedAttributes(
                ServerTracingInterceptor.ServerRequestAttribute.CALL_ATTRIBUTES,
                ServerTracingInterceptor.ServerRequestAttribute.METHOD_TYPE,
                ServerTracingInterceptor.ServerRequestAttribute.HEADERS)
            .build();

    ProfileService profileService = new ProfileService();

    Server assignorServer =
        NettyServerBuilder.forPort(servicePort)
            .addService(tracingInterceptor.intercept(new UserService(profileService)))
            .addService(tracingInterceptor.intercept(profileService))
            .build()
            .start();

    Runtime.getRuntime().addShutdownHook(new Thread(assignorServer::shutdownNow));

    assignorServer.awaitTermination();
  }
}
