package com.gramant.auth.adapters.rest.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gramant.auth.domain.UserId;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.util.Collection;

@Getter
@Accessors(fluent = true)
public class UpdateActivityRequest {

    @NotEmpty
    private final Collection<UserId> userIds;

    @JsonCreator
    public UpdateActivityRequest(@JsonProperty("userIds") Collection<UserId> userIds) {
        this.userIds = userIds;
    }
}
