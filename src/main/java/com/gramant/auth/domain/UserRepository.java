package com.gramant.auth.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);

    Optional<User> get(UserId userId);

    User add(User user);

    Collection<User> list();

    Collection<User> getAll(Collection<UserId> userIds);

    void updateAll(List<User> userIds);

    void update(User user);
}
