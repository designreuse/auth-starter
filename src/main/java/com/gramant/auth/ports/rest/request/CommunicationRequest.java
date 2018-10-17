package com.gramant.auth.ports.rest.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gramant.auth.domain.UserId;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.util.Collection;

@Getter
@Accessors(fluent = true)
public class CommunicationRequest {

    @NotEmpty
    private final Collection<UserId> userIds;

    @NotEmpty
    private final String message;

    @JsonCreator
    public CommunicationRequest(@JsonProperty("userIds") Collection<UserId> userIds,
                                @JsonProperty("message") String message) {
        this.userIds = userIds;
        this.message = message;
    }
}
