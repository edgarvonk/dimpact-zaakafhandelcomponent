/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.authentication;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.util.List;

import net.atos.zac.identity.model.User;

public class LoggedInUser extends User {

    private List<String> groupIds;

    public LoggedInUser(final String id, final String firstName, final String lastName, final String displayName, final String email,
            final List<String> groupIds) {
        super(id, firstName, lastName, displayName, email);
        this.groupIds = groupIds;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public boolean isInAnyGroup() {
        return isNotEmpty(groupIds);
    }
}
