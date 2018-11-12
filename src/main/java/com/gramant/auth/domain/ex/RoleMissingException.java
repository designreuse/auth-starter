package com.gramant.auth.domain.ex;

import com.gramant.auth.domain.RoleId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RoleMissingException extends RuntimeException {

    public RoleMissingException(RoleId roleId) {
        super("Role with ID [" + roleId + "] not found!");
    }
}
