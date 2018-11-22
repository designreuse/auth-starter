package com.gramant.auth.domain.event;

import com.gramant.auth.domain.User;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Collection;

@Getter
@Accessors(fluent = true)
public class UsersMessaged {

    private final Collection<User> users;
    private final String message;

    public UsersMessaged(Collection<User> users, String message) {
        this.users = users;
        this.message = message;
    }
}
