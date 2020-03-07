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
import android.media.MediaPlayer;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.farsunset.cim.sdk.android.CIMPushManager;
import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.model.Message;
import com.farsunset.cim.sdk.android.model.SentBody;
import com.farsunset.ichat.example.R;
import com.farsunset.ichat.example.adapter.SystemMsgListViewAdapter;
import com.farsunset.ichat.example.app.CIMMonitorActivity;
import com.farsunset.ichat.example.app.Constant;

import java.util.ArrayList;

public class SystemMessageActivity extends CIMMonitorActivity implements OnClickListener {

    protected ListView chatListView;
    protected SystemMsgListViewAdapter adapter;
    private ArrayList<Message> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_chat);
        initViews();
    }

    public void initViews() {

        list = new ArrayList<Message>();

        chatListView = (ListView) findViewById(R.id.chat_list);
        findViewById(R.id.TOP_BACK_BUTTON).setOnClickListener(this);
        findViewById(R.id.TOP_BACK_BUTTON).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.TOP_BACK_BUTTON)).setText("登录");
        ((TextView) findViewById(R.id.TITLE_TEXT)).setText("系统消息");
        ((TextView) findViewById(R.id.account)).setText(this.getIntent().getStringExtra("account"));

        adapter = new SystemMsgListViewAdapter(this, list);
        chatListView.setAdapter(adapter);

        Toast.makeText(this,"登录成功，请通过后台页面发送消息吧^_^",Toast.LENGTH_LONG).show();

    }

    //收到消息
    @Override
    public void onMessageReceived(Message message) {

        if (message.getAction().equals(Constant.MessageAction.ACTION_999)) {
            //返回登录页面，停止接受消息
            CIMPushManager.stop(this);

            Toast.makeText(this,"你被系统强制下线!",Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            list.add(0,message);
            adapter.notifyDataSetChanged();
            chatListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    chatListView.setSelection(0);
                }
            },500);
        }

    }

    @Override
    public void onNetworkChanged(NetworkInfo info) {

        if (info == null) {
            Toast.makeText(this,"网络已断开!",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"网络已恢复，重新连接....",Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        //返回登录页面，停止接受消息
        CIMPushManager.stop(this);
        startActivity(new Intent(this, LoginActivity.class));
        super.onBackPressed();
    }


}
