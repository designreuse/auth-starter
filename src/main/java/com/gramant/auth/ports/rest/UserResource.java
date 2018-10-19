package com.gramant.auth.ports.rest;

import com.gramant.auth.app.ManageUser;
import com.gramant.auth.app.PasswordResetOperations;
import com.gramant.auth.domain.PasswordResetToken;
import com.gramant.auth.domain.PasswordResetTokenId;
import com.gramant.auth.domain.ex.PasswordResetTokenExpiredException;
import com.gramant.auth.domain.ex.PasswordResetTokenNotFoundException;
import com.gramant.auth.domain.ex.UserMissingException;
import com.gramant.auth.ports.rest.handlers.CreateUserHandler;
import com.gramant.auth.ports.rest.request.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/users")
@AllArgsConstructor
public class UserResource {

    private final ManageUser userManager;
    private final PasswordResetOperations passwordResetOperations;
    private final CreateUserHandler createUserHandler;

    @PostMapping
    public Object createUser(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        return createUserHandler.handleCreateRequest(userRegistrationRequest);
    }

    @PutMapping("/deactivation")
    public ResponseEntity deactivateAll(@RequestBody UpdateActivityRequest request) {
        userManager.batchUpdateActivity(request, false);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/activation")
    public ResponseEntity activateAll(@RequestBody UpdateActivityRequest request) {
        userManager.batchUpdateActivity(request, true);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/message")
    public ResponseEntity communicate(@RequestBody CommunicationRequest request) {
        userManager.communicate(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-reset-token")
    public ResponseEntity createPasswordResetToken(@RequestBody PasswordResetRequest passwordResetRequest) throws UserMissingException {
        passwordResetOperations.requestPasswordChange(passwordResetRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/password-reset-token/{tokenId}")
    public ResponseEntity confirmResetPassword(@PathVariable PasswordResetTokenId tokenId)
            throws PasswordResetTokenNotFoundException, PasswordResetTokenExpiredException {
        PasswordResetToken token = passwordResetOperations.confirmPasswordChange(tokenId);
        return ResponseEntity.ok().body(token.user().id().asString());
    }

    @PutMapping("/password")
    public ResponseEntity updatePassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest)
            throws PasswordResetTokenExpiredException, PasswordResetTokenNotFoundException, UserMissingException {
        passwordResetOperations.updatePassword(passwordUpdateRequest);
        return ResponseEntity.ok().build();
    }
}
