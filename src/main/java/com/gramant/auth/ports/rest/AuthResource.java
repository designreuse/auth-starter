package com.gramant.auth.ports.rest;

import com.gramant.auth.app.ManageUser;
import com.gramant.auth.domain.AuthenticatedUserDetails;
import com.gramant.auth.ports.rest.representation.PrivilegedUserRepresentation;
import com.gramant.auth.ports.rest.representation.UserRepresentation;
import com.gramant.auth.ports.rest.request.UserRegistrationRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/")
@AllArgsConstructor
public class AuthResource {

    private ManageUser userManager;

    @GetMapping("/user")
    public ResponseEntity<PrivilegedUserRepresentation> userDetails(@AuthenticationPrincipal AuthenticatedUserDetails principal) {
        return Optional.ofNullable(principal)
                .map(user -> ResponseEntity.ok(new PrivilegedUserRepresentation(user)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/user")
    public ResponseEntity<UserRepresentation> createUser(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        return ResponseEntity.ok(new UserRepresentation(userManager.add(userRegistrationRequest)));
    }
}