package com.gramant.auth.adapters.rest;

import com.gramant.auth.domain.AuthenticatedUserDetails;
import com.gramant.auth.adapters.rest.representation.PrivilegedUserRepresentation;
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
        return Optional.ofNullable(principal)
                .map(user -> ResponseEntity.ok(new PrivilegedUserRepresentation(user)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}