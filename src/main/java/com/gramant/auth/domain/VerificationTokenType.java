package com.gramant.auth.domain;

import lombok.Getter;

@Getter
public enum VerificationTokenType {

    EMAIL(1008),
    PASSWORD(1);

    /**
     * коэффициент ко времени протухания токена, умножается на 10 минут.
     * 1008 -> 1 неделя
     */
    private int timeFactor;

    VerificationTokenType(int timeFactor) {
        this.timeFactor = timeFactor;
    }
}
