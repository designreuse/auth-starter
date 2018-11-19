package com.gramant.auth.app;


import com.gramant.auth.domain.User;
import com.gramant.auth.domain.VerificationToken;

// todo: [#notify] move to separate module?
public interface Notifier {

    void registrationSuccess(User createdUser);

    void communicate(User user, String message);

    void resetPassword(VerificationToken token);

    void resetPasswordSuccess(User user);

    void confirmEmail(VerificationToken token);

    void confirmEmailSuccess(User user);
}
