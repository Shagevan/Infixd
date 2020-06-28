/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */package com.connect.infixd.mobile.Interfaces;

import com.infixd.client.model.UserResponse;

import java.util.List;

public interface GetFriendsDetails {
    void getFriendsDetails(List<UserResponse> friendsDetails);
}
