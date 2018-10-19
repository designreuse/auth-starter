package com.gramant.auth.ports.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gramant.auth.app.ManageUser;
import com.gramant.auth.domain.UserId;
import com.gramant.auth.domain.ex.UserMissingException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/check")
@AllArgsConstructor
public class ExistsValidationResource {

    private ManageUser userManager;

    private static final ExistsDTO EXISTS = new ExistsDTO(true);
    private static final ExistsDTO NOT_EXISTS = new ExistsDTO(false);

    @GetMapping("/email/{mail}")
    public ResponseEntity<ExistsDTO> checkEmailExists(@PathVariable String mail) {
        try {
            userManager.findEnabledByEmail(mail);
            return ResponseEntity.ok(EXISTS);
        } catch (UserMissingException e) {
            return ResponseEntity.ok(NOT_EXISTS);
        }
    }

    @GetMapping("/username/{id}")
    public ResponseEntity<ExistsDTO> checkUsernameExists(@PathVariable String id) {
        try {
            userManager.findEnabledById(UserId.of(id));
            return ResponseEntity.ok(EXISTS);
        } catch (UserMissingException e) {
            return ResponseEntity.ok(NOT_EXISTS);
        }
    }

    @AllArgsConstructor
    static class ExistsDTO {
        @JsonProperty
        private boolean exists;
    }
}


