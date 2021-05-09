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
package com.farsunset.cim.dialog;


import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialog;

import com.farsunset.cim.R;
import com.farsunset.cim.http.SendMessageManager;
import com.farsunset.cim.sdk.android.model.Message;


public class SendMessageDialog extends AppCompatDialog implements View.OnClickListener, DialogInterface.OnShowListener {

    private final EditText textEdit;
    private final EditText uidTextEdit;
    private final String uid;

    public SendMessageDialog(Context context,String uid) {

        super(context);
        setContentView(R.layout.dialog_send_message);
        findViewById(R.id.leftButton).setOnClickListener(this);
        findViewById(R.id.rightButton).setOnClickListener(this);

        textEdit = findViewById(R.id.content);
        uidTextEdit = findViewById(R.id.uid);

        setCanceledOnTouchOutside(false);
        setOnShowListener(this);
        this.uid = uid;
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.leftButton) {
            dismiss();
            return;
        }

        if (!TextUtils.isEmpty(textEdit.getText()) && !TextUtils.isEmpty(uidTextEdit.getText()) ){
            Message message = new Message();
            /*
             * 暂定action为0
             */
            message.setAction("0");
            message.setSender(uid);
            message.setReceiver(uidTextEdit.getText().toString().trim());
            message.setContent(textEdit.getText().toString());
            SendMessageManager.send(message);
            dismiss();
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        textEdit.setSelection(textEdit.getText().length());
    }

}
