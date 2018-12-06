package com.gramant.auth.app;

import com.gramant.auth.domain.event.*;

public interface HandlePasswordEvent {

    void handlePasswordChangeRequestedEvent(PasswordChangeRequested event);

    void handlePasswordChangeCompletedEvent(PasswordChangeCompleted event);

    void handlePasswordResetCompletedEvent(PasswordResetCompleted event);
}
