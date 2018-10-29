package com.gramant.auth.adapters.rest.handlers;

import com.gramant.auth.adapters.rest.request.UserRegistrationRequest;

public interface CreateUserHandler {

    Object handleCreateRequest(UserRegistrationRequest registrationRequest);
}
