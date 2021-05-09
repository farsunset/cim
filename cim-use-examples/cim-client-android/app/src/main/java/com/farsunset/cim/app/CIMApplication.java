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
package com.farsunset.cim.app;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

public class CIMApplication extends Application implements android.app.Application.ActivityLifecycleCallbacks {

    public static String NOTIFICATION_CHANNEL_ID = "NEW_MESSAGE_NOTIFICATION";

    public static String NOTIFICATION_CHANNEL_NAME = "新消息通知";

    private static CIMApplication context;

    public static CIMApplication getInstance() {
        return context;
    }

    private final AtomicInteger activeCounter = new AtomicInteger();


    @Override
    public void onCreate() {

        super.onCreate();

        registerActivityLifecycleCallbacks(this);

        context = this;

        createNotificationChannel();

    }


    private void createNotificationChannel(){

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationManager notificationMgr = ContextCompat.getSystemService(this,NotificationManager.class);

        if (notificationMgr.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(true);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            notificationMgr.createNotificationChannel(channel);
        }

    }



    public boolean isAppInBackground(){
        return activeCounter.get() == 0;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        activeCounter.getAndIncrement();
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }


    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        activeCounter.decrementAndGet();
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

}
