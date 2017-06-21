package net.expertio.dependencies;

import net.expertio.domain.User;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class UserProvider implements Supplier<User> {

  private final Random random = new Random();

  @Override
  public User get() {
    return User.newBuilder()
        .setId(random.nextInt(99))
        .setFirstName("Jim Jhones")
        .setLastName(UUID.randomUUID().toString())
        .build();
  }
}
