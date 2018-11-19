package com.gramant.auth.adapters.rest;

import com.gramant.auth.app.ManageUser;
import com.gramant.auth.app.PreProcessRegistrationStep;
import com.gramant.auth.app.VerificationTokenOperations;
import com.gramant.auth.domain.VerificationToken;
import com.gramant.auth.domain.VerificationTokenId;
import com.gramant.auth.domain.ex.VerificationTokenExpiredException;
import com.gramant.auth.domain.ex.VerificationTokenNotFoundException;
import com.gramant.auth.domain.ex.UserMissingException;
import com.gramant.auth.adapters.rest.request.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@ResponseBody
@RequestMapping("/auth/users")
@AllArgsConstructor
public class UserResource {

    private final ManageUser userManager;
    private final VerificationTokenOperations verificationTokenOperations;
    private final PreProcessRegistrationStep preStep;

    @PostMapping
    public ResponseEntity createUser(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        UserRegistrationRequest next = preStep.process(userRegistrationRequest);
        userManager.add(next);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/deactivation")
    @PreAuthorize("hasAuthority('EDIT_USERS')")
    public ResponseEntity deactivateAll(@RequestBody UpdateActivityRequest request) {
        userManager.batchUpdateActivity(request, false);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/activation")
    @PreAuthorize("hasAuthority('EDIT_USERS')")
    public ResponseEntity activateAll(@RequestBody UpdateActivityRequest request) {
        userManager.batchUpdateActivity(request, true);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/message")
    public ResponseEntity communicate(@RequestBody CommunicationRequest request) {
        userManager.communicate(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update")
    public ResponseEntity update(@RequestBody @Valid UserUpdateRequest request) throws UserMissingException {
        userManager.update(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-reset-token")
    public ResponseEntity createPasswordResetToken(@RequestBody PasswordResetRequest passwordResetRequest) throws UserMissingException {
        verificationTokenOperations.requestPasswordChange(passwordResetRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/password-reset-token/{tokenId}")
    public ResponseEntity confirmResetPassword(@PathVariable VerificationTokenId tokenId)
            throws VerificationTokenNotFoundException, VerificationTokenExpiredException {
        VerificationToken token = verificationTokenOperations.confirmPasswordChange(tokenId);
        return ResponseEntity.ok().body(token.user().id().asString());
    }

    @PutMapping("/password")
    public ResponseEntity updatePassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest)
            throws VerificationTokenExpiredException, VerificationTokenNotFoundException, UserMissingException {
        verificationTokenOperations.updatePassword(passwordUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email-confirmation/{tokenId}")
    public ResponseEntity confirmEmail(@PathVariable VerificationTokenId tokenId)
            throws VerificationTokenNotFoundException, VerificationTokenExpiredException, UserMissingException {
        verificationTokenOperations.confirmEmail(tokenId);
        return ResponseEntity.ok().build();
    }
}
