/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Wrappers;

import com.infixd.client.model.GetGroupInfoResponse;

import java.io.Serializable;

public class GetGroupInfoWrapper implements Serializable {

    private GetGroupInfoResponse response;

    public GetGroupInfoResponse getResponse() {
        return response;
    }

    public void setResponse(GetGroupInfoResponse response) {
        this.response = response;
    }
}
