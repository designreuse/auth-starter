package com.gramant.auth.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;

@AllArgsConstructor
@Getter
public enum Role {

    ADMIN(1, unmodifiableList(asList(Privilege.USERS, Privilege.VMS, Privilege.PRICES))),
    USER(2, singletonList(Privilege.DEFAULT));

    private int id;
    private List<Privilege> privileges;

    @JsonCreator
    public static Role getById(int id) {
        return Arrays.stream(Role.values())
                .filter(r -> r.id == id).findAny()
                .orElseThrow(() -> new IllegalArgumentException("Can not find Role with id " + id));
    }
}
