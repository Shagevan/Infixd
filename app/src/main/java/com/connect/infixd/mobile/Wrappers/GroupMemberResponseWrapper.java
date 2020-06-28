/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Wrappers;

import com.infixd.client.model.GroupMemberResponse;

import java.io.Serializable;
import java.util.List;

public class GroupMemberResponseWrapper implements Serializable{
    private List<GroupMemberResponse> membersInfo;

    public List<GroupMemberResponse> getMembersInfo() {
        return membersInfo;
    }

    public void setMembersInfo(List<GroupMemberResponse> membersInfo) {
        this.membersInfo = membersInfo;
    }
}
