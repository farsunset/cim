/**
 * probject:cim-android-sdk
 * @version 2.1.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.sdk.android;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.model.Message;
import com.farsunset.cim.sdk.android.model.ReplyBody;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.util.Log;
 

/**
 * CIM 消息监听器管理
 */
public class CIMListenerManager  {

	private static ArrayList<CIMEventListener> cimListeners = new ArrayList<CIMEventListener>();
	
	

	public static void registerMessageListener(CIMEventListener listener,Context mcontext) {

		if (!cimListeners.contains(listener)) {
			cimListeners.add(listener);
			// 按照接收顺序倒序
			Collections.sort(cimListeners, new CIMMessageReceiveComparator(mcontext));
		}
	}

	
	public static void removeMessageListener(CIMEventListener listener) {
		for (int i = 0; i < cimListeners.size(); i++) {
			if (listener.getClass() == cimListeners.get(i).getClass()) {
				cimListeners.remove(i);
			}
		}
	}
	
	
	public static void notifyOnNetworkChanged(NetworkInfo info) {
		for (CIMEventListener listener : cimListeners) {
			listener.onNetworkChanged(info);
		}
	}
	
	
	public static void notifyOnConnectionSuccessed(boolean hasAutoBind) {
		for (CIMEventListener listener : cimListeners) {
			listener.onConnectionSuccessed(hasAutoBind);
		}
	}
	public static void notifyOnMessageReceived(Message message) {
		for (CIMEventListener listener : cimListeners) {
			listener.onMessageReceived(message);
		}
	}
	
	public static void notifyOnConnectionClosed() {
		for (CIMEventListener listener : cimListeners) {
			listener.onConnectionClosed();
		}
	}
	
	
	public static void notifyOnReplyReceived(ReplyBody body) {
		for (CIMEventListener listener : cimListeners) {
			listener.onReplyReceived(body);
		}
	}
	
	public static void notifyOnConnectionFailed(Exception e) {
		for (CIMEventListener listener : cimListeners) {
			listener.onConnectionFailed(e);
		}
	}
	
	public static void destory() {
		cimListeners.clear();
	}
	
	public static void logListenersName() {
		for (CIMEventListener listener : cimListeners) {
			Log.i(CIMEventListener.class.getSimpleName(),"#######" + listener.getClass().getName() + "#######" );
		}
	}
	
	/**
	 * 消息接收activity的接收顺序排序，CIM_RECEIVE_ORDER倒序
	 */
   static class CIMMessageReceiveComparator  implements Comparator<CIMEventListener>{

		Context mcontext;
		public CIMMessageReceiveComparator(Context ctx)
		{
			mcontext = ctx;
		}
		
		@Override
		public int compare(CIMEventListener arg1, CIMEventListener arg2) {
			 
			Integer order1  = CIMConstant.CIM_DEFAULT_MESSAGE_ORDER;
			Integer order2  = CIMConstant.CIM_DEFAULT_MESSAGE_ORDER;
			ActivityInfo info;
			if (arg1 instanceof Activity  ) {
				
				try {
					 info = mcontext.getPackageManager() .getActivityInfo(((Activity)(arg1)).getComponentName(), PackageManager.GET_META_DATA);
					 if(info.metaData!=null)
					 {
						 order1 = info.metaData.getInt("CIM_RECEIVE_ORDER");
					 }
					 
			     } catch (Exception e) {}
			}
			
			if (arg1 instanceof Activity ) {
				try {
					 info = mcontext.getPackageManager() .getActivityInfo(((Activity)(arg2)).getComponentName(), PackageManager.GET_META_DATA);
					 if(info.metaData!=null)
					 {
						 order2 = info.metaData.getInt("CIM_RECEIVE_ORDER");
					 }
					 
			     } catch (Exception e) {}
			}
			
			return order2.compareTo(order1);
		}
		 

	}

}