package com.gramant.auth.adapters.rest;

import com.gramant.auth.adapters.rest.representation.ContextUserRepresentation;
import com.gramant.auth.app.AuthenticationOperations;
import com.gramant.auth.domain.MetaUser;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@ResponseBody
@RequestMapping("/auth/profile")
@AllArgsConstructor
public class ProfileResource {

    private final AuthenticationOperations authenticationOperations;

    @GetMapping
    public ResponseEntity<ContextUserRepresentation> userDetails(Authentication authentication) {
        Optional<MetaUser> authenticated = authenticationOperations.confirmAuthentication(authentication);
        return authenticated
                .map(a -> ResponseEntity.ok(new ContextUserRepresentation(a)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}