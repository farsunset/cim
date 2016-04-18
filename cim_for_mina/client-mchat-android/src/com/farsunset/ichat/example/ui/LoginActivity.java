/**
 * probject:cim
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.ichat.example.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.farsunset.ichat.example.R;
import com.farsunset.ichat.example.app.CIMMonitorActivity;
import com.farsunset.ichat.example.app.Constant;
import com.farsunset.cim.sdk.android.CIMPushManager;
import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.model.ReplyBody;
public class LoginActivity extends CIMMonitorActivity implements
		OnClickListener {

	EditText accountEdit;
	Button loginButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initViews();
	}

	private void initViews() {

		accountEdit = (EditText) this.findViewById(R.id.account);
		loginButton = (Button) this.findViewById(R.id.login);
		loginButton.setOnClickListener(this);

	}

	private void doLogin() {

		if (!"".equals(accountEdit.getText().toString().trim())) {
			showProgressDialog("提示", "正在登陆，请稍后......");
			if(CIMPushManager.getState(this) == CIMPushManager.STATE_NORMAL )
			{
				CIMPushManager.bindAccount(this, accountEdit.getText().toString().trim());
				return;
			}
			if(CIMPushManager.getState(this) == CIMPushManager.STATE_STOPED )
			{
				CIMPushManager.connect(this,Constant.CIM_SERVER_HOST, Constant.CIM_SERVER_PORT);
			}
			
		}

	}

	@Override
	public void onConnectionSuccessed(boolean autoBind) {
		if(!autoBind)
		CIMPushManager.bindAccount(this, accountEdit.getText().toString().trim());
	}
	
	
	@Override
	public void onReplyReceived(final ReplyBody reply) {

		if (reply.getKey().equals(CIMConstant.RequestKey.CLIENT_BIND)) {

			if (reply.getCode().equals(CIMConstant.ReturnCode.CODE_200)) {

				hideProgressDialog();
				Intent intent = new Intent(this, SystemMessageActivity.class);
				intent.putExtra("account", accountEdit.getText().toString().trim());
				startActivity(intent);
				this.finish();
			}
		}

	}

	 

	 

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.login:
			doLogin();
			break;
		}

	}

	@Override
	public void onBackPressed() {

		CIMPushManager.destroy(this);
		this.finish();
	}

	 
}
