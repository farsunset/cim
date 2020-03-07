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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.farsunset.cim.sdk.android.CIMPushManager;
import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.model.ReplyBody;
import com.farsunset.ichat.example.R;
import com.farsunset.ichat.example.app.CIMMonitorActivity;
import com.farsunset.ichat.example.app.Constant;

public class LoginActivity extends CIMMonitorActivity implements OnClickListener {

    EditText accountEdit;
    Button loginButton;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    private void initViews() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在登录，请稍候......");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        accountEdit = (EditText) this.findViewById(R.id.account);
        loginButton = (Button) this.findViewById(R.id.login);
        loginButton.setOnClickListener(this);

    }

    private void doLogin() {

        if (!"".equals(accountEdit.getText().toString().trim())) {
            progressDialog.show();
            if (CIMPushManager.isConnected(this)) {
                CIMPushManager.bindAccount(this, accountEdit.getText().toString().trim());
            } else {
                CIMPushManager.connect(this, Constant.CIM_SERVER_HOST, Constant.CIM_SERVER_PORT);
            }

        }
    }

    @Override
    public void onConnectFinished(boolean autoBind) {
        if (!autoBind)
            CIMPushManager.bindAccount(this, accountEdit.getText().toString().trim());
    }


    @Override
    public void onReplyReceived(final ReplyBody reply) {
        progressDialog.dismiss();
        /*
         * 收到code为200的回应 账号绑定成功
         */
        if (reply.getKey().equals(CIMConstant.RequestKey.CLIENT_BIND) && reply.getCode().equals("200")) {
                Intent intent = new Intent(this, SystemMessageActivity.class);
                intent.putExtra("account", accountEdit.getText().toString().trim());
                startActivity(intent);
                this.finish();
        }
    }


    @Override
    public void onClick(View v) {
        doLogin();
    }

    @Override
    public void onBackPressed() {
       CIMPushManager.destroy(this);
       super.onBackPressed();
    }


}
