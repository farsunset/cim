package com.farsunset.cim.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.farsunset.cim.BuildConfig;
import com.farsunset.cim.R;
import com.farsunset.cim.sdk.android.CIMEventListener;
import com.farsunset.cim.sdk.android.CIMListenerManager;
import com.farsunset.cim.sdk.android.CIMPushManager;
import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.constant.RequestKey;
import com.farsunset.cim.sdk.android.model.Message;
import com.farsunset.cim.sdk.android.model.ReplyBody;
import com.farsunset.cim.sdk.android.model.SentBody;
import com.farsunset.cim.widget.RainbowBallView;

public class LoginActivity extends AppCompatActivity implements CIMEventListener {

    private EditText uidEdit;

    private ProgressDialog progressDialog;

    private RainbowBallView ballsView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowFullscreen();
        setStatusBarColor(0x40000000);
        setContentView(R.layout.activity_login);
        ((TextView)findViewById(R.id.host)).setText(BuildConfig.CIM_SERVER_HOST + ":" + BuildConfig.CIM_SERVER_PORT);
        uidEdit = findViewById(R.id.uid);
        ballsView = findViewById(R.id.balls);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在连接......");

        CIMListenerManager.registerMessageListener(this);
    }

    public void onLoginButtonClicked(View view){

        if (uidEdit.getText().toString().trim().equals("")){
            return;
        }

        progressDialog.show();
        /*
         * 第一步 连接cim推送服务端
         */
        CIMPushManager.connect(this,BuildConfig.CIM_SERVER_HOST,BuildConfig.CIM_SERVER_PORT);

    }


    public void setStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    public void setWindowFullscreen(){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        CIMListenerManager.removeMessageListener(this);
    }
    @Override
    public void onMessageReceived(Message message) {

    }

    @Override
    public void onReplyReceived(ReplyBody replyBody) {

        progressDialog.dismiss();
        /*
         *第三步 用户id绑定成功，可以接收消息了
         */
        if (replyBody.getKey().equals(RequestKey.CLIENT_BIND)) {
            ballsView.runaway();
            Intent intent = new Intent(this,MessageActivity.class);
            intent.putExtra("uid",uidEdit.getText().toString().trim());
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onSendFinished(SentBody sentBody) {

    }

    @Override
    public void onNetworkChanged(NetworkInfo networkInfo) {

    }

    /**
     * 连接cim推送服务端成功回调
     * @param b
     */
    @Override
    public void onConnectFinished(boolean b) {
        /*
         * 第二步 绑定用户id到长连接
         * 这里业务方可以设置token等由后端实现转换成用户id
         */
        CIMPushManager.bind(this,uidEdit.getText().toString());
    }

    @Override
    public void onConnectionClosed() {

    }

    /**
     * 连接cim推送服务端失败回调
     * @param
     */
    @Override
    public void onConnectFailed() {
        progressDialog.dismiss();
        Toast.makeText(this,"连接失败! 请检查Host和Port是否正确，",Toast.LENGTH_LONG).show();

    }

    @Override
    public int getEventDispatchOrder() {
        return 0;
    }
}