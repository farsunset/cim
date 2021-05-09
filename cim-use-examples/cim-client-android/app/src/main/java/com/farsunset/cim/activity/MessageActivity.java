package com.farsunset.cim.activity;

import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.farsunset.cim.R;
import com.farsunset.cim.adapter.MessageListAdapter;
import com.farsunset.cim.dialog.SendMessageDialog;
import com.farsunset.cim.sdk.android.CIMEventListener;
import com.farsunset.cim.sdk.android.CIMListenerManager;
import com.farsunset.cim.sdk.android.CIMPushManager;
import com.farsunset.cim.sdk.android.model.Message;
import com.farsunset.cim.sdk.android.model.ReplyBody;
import com.farsunset.cim.sdk.android.model.SentBody;

public class MessageActivity extends AppCompatActivity implements CIMEventListener {

    private MessageListAdapter adapter;

    private SendMessageDialog sendMessageDialog;

    private RecyclerView messageListView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle("消息显示面板");
        toolbar.setSubtitle("在管理后台或者调用接口发送消息");

        messageListView = findViewById(R.id.recyclerView);
        messageListView.setLayoutManager(new LinearLayoutManager(this));
        messageListView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MessageListAdapter();
        messageListView.setAdapter(adapter);
        sendMessageDialog = new SendMessageDialog(this,getIntent().getStringExtra("uid"));

        CIMListenerManager.registerMessageListener(this);
    }

    @Override
    public void onMessageReceived(Message message) {
        adapter.add(message);
        messageListView.smoothScrollToPosition(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        /*
         * 页面关闭记得调用此方法喔
         */
        CIMListenerManager.removeMessageListener(this);

        /*
         * 退出应用记得调用此方法喔
         */
        CIMPushManager.destroy(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_send) {
            sendMessageDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onReplyReceived(ReplyBody replyBody) {

    }

    @Override
    public void onSendFinished(SentBody sentBody) {

    }

    @Override
    public void onNetworkChanged(NetworkInfo networkInfo) {

    }

    @Override
    public void onConnectFinished(boolean b) {

    }

    @Override
    public void onConnectionClosed() {
    }

    @Override
    public void onConnectFailed() {}

    @Override
    public int getEventDispatchOrder() {
        return 0;
    }
}