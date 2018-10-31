package com.gramant.auth.app;

import com.gramant.auth.domain.User;

public interface AdditionalUserDataFetchHandler {

    Object fetchAdditionalData(User user);
}
