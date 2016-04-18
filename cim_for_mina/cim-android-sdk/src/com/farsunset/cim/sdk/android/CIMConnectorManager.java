/**
 * probject:cim-android-sdk
 * @version 2.0.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.sdk.android;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.exception.CIMSessionDisableException;
import com.farsunset.cim.sdk.android.exception.NetWorkDisableException;
import com.farsunset.cim.sdk.android.exception.WriteToClosedSessionException;
import com.farsunset.cim.sdk.android.filter.ClientMessageCodecFactory;
import com.farsunset.cim.sdk.android.model.Message;
import com.farsunset.cim.sdk.android.model.ReplyBody;
import com.farsunset.cim.sdk.android.model.SentBody;

/**
 * 连接服务端管理，cim核心处理类，管理连接，以及消息处理
 * 
 * @author 3979434@qq.com
 */
class CIMConnectorManager  {

	final static String TAG = CIMConnectorManager.class.getSimpleName();
	private  NioSocketConnector connector;
	private  ConnectFuture connectFuture;

	Context context;
   
	private final int BOTH_IDLE_TIME = 120;//秒
	
	private final int HEARBEAT_TIME_OUT = 330 * 1000;// 收到服务端心跳请求超时时间 毫秒
	
	private final String KEY_LAST_HEART_TIME =  "KEY_LAST_HEART_TIME" ;
	
	static CIMConnectorManager manager;

	// 消息广播action
	public static final String ACTION_MESSAGE_RECEIVED = "com.farsunset.cim.MESSAGE_RECEIVED";
	
	// 发送sendbody失败广播
	public static final String ACTION_SENT_FAILED = "com.farsunset.cim.SENT_FAILED";
	
	// 发送sendbody成功广播
	public static final String ACTION_SENT_SUCCESSED = "com.farsunset.cim.SENT_SUCCESSED";
	// 链接意外关闭广播
	public static final String ACTION_CONNECTION_CLOSED = "com.farsunset.cim.CONNECTION_CLOSED";
	// 链接失败广播
	public static final String ACTION_CONNECTION_FAILED = "com.farsunset.cim.CONNECTION_FAILED";
	// 链接成功广播
	public static final String ACTION_CONNECTION_SUCCESSED = "com.farsunset.cim.CONNECTION_SUCCESSED";
	// 发送sendbody成功后获得replaybody回应广播
	public static final String ACTION_REPLY_RECEIVED = "com.farsunset.cim.REPLY_RECEIVED";
	// 网络变化广播
	public static final String ACTION_NETWORK_CHANGED = "android.net.conn.CONNECTIVITY_CHANGE";
	
	// 未知异常
	public static final String ACTION_UNCAUGHT_EXCEPTION = "com.farsunset.cim.UNCAUGHT_EXCEPTION";

	//重试连接
	public final static String ACTION_CONNECTION_RECOVERY = "com.farsunset.cim.CONNECTION_RECOVERY";

