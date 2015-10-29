package com.farsunset.ichat.example.adapter;

import java.util.Comparator;

import com.farsunset.cim.client.model.Message;

public class MessageTimeDescComparator  implements Comparator<Message>{

	@Override
	public int compare(Message arg0, Message arg1) {
		 
		return (int) (arg1.getTimestamp() - arg0.getTimestamp()) ;
	}

	 

}
