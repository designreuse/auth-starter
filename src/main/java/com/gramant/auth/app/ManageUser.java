package com.gramant.auth.app;

import com.gramant.auth.domain.User;
import com.gramant.auth.domain.UserId;
import com.gramant.auth.domain.ex.UserMissingException;
import com.gramant.auth.ports.rest.request.CommunicationRequest;
import com.gramant.auth.ports.rest.request.UpdateActivityRequest;
import com.gramant.auth.ports.rest.request.UserRegistrationRequest;
import com.gramant.auth.ports.rest.request.UserUpdateRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface ManageUser {

    User add(@NotNull @Valid UserRegistrationRequest userRegistrationRequest);

    User update(@NotNull @Valid UserUpdateRequest userUpdateRequest);

    User findEnabledByEmail(@NotNull String email) throws UserMissingException;

    User findEnabledById(@NotNull UserId userId) throws UserMissingException;

    List<User> list();

    User get(@NotNull UserId userId);

    /**
     *
     * @param request contains target id list
     * @param activate true: activate list, false: deactivate list
     */
    void batchUpdateActivity(@NotNull @Valid UpdateActivityRequest request, boolean activate);

    void communicate(@NotNull @Valid CommunicationRequest request);
}
