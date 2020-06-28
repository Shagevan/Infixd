/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Interfaces;

import com.infixd.client.model.Location;

import java.util.List;

public interface GetNearUsers {
    void getUserDetails (List<Location> nearByUsers);
}
