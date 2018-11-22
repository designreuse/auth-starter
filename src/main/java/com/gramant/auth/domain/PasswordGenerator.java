package com.gramant.auth.domain;

import java.util.UUID;

public class PasswordGenerator {

    public String generatePassword() {
        return UUID.randomUUID().toString().split("-")[0];
    }
}
