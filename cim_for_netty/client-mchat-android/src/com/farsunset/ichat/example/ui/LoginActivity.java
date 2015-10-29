package com.farsunset.ichat.example.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.farsunset.cim.client.android.CIMPushManager;
import com.farsunset.cim.client.constant.CIMConstant;
import com.farsunset.cim.client.model.ReplyBody;
import com.farsunset.ichat.example.R;
import com.farsunset.ichat.example.app.CIMMonitorActivity;

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
			
			
			CIMPushManager.bindAccount(this, accountEdit.getText().toString().trim());
		}

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

		CIMPushManager.destory(this);
		this.finish();
	}

	 
}
