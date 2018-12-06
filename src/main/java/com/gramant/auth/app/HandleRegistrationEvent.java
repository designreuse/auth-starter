package com.gramant.auth.app;

import com.gramant.auth.domain.event.EmailConfirmationCompleted;
import com.gramant.auth.domain.event.EmailConfirmationRequested;

public interface HandleRegistrationEvent {

    void processEmailConfirmationRequestedEvent(EmailConfirmationRequested event);

    void processEmailConfirmationCompletedEvent(EmailConfirmationCompleted event);

}
