package net.expertio.service;

import io.grpc.stub.StreamObserver;
import net.expertio.dependencies.UserProvider;
import net.expertio.domain.GetProfileRequest;
import net.expertio.domain.GetUserRequest;
import net.expertio.domain.User;
import net.expertio.opentracing.TracerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class UserService extends UserServiceGrpc.UserServiceImplBase {

  private static final Logger log = LoggerFactory.getLogger(UserService.class);
  private final Supplier<User> userProvider = new UserProvider();
  private ProfileService profileService;

  public UserService(ProfileService profileService) {
    TracerFactory.init("Bar", null);
    this.profileService = profileService;
  }

  @Override
  public void getUser(GetUserRequest request, StreamObserver<User> responseObserver) {
    log.debug("get user for req id {} ", request.getId());
    profileService.getProfile(GetProfileRequest.newBuilder().setId(request.getId()).build());
    responseObserver.onNext(userProvider.get());
    responseObserver.onCompleted();
  }
}
