/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Interfaces;

import com.infixd.client.model.GroupInfoResponse;

import java.util.List;

public interface AddGroupResult {
    void getGroups(List<GroupInfoResponse> groups);
}
