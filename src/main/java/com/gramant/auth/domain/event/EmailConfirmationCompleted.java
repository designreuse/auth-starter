package com.gramant.auth.domain.event;

import com.gramant.auth.domain.User;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class EmailConfirmationCompleted{
    private final User user;

    public EmailConfirmationCompleted(User user) {
        this.user = user;
    }
}
