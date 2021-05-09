package com.farsunset.cim.http;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MessageService {

    @POST("/api/message/send")
    @FormUrlEncoded
    Call<Void> send(@Field("sender") String sender,
                            @Field("receiver") String receiver,
                            @Field("action") String action,
                            @Field("content") String content);
}
