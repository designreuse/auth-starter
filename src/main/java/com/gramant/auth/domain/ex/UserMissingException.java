package com.gramant.auth.domain.ex;

import com.gramant.auth.domain.UserId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserMissingException extends Exception {
    public UserMissingException(String email) {
        super("Cannot find active User with email: [" + email + "]");
    }

    public UserMissingException(UserId userId) {
        super("Cannot find active User with id: [" + userId + "]");
    }

}