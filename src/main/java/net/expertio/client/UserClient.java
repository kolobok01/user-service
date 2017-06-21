package net.expertio.client;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.opentracing.contrib.ClientTracingInterceptor;
import io.opentracing.util.GlobalTracer;
import net.expertio.domain.GetUserRequest;
import net.expertio.domain.User;
import net.expertio.opentracing.TracerFactory;
import net.expertio.service.UserService;
import net.expertio.service.UserServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class UserClient {

  private static final Logger log = LoggerFactory.getLogger(UserService.class);

  public static void main(String[] args) throws InterruptedException {

    String host = "localhost";
    int servicePort = 8076;

    Random random = new Random();
    Set<User> users = Sets.<User>newHashSet();

    TracerFactory.init("Foo", null);

    ClientTracingInterceptor tracingInterceptor =
        new ClientTracingInterceptor.Builder(GlobalTracer.get()).build();
    ManagedChannel channel =
        ManagedChannelBuilder.forAddress(host, servicePort)
            .usePlaintext(true)
            .intercept(tracingInterceptor)
            .build();

    UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);
    Stopwatch stopwatch = Stopwatch.createStarted();
    java.util.stream.Stream.iterate(0, (x) -> x + 1)
        .map(
            k -> {
              User assignor =
                  stub.getUser(GetUserRequest.newBuilder().setId(random.nextInt(1000)).build());
              users.add(assignor);
              return 1;
            })
        .limit(10)
        .count();

    stopwatch.stop();
    long duration = stopwatch.elapsed(TimeUnit.SECONDS);

    System.out.println(users.size() + " completed in " + duration);
    channel.shutdown();
  }
}
