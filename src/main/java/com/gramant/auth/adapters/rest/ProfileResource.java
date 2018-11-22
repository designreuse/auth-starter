package com.gramant.auth.adapters.rest;

import com.gramant.auth.adapters.rest.representation.PrivilegedUserRepresentation;
import com.gramant.auth.domain.AuthenticatedUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@ResponseBody
@RequestMapping("/auth/profile")
@AllArgsConstructor
public class ProfileResource {

    @GetMapping
    public ResponseEntity<PrivilegedUserRepresentation> userDetails(@AuthenticationPrincipal AuthenticatedUserDetails principal) {
        Optional<AuthenticatedUserDetails> authenticated = Optional.ofNullable(principal);
        return authenticated
                .map(a -> ResponseEntity.ok(new PrivilegedUserRepresentation(a)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}