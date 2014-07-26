package com.farsunset.cim.client.android;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
 

/**
 * CIM 消息监听器管理
 * 
 * @author 3979434@qq.com
 */
public class CIMListenerManager  {

	private static ArrayList<OnCIMMessageListener> cimListeners = new ArrayList<OnCIMMessageListener>();
	
	

	public static void registerMessageListener(OnCIMMessageListener listener,Context mcontext) {

		if (!cimListeners.contains(listener)) {
			cimListeners.add(listener);
			// 按照接收顺序倒序
			Collections.sort(cimListeners, new CIMMessageReceiveComparator(mcontext));
		}
	}

	
	public static void removeMessageListener(OnCIMMessageListener listener) {
		for (int i = 0; i < cimListeners.size(); i++) {
			if (listener.getClass() == cimListeners.get(i).getClass()) {
				cimListeners.remove(i);
			}
		}
	}
	
	public static ArrayList<OnCIMMessageListener> getCIMListeners() {
		return cimListeners;
	}
}