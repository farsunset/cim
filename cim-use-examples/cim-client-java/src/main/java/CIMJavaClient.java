import com.farsunset.cim.sdk.client.CIMEventBroadcastReceiver;
import com.farsunset.cim.sdk.client.CIMEventListener;
import com.farsunset.cim.sdk.client.CIMPushManager;
import com.farsunset.cim.sdk.client.model.Message;
import com.farsunset.cim.sdk.client.model.ReplyBody;
import com.farsunset.cim.sdk.client.model.SentBody;

public class CIMJavaClient implements CIMEventListener {
 	public static void startup() {
		/*
		 * 第一步 设置运行时参数
		 */
		CIMPushManager.setClientVersion("1.0.0");


		/*
		 * 第二步 设置全局的事件监听器
		 */
		CIMEventBroadcastReceiver.getInstance().setGlobalCIMEventListener(new CIMJavaClient());
		
		/*
		 * 第三步 连接到服务器
		 */
		CIMPushManager.connect("127.0.0.1", 23456);
		 
	}
 

	@Override
	public void onConnectionClosed() {
		System.out.println("onConnectionClosed");
		/*
		 * 在此可以将事件分发到各个监听了CIMEventBroadcastReceiver的地方
		 * 第一步 连接到服务器 在需要监听事件的类调用CIMListenerManager.registerMessageListener(listener);
		 * 第二部 在此调用CIMListenerManager.notifyOnConnectionClosed()
		 */
	}



	@Override
	public void onConnectFailed() {
		System.out.println("onConnectionFailed");
		/*
		 * 在此可以将事件分发到各个监听了CIMEventBroadcastReceiver的地方
		 * 第一步 连接到服务器 在需要监听事件的类调用CIMListenerManager.registerMessageListener(listener);
		 * 第二部 在此调用CIMListenerManager.notifyOnConnectionFailed(e)
		 */
	}

	@Override
	public void onConnectFinished(boolean hasAutoBind) {
		System.out.println("onConnectFinished");
		if(!hasAutoBind){
			CIMPushManager.bindAccount("10000");
		}
		/*
		 * 在此可以将事件分发到各个监听了CIMEventBroadcastReceiver的地方
		 * 第一步 连接到服务器 在需要监听事件的类调用CIMListenerManager.registerMessageListener(listener);
		 * 第二部 在此调用CIMListenerManager.onConnectFinished(hasAutoBind)
		 */
	}

	@Override
	public void onMessageReceived(Message message) {
		System.out.println(message.toString());
		/*
		 * 在此可以将事件分发到各个监听了CIMEventBroadcastReceiver的地方
		 * 第一步 连接到服务器 在需要监听事件的类调用CIMListenerManager.registerMessageListener(listener);
		 * 第二部 在此调用CIMListenerManager.notifyOnMessageReceived(message)
		 */
	}

	
	@Override
	public void onReplyReceived(ReplyBody replybody) {
		System.out.println(replybody.toString());
		/*
		 * 在此可以将事件分发到各个监听了CIMEventBroadcastReceiver的地方
		 * 第一步 连接到服务器 在需要监听事件的类调用CIMListenerManager.registerMessageListener(listener);
		 * 第二部 在此调用CIMListenerManager.notifyOnReplyReceived(replybody)
		 */
	}

	@Override
	public void onSendFinished(SentBody sentBody) {

	}
	@Override
	public int getEventDispatchOrder() {
		return 1;
	}

	public static void main(String[] a){
		startup();
	}




}
