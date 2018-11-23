package com.gramant.auth.app;


import com.gramant.auth.domain.User;
import com.gramant.auth.domain.VerificationToken;

// todo: [#notify] move to separate module?
public interface Notifier {

    void registrationSuccess(User createdUser);

    void communicate(User user, String message);

    void recoverPassword(VerificationToken token, String email);

    void recoverPasswordSuccess(User user);

    void confirmEmail(VerificationToken token, String email);

    void confirmEmailSuccess(User user);

    void resetPasswordSuccess(User user, String newPassword);
}
