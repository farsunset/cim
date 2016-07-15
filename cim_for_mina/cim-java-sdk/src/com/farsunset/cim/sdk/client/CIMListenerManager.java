/**
 * probject:cim-java-sdk
 * @version 2.0.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.sdk.client;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.farsunset.cim.sdk.client.model.Message;
import com.farsunset.cim.sdk.client.model.ReplyBody;

 
/**
 * CIM 消息监听器管理
 */
public class CIMListenerManager  {

	private static ArrayList<CIMEventListener> cimListeners = new ArrayList<CIMEventListener>();
	
	protected static final Logger logger = Logger.getLogger(CIMListenerManager.class);


	public static void registerMessageListener(CIMEventListener listener) {

		if (!cimListeners.contains(listener)) {
			cimListeners.add(listener);
		}
	}

	
	public static void removeMessageListener(CIMEventListener listener) {
		for (int i = 0; i < cimListeners.size(); i++) {
			if (listener.getClass() == cimListeners.get(i).getClass()) {
				cimListeners.remove(i);
			}
		}
	}
	
	public static void notifyOnConnectionSuccessed(boolean antoBind) {
		for (CIMEventListener listener : cimListeners) {
			listener.onConnectionSuccessed(antoBind);
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
			logger.debug("#######" + listener.getClass().getName() + "#######" );
		}
	}
 
}