	private ExecutorService executor;
	private CIMConnectorManager(Context ctx) {
		context = ctx;
		executor = Executors.newFixedThreadPool(1);

		connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(10 * 1000);
		connector.getSessionConfig().setTcpNoDelay(true);
		connector.getSessionConfig().setBothIdleTime(BOTH_IDLE_TIME);
		connector.getSessionConfig().setReadBufferSize(2048);
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ClientMessageCodecFactory()));
		
		KeepAliveFilter keepAliveaHandler = new KeepAliveFilter(new ClientKeepAliveFactoryImpl(),  IdleStatus.BOTH_IDLE);
		keepAliveaHandler.setForwardEvent(true);
		connector.getFilterChain().addLast("heartbeat", keepAliveaHandler);
		connector.setHandler(iohandler);
		
		 

	}

	public synchronized static CIMConnectorManager getManager(Context context) {
		if (manager == null) {
			manager = new CIMConnectorManager(context);
		}
		return manager;

	}

	private synchronized void  syncConnection(final String cimServerHost,final int cimServerPort) {
		try {

			if(isConnected()){
				return ;
			}
			
			CIMCacheToolkit.getInstance(context).putBoolean(CIMCacheToolkit.KEY_CIM_CONNECTION_STATE, false);
			InetSocketAddress remoteSocketAddress = new InetSocketAddress(cimServerHost, cimServerPort);
			connectFuture = connector.connect(remoteSocketAddress);
			connectFuture.awaitUninterruptibly();
			connectFuture.getSession();
		} catch (Exception e) {
			
			Intent intent = new Intent();
			intent.setAction(ACTION_CONNECTION_FAILED);
			intent.putExtra("exception", e);
			context.sendBroadcast(intent);
			
			Log.e(TAG, "******************CIM连接服务器失败  "+cimServerHost+":"+cimServerPort);
			
		}

	}

	public  void connect(final String cimServerHost, final int cimServerPort) {


		if (!netWorkAvailable(context)) {
			
			Intent intent = new Intent();
			intent.setAction(ACTION_CONNECTION_FAILED);
			intent.putExtra("exception", new NetWorkDisableException());
			context.sendBroadcast(intent);
			
			return;
		}
		
		Future<?> future = executor.submit(new Runnable() {
			@Override
			public void run() {
				syncConnection(cimServerHost, cimServerPort);
			}
		});
		try {
			if(future.get()!=null)
			{
				connect(cimServerHost,cimServerPort);
			}
		} catch (Exception e) {
			
			connect(cimServerHost,cimServerPort);
			e.printStackTrace();
		}  
		
		 
	}

	public void send(final SentBody body) {

		 
		executor.execute(new Runnable() {
			@Override
			public void run() {

				android.os.Message msg = new android.os.Message();
				msg.getData().putSerializable("body", body);
				
				IoSession session = getCurrentSession();
				if(session!=null && session.isConnected())
				{
					WriteFuture wf = session.write(body);
					// 消息发送超时 10秒
					wf.awaitUninterruptibly(5, TimeUnit.SECONDS);
					
					if (!wf.isWritten()) {

						
						Intent intent = new Intent();
						intent.setAction(ACTION_SENT_FAILED);
						intent.putExtra("exception", new WriteToClosedSessionException());
						intent.putExtra("sentBody", body);
						context.sendBroadcast(intent);
					}
				}else
				{
				 
					Intent intent = new Intent();
					intent.setAction(ACTION_SENT_FAILED);
					intent.putExtra("exception", new CIMSessionDisableException());
					intent.putExtra("sentBody", body);
					context.sendBroadcast(intent);
				}
			}
		});
	}

	public   void destroy() {
		IoSession session = getCurrentSession();
		if (session != null) {
			session.closeNow();
			session.removeAttribute("account");
		}

		if (connector != null && !connector.isDisposed()) {
			connector.dispose();
		}
		manager = null;
	}

	public boolean isConnected() {
		IoSession session = getCurrentSession();
		if (session == null ) {
			return false;
		}
		return session.isConnected() ;
	}

	
	
	public void closeSession()
	{
		IoSession session = getCurrentSession();
		if(session!=null)
		{
			session.closeNow();
		}
	}

	public IoSession getCurrentSession()
	{
		if(connector.getManagedSessionCount()>0)
		{
			for(Long key:connector.getManagedSessions().keySet())
			{
				return connector.getManagedSessions().get(key);
			}
		}
		
		return null;
	}

	IoHandlerAdapter iohandler = new IoHandlerAdapter() {

		@Override
		public void sessionCreated(IoSession session) throws Exception {

			
			Log.i(TAG, "******************CIM连接服务器成功:"+session.getLocalAddress());
			
			setLastHeartbeatTime(session);
			
			Intent intent = new Intent();
			intent.setAction(ACTION_CONNECTION_SUCCESSED);
			context.sendBroadcast(intent);

		}

		@Override
		public void sessionOpened(IoSession session) throws Exception {
			session.getConfig().setBothIdleTime(180);
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {

			Log.e(TAG, "******************CIM与服务器断开连接:"+session.getLocalAddress());
			 
			Intent intent = new Intent();
			intent.setAction(ACTION_CONNECTION_CLOSED);
			context.sendBroadcast(intent);

		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status)throws Exception {
			Log.d(TAG, "******************CIM与服务器连接空闲:"+session.getLocalAddress() + " isActive:" + session.isActive()+  " isConnected:" + session.isConnected());
			
			/**
			 * 用于解决，wifi情况下。偶而路由器与服务器断开连接时，客户端并没及时收到关闭事件
			 * 导致这样的情况下当前连接无效也不会重连的问题
			 * 
			 */
			long lastHeartbeatTime = getLastHeartbeatTime(session);
			if(System.currentTimeMillis() - lastHeartbeatTime >= HEARBEAT_TIME_OUT)
			{
				session.closeNow();
			}
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause)
				throws Exception {

			Intent intent = new Intent();
			intent.setAction(ACTION_UNCAUGHT_EXCEPTION);
			intent.putExtra("exception", cause);
			context.sendBroadcast(intent);
		}

		@Override
		public void messageReceived(IoSession session, Object obj)
				throws Exception {

			if (obj instanceof Message) {

				Intent intent = new Intent();
				intent.setAction(ACTION_MESSAGE_RECEIVED);
				intent.putExtra("message", (Message) obj);
				context.sendBroadcast(intent);
			 
			}
			if (obj instanceof ReplyBody) {

				
				Intent intent = new Intent();
				intent.setAction(ACTION_REPLY_RECEIVED);
				intent.putExtra("replyBody", (ReplyBody) obj);
				context.sendBroadcast(intent);
			}
		}

		@Override
		public void messageSent(IoSession session, Object message) throws Exception {
			if(message instanceof SentBody)
			{
				Intent intent = new Intent();
				intent.setAction(ACTION_SENT_SUCCESSED);
				intent.putExtra("sentBody", (SentBody) message);
				context.sendBroadcast(intent);
			}
		}
	};
	
	
	private void setLastHeartbeatTime(IoSession session)
	{
		session.setAttribute(KEY_LAST_HEART_TIME, System.currentTimeMillis());
	}
	
	private long  getLastHeartbeatTime(IoSession session)
	{
		long time = 0;
		Object value ;
		if((value = session.getAttribute(KEY_LAST_HEART_TIME)) !=null){
			time = Long.parseLong(value.toString());
		}
		return time;
	}
	
	public static boolean netWorkAvailable(Context context) {
		try {
			ConnectivityManager nw = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = nw.getActiveNetworkInfo();
			return networkInfo != null;
		} catch (Exception e) {}

		return false;
	}

	public class ClientKeepAliveFactoryImpl implements KeepAliveMessageFactory {

		@Override
		public Object getRequest(IoSession arg0) {
			return null;
		}

		@Override
		public Object getResponse(IoSession arg0, Object arg1) {
			return CIMConstant.CMD_HEARTBEAT_RESPONSE;
		}

		@Override
		public boolean isRequest(IoSession session, Object data) {
			
			setLastHeartbeatTime(session);
			
			return CIMConstant.CMD_HEARTBEAT_REQUEST.equalsIgnoreCase(data.toString());
		}

		@Override
		public boolean isResponse(IoSession arg0, Object arg1) {
			return false;
		}

	}
}