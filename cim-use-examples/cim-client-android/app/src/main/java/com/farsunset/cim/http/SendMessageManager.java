/*
 * Copyright 2013-2019 Xia Jun(3979434@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * **************************************************************************************
 *
 *                         Website : http://www.farsunset.com                           *
 *
 * **************************************************************************************
 */
package com.farsunset.cim.http;

import android.widget.Toast;

import com.farsunset.cim.BuildConfig;
import com.farsunset.cim.app.CIMApplication;
import com.farsunset.cim.sdk.android.model.Message;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SendMessageManager{


    public static <T> T createService(Class<T> tClass) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.CIM_API_URL + "/")
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();

        return retrofit.create(tClass);

    }


    public static void send(final Message message) {

        MessageService messageService = createService(MessageService.class);

        messageService.send(message.getSender(), message.getReceiver(),message.getAction(),message.getContent()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(CIMApplication.getInstance(),"发送成功!",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(CIMApplication.getInstance(),"发送失败,请检查接口地址或者服务器是否正常",Toast.LENGTH_LONG).show();
            }
        });
    }

}
