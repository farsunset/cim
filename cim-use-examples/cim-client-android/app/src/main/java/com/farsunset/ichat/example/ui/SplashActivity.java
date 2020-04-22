/**
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
 * *
 * Website : http://www.farsunset.com                           *
 * *
 * **************************************************************************************
 */
package com.farsunset.ichat.example.ui;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;

import com.farsunset.cim.sdk.android.CIMPushManager;
import com.farsunset.ichat.example.BuildConfig;
import com.farsunset.ichat.example.R;
import com.farsunset.ichat.example.app.CIMMonitorActivity;
import com.farsunset.ichat.example.app.Constant;

public class SplashActivity extends CIMMonitorActivity {


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        CIMPushManager.setLoggerEnable(this, BuildConfig.DEBUG);
        //连接服务端

        CIMPushManager.connect(SplashActivity.this, Constant.CIM_SERVER_HOST, Constant.CIM_SERVER_PORT);


        final View view = View.inflate(this, R.layout.activity_splansh, null);
        setContentView(view);
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(2000);
        view.startAnimation(aa);


    }

    @Override
    public void onConnectFinished(boolean autoBind) {

        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        finish();
        CIMPushManager.destroy(this);
    }

    @Override
    public void onConnectFailed() {
        Toast.makeText(this,"连接服务器失败，请检查当前设备是否能连接上服务器IP和端口",Toast.LENGTH_LONG).show();
    }
}
