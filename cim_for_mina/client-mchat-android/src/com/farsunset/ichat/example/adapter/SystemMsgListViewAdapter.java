/**
 * probject:cim
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.ichat.example.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.farsunset.cim.sdk.android.model.Message;
import com.farsunset.ichat.example.R;
import com.farsunset.ichat.example.ui.SystemMessageActivity;


public class SystemMsgListViewAdapter extends BaseAdapter {

	protected List<Message> list;
	protected SystemMessageActivity scactivity;
    int faceSize = 30;
    
	public SystemMsgListViewAdapter(SystemMessageActivity context, List<Message> list) {
		super();
		this.scactivity = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Message getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public void notifyDataSetChanged()
	{
		//Collections.sort(list, new MessageTimeDescComparator());
	}
	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View chatItemView, ViewGroup parent) {
		
		final Message msg = getItem(position);
		
		chatItemView =LayoutInflater.from(scactivity).inflate(R.layout.item_chat_sysmsg, null);
		
		
		((TextView) chatItemView.findViewById(R.id.textMsgType)).setText("系统消息");
		
		((TextView) chatItemView.findViewById(R.id.time)).setText(getDateTimeString(Long.valueOf(msg.getTimestamp())));
		
		((TextView) chatItemView.findViewById(R.id.content)).setText(msg.getContent() );
		((ImageView) chatItemView.findViewById(R.id.headImageView)).setImageResource(R.drawable.icon_recent_sysmsg);
		 
		return chatItemView;
	}

	public List<Message> getList() {
		return list;
	}

	public void setList(List<Message> list) {
		this.list = list;
	}
	public static String getDateTimeString(long t)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
		return  sdf.format(new Date(t));
	}
	
}
