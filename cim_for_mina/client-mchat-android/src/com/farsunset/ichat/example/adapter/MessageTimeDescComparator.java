/**
 * probject:cim
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.ichat.example.adapter;

import java.util.Comparator;

import com.farsunset.cim.sdk.android.model.Message;

public class MessageTimeDescComparator  implements Comparator<Message>{

	@Override
	public int compare(Message arg0, Message arg1) {
		 
		return (int) (arg1.getTimestamp() - arg0.getTimestamp()) ;
	}

	 

}
