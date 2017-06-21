package net.expertio.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.opentracing.contrib.ClientTracingInterceptor;
import io.opentracing.util.GlobalTracer;
import net.expertio.domain.GetProfileRequest;
import net.expertio.domain.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class ProfileService extends ProfileServiceGrpc.ProfileServiceImplBase {
  private static final Logger log = LoggerFactory.getLogger(ProfileService.class);
  final ClientTracingInterceptor tracingInterceptor;
  final ManagedChannel channel;
  final ProfileServiceGrpc.ProfileServiceStub blockingStub;

  public ProfileService() {
    this.tracingInterceptor = new ClientTracingInterceptor.Builder(GlobalTracer.get()).build();
    this.channel =
        ManagedChannelBuilder.forAddress("localhost", 8079)
            .usePlaintext(true)
            .intercept(tracingInterceptor)
            .build();
    this.blockingStub = ProfileServiceGrpc.newStub(channel);
  }

  public void getProfile(GetProfileRequest request) {
    log.info("get profile for req id {} ", request);
    blockingStub.getProfile(
        GetProfileRequest.newBuilder().setId(new Random().nextInt(70)).build(),
        new StreamObserver<Profile>() {
          @Override
          public void onNext(Profile value) {
            System.out.println("on next on profile ");
          }

          @Override
          public void onError(Throwable t) {
              //like a good developers we do nothing
          }

          @Override
          public void onCompleted() {

          }
        });
  }
}
