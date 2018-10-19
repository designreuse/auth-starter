package com.gramant.auth.ports.rest.handlers;

import com.gramant.auth.ports.rest.request.UserRegistrationRequest;

public interface CreateUserHandler {

    Object handleCreateRequest(UserRegistrationRequest registrationRequest);
}
