/**
 * Copyright 2013-2023 Xia Jun(3979434@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***************************************************************************************
 *                                                                                     *
 *                        Website : http://www.farsunset.com                           *
 *                                                                                     *
 ***************************************************************************************
 */
package com.farsunset.ichat.example.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.farsunset.cim.sdk.android.CIMPushManager;
import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.model.Message;
import com.farsunset.cim.sdk.android.model.SentBody;
import com.farsunset.ichat.example.R;
import com.farsunset.ichat.example.adapter.SystemMsgListViewAdapter;
import com.farsunset.ichat.example.app.CIMMonitorActivity;
import com.farsunset.ichat.example.app.Constant;

public class SystemMessageActivity extends CIMMonitorActivity implements OnClickListener{

	protected ListView chatListView;
	protected SystemMsgListViewAdapter adapter;
	private ArrayList<Message> list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_chat);
		initViews();

		//绑定账号成功，获取离线消息
		getOfflineMessage();
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
         
		 showToask("登录成功，请通过后台页面发送消息吧^_^");
	}
	
	//收到消息
	@Override
	public void onMessageReceived(Message message) {
		 
		if(message.getAction().equals(Constant.MessageType.TYPE_999))
		{
			  //返回登录页面，停止接受消息
		      CIMPushManager.stop(this);
		      
			  this.showToask("你被迫下线!");
			  Intent intent = new Intent(this, LoginActivity.class);
			  startActivity(intent);
			  this.finish();
		}else
		{
			MediaPlayer.create(this, R.raw.classic).start();
			list.add(message);
			adapter.notifyDataSetChanged();
			chatListView.setSelection(chatListView.getTop());
			
		}

	}
 
	//获取离线消息，代码示例，前提是服务端要实现此功能,建议使用http 接口拉去大量的离线消息
	private void getOfflineMessage()
	{
		SentBody sent = new SentBody();
		sent.setKey(CIMConstant.RequestKey.CLIENT_PULL_MESSAGE);
		sent.put("account", this.getIntent().getStringExtra("account"));
		CIMPushManager.sendRequest(this, sent);
	}
	
	@Override
	public   void onNetworkChanged(NetworkInfo info){
		
		if(info ==null)
		{
			showToask("网络已断开");
			
		}else
		{
			showToask("网络已恢复，重新连接....");
		}
		
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.TOP_BACK_BUTTON: {
				onBackPressed();
				break;
			}
		}
	}

	@Override
	public void onBackPressed() {
		
		//返回登录页面，停止接受消息
	    CIMPushManager.stop(this);
	    
	    Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		this.finish();
	}

	
}
