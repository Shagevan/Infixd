package com.connect.infixd.mobile.Retrofit;


import com.connect.infixd.mobile.POJOModels.GetFBPhotosResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GraphAPIInterface {

    @GET("me?fields=photos.limit(50)%7Bimages%7D")
    Call<GetFBPhotosResponse> getFacebookPhotos(@Query(value = "access_token",encoded = true) String accessToken);

}
