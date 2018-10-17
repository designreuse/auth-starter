package com.gramant.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum Privilege {

    DEFAULT(0),
    USERS(1),
    VMS(2),
    PRICES(4);

    private int id;

    public static Privilege getById(int id) {
        return Arrays.stream(Privilege.values()).filter(r -> r.id == id).findAny().orElseThrow(() -> new IllegalArgumentException("Can not find Privilege with id " + id));
    }

}
