/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Wrappers;

import com.infixd.client.model.GroupInfoResponse;

import java.io.Serializable;
import java.util.List;

public class GroupInfoResponseWrapper implements Serializable {

    private List<GroupInfoResponse> groups;

    public List<GroupInfoResponse> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupInfoResponse> groups) {
        this.groups = groups;
    }
}
