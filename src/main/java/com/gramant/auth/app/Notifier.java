package com.gramant.auth.app;


import com.gramant.auth.domain.PasswordResetToken;
import com.gramant.auth.domain.User;

// todo: [#notify] move to separate module?
public interface Notifier {

    void registrationSuccess(User createdUser);

    void communicate(User user, String message);

    void resetPassword(PasswordResetToken token);

    void resetPasswordSuccess(User user);

    void confirmEmail();
}
