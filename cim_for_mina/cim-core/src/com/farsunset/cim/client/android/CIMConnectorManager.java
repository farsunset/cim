package com.farsunset.cim.client.android;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.farsunset.cim.nio.constant.CIMConstant;
import com.farsunset.cim.nio.filter.ClientMessageCodecFactory;
import com.farsunset.cim.nio.mutual.Message;
import com.farsunset.cim.nio.mutual.ReplyBody;
import com.farsunset.cim.nio.mutual.SentBody;

/**
 * 连接服务端管理，cim核心处理类，管理连接，以及消息处理
 * 
 * @author 3979434@qq.com
 */
class CIMConnectorManager  {

	private  NioSocketConnector connector;
	private  ConnectFuture connectFuture;
	private  IoSession session;

	Context context;


	static CIMConnectorManager manager;

	// 消息广播action
	public static final String ACTION_MESSAGE_RECEIVED = "com.farsunset.cim.MESSAGE_RECEIVED";
	
	// 发送sendbody失败广播
	public static final String ACTION_SENT_FAILED = "com.farsunset.cim.SENT_FAILED";
	
	// 发送sendbody成功广播
	public static final String ACTION_SENT_SUCCESS = "com.farsunset.cim.SENT_SUCCESS";
	// 链接意外关闭广播
	public static final String ACTION_CONNECTION_CLOSED = "com.farsunset.cim.CONNECTION_CLOSED";
	// 链接失败广播
	public static final String ACTION_CONNECTION_FAILED = "com.farsunset.cim.CONNECTION_FAILED";
	// 链接成功广播
	public static final String ACTION_CONNECTION_SUCCESS = "com.farsunset.cim.CONNECTION_SUCCESS";
	// 发送sendbody成功后获得replaybody回应广播
	public static final String ACTION_REPLY_RECEIVED = "com.farsunset.cim.REPLY_RECEIVED";
	// 网络变化广播
	public static final String ACTION_NETWORK_CHANGED = "android.net.conn.CONNECTIVITY_CHANGE";
	
	// 未知异常
	public static final String ACTION_UNCAUGHT_EXCEPTION = "com.farsunset.cim.UNCAUGHT_EXCEPTION";

	// CIM连接状态
	public static final String ACTION_CONNECTION_STATUS = "com.farsunset.cim.CONNECTION_STATUS";


	private ExecutorService executor;


	private CIMConnectorManager(Context ctx) {
		context = ctx;
		executor = Executors.newFixedThreadPool(3);

		connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(10 * 1000);
		connector.getSessionConfig().setBothIdleTime(180);
		connector.getSessionConfig().setKeepAlive(true);
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ClientMessageCodecFactory()));
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
			
			InetSocketAddress remoteSocketAddress = new InetSocketAddress(cimServerHost, cimServerPort);
			connectFuture = connector.connect(remoteSocketAddress);
			connectFuture.awaitUninterruptibly();
			session = connectFuture.getSession();

		} catch (Exception e) {
			
			Intent intent = new Intent();
			intent.setAction(ACTION_CONNECTION_FAILED);
			intent.putExtra("exception", e);
			context.sendBroadcast(intent);
			
			System.out.println("******************CIM连接服务器失败  "+cimServerHost+":"+cimServerPort);
			
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
		if (manager.session != null) {
			manager.session.close(false);
			manager.session.removeAttribute("account");
		}

		if (manager.connector != null && !manager.connector.isDisposed()) {
			manager.connector.dispose();
		}
		manager = null;
	}

	public boolean isConnected() {
		if (session == null || connector == null) {
			return false;
		}
		return session.isConnected() ;
	}

	public void deliverIsConnected() {
		Intent intent = new Intent();
		intent.setAction(ACTION_CONNECTION_FAILED);
		intent.putExtra(CIMPushManager.KEY_CIM_CONNECTION_STATUS, isConnected());
		context.sendBroadcast(intent);
	}

	
	
	public void closeSession()
	{
		if(session!=null)
		{
			session.close(false);
		}
	}


	IoHandlerAdapter iohandler = new IoHandlerAdapter() {

		@Override
		public void sessionCreated(IoSession session) throws Exception {

			System.out.println("******************CIM连接服务器成功:"+session.getLocalAddress());
			
			Intent intent = new Intent();
			intent.setAction(ACTION_CONNECTION_SUCCESS);
			context.sendBroadcast(intent);

		}

		@Override
		public void sessionOpened(IoSession session) throws Exception {
			session.getConfig().setBothIdleTime(180);
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {

			System.out.println("******************CIM与服务器断开连接:"+session.getLocalAddress());
			if(CIMConnectorManager.this.session.getId()==session.getId())
			{
			   
			   Intent intent = new Intent();
			   intent.setAction(ACTION_CONNECTION_CLOSED);
			   context.sendBroadcast(intent);

			}
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status)
				throws Exception {

			System.out.println("******************CIM与服务器连接空闲:"+session.getLocalAddress());
			SentBody sent = new SentBody();
			sent.setKey(CIMConstant.RequestKey.CLIENT_HEARTBEAT);
			send(sent);
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
		public void messageSent(IoSession session, Object message)
				throws Exception {

			Intent intent = new Intent();
			intent.setAction(ACTION_SENT_SUCCESS);
			intent.putExtra("sentBody", (SentBody) message);
			context.sendBroadcast(intent);
			
 
		}
	};
	
	public static boolean netWorkAvailable(Context context) {
		try {
			ConnectivityManager nw = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = nw.getActiveNetworkInfo();
			return networkInfo != null;
		} catch (Exception e) {}

		return false;
	}

	
}