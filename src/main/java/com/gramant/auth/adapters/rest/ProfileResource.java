package com.gramant.auth.adapters.rest;

import com.gramant.auth.adapters.rest.representation.PrivilegedUserRepresentation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@ResponseBody
@RequestMapping("/auth/profile")
@AllArgsConstructor
public class ProfileResource {

    @GetMapping
    public ResponseEntity<PrivilegedUserRepresentation> userDetails(Authentication authentication) {
        return Optional.ofNullable(authentication)
                .map(user -> ResponseEntity.ok(new PrivilegedUserRepresentation(authentication)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}