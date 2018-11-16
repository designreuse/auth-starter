package com.gramant.auth.app;

import com.gramant.auth.adapters.rest.request.UserRegistrationRequest;

public interface PreProcessRegistrationStep  {

    UserRegistrationRequest process(UserRegistrationRequest registrationRequest);
}
