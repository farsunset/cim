/**
 * Copyright 2013-2033 Xia Jun(3979434@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***************************************************************************************
 *                                                                                     *
 *                        Website : http://www.farsunset.com                           *
 *                                                                                     *
 ***************************************************************************************
 */
package com.farsunset.cim.sdk.android;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.exception.SessionClosedException;
import com.farsunset.cim.sdk.android.exception.NetworkDisabledException;
import com.farsunset.cim.sdk.android.filter.ClientMessageCodecFactory;
import com.farsunset.cim.sdk.android.model.HeartbeatRequest;
import com.farsunset.cim.sdk.android.model.HeartbeatResponse;
import com.farsunset.cim.sdk.android.model.Message;
import com.farsunset.cim.sdk.android.model.ReplyBody;
import com.farsunset.cim.sdk.android.model.SentBody;

/**
 * 连接服务端管理，cim核心处理类，管理连接，以及消息处理
 * 
 * @author 3979434@qq.com
 */
class CIMConnectorManager  extends IoHandlerAdapter implements KeepAliveMessageFactory {

	private final String TAG = CIMConnectorManager.class.getSimpleName();
	private final int READ_BUFFER_SIZE = 2048;//bit
	private final int CONNECT_TIMEOUT = 10 * 1000;//秒
	private final int WRITE_TIMEOUT = 10 * 1000;//秒

	private final int READ_IDLE_TIME = 120;//秒
	private final int HEARBEAT_TIME_OUT = (READ_IDLE_TIME + 10) * 1000;// 收到服务端心跳请求超时时间 毫秒
	private final String KEY_LAST_HEART_TIME =  "KEY_LAST_HEART_TIME" ;
		
	private NioSocketConnector connector;
	private ConnectFuture connectFuture;
	private ExecutorService executor = Executors.newFixedThreadPool(1);
	private Context context;
	
	private static CIMConnectorManager manager;

	

	private CIMConnectorManager(Context ctx) {
		context = ctx;

		connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
		connector.getSessionConfig().setTcpNoDelay(true);
		connector.getSessionConfig().setKeepAlive(true);
		connector.getSessionConfig().setReadBufferSize(READ_BUFFER_SIZE);
		
		KeepAliveFilter keepAliveaHandler = new KeepAliveFilter(this);
		keepAliveaHandler.setRequestInterval(READ_IDLE_TIME);
		keepAliveaHandler.setRequestTimeoutHandler(KeepAliveRequestTimeoutHandler.NOOP);
		keepAliveaHandler.setForwardEvent(true);
		
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ClientMessageCodecFactory()));
		connector.getFilterChain().addLast("heartbeat", keepAliveaHandler);
		
		connector.setHandler(this);

	}

	public synchronized static CIMConnectorManager getManager(Context context) {
		if (manager == null) {
			manager = new CIMConnectorManager(context);
		}
		return manager;

	}

	private  synchronized void syncConnection(final String host,final int port) {
		
		if(isConnected()){
			return ;
		}
		
		try {

			Log.i(TAG, "****************CIM正在连接服务器  "+host+":"+port+"......");
			
			CIMCacheManager.putBoolean(context,CIMCacheManager.KEY_CIM_CONNECTION_STATE, false);
			InetSocketAddress remoteSocketAddress = new InetSocketAddress(host, port);
			connectFuture = connector.connect(remoteSocketAddress);
			connectFuture.awaitUninterruptibly();
			connectFuture.getSession();
		} catch (Exception e) {
			
			long interval = CIMConstant.RECONN_INTERVAL_TIME - (5*1000 - new Random().nextInt(15*1000));

			Intent intent = new Intent();
			intent.setAction(CIMConstant.IntentAction.ACTION_CONNECTION_FAILED);
			intent.putExtra(Exception.class.getName(), e.getClass().getSimpleName());
			intent.putExtra("interval", interval);
			context.sendBroadcast(intent);
			
			Log.e(TAG, "****************CIM连接服务器失败  "+host+":"+port+"......将在"+interval/1000+"秒后重新尝试连接");
		 
		}  

	}

	public void connect(final String host, final int port) {

		if (!isNetworkConnected(context)) {
			
			Intent intent = new Intent();
			intent.setAction(CIMConstant.IntentAction.ACTION_CONNECTION_FAILED);
			intent.putExtra(Exception.class.getName(),NetworkDisabledException.class.getSimpleName());
			context.sendBroadcast(intent);
			
			return;
		}
	
		executor.execute(new Runnable() {
			public void run() 
			{
				syncConnection(host, port);
		    }
		});
		 
	}

	public synchronized void send(SentBody body) {

		boolean isSuccessed = false;
		
		String exceptionName =SessionClosedException.class.getSimpleName();
		
		IoSession session = getCurrentSession();
		if(session!=null && session.isConnected())
		{
			WriteFuture wf = session.write(body);
			// 消息发送超时 5秒
			wf.awaitUninterruptibly(WRITE_TIMEOUT);
			isSuccessed = wf.isWritten();
			if(wf.getException() != null)
			{
				exceptionName =wf.getException().getClass().getSimpleName();
			}
		} 
		
		if(!isSuccessed){
			Intent intent = new Intent();
			intent.setAction(CIMConstant.IntentAction.ACTION_SENT_FAILED);
			intent.putExtra(Exception.class.getName(),exceptionName);
			intent.putExtra(SentBody.class.getName(), body);
			context.sendBroadcast(intent);
		}
		
	}

	public   void destroy() {
		IoSession session = getCurrentSession();
		if (session != null) {
			session.closeNow();
		}

		if (connector != null && !connector.isDisposed()) {
			connector.dispose();
		}
		
		manager = null;
	}

	public boolean isConnected() {
		IoSession session = getCurrentSession();
		return session != null ;
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
		Map<Long,IoSession> sessions = connector.getManagedSessions();
		for(Long key:sessions.keySet())
		{
			IoSession session = sessions.get(key);
			if(session.isConnected()){
				return session;
			}
		}
		return null;
	}

	
	@Override
	public void sessionCreated(IoSession session) throws Exception {

		
		Log.i(TAG, "****************CIM连接服务器成功:"+session.getLocalAddress()+" NID:"+session.getId());
		
		setLastHeartbeatTime(session);
		
		Intent intent = new Intent();
		intent.setAction(CIMConstant.IntentAction.ACTION_CONNECTION_SUCCESSED);
		context.sendBroadcast(intent);

	}
	 
	@Override
	public void sessionClosed(IoSession session) {

		Log.e(TAG, "****************CIM与服务器断开连接:"+session.getLocalAddress()+" NID:"+session.getId());
		 
		Intent intent = new Intent();
		intent.setAction(CIMConstant.IntentAction.ACTION_CONNECTION_CLOSED);
		context.sendBroadcast(intent);

	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)  {
		Log.d(TAG, "****************CIM "+status.toString().toUpperCase()+":"+session.getLocalAddress() +" NID:"+session.getId()+  " isConnected:" + session.isConnected());
		
		/**
		 * 用于解决，wifi情况下。偶而路由器与服务器断开连接时，客户端并没及时收到关闭事件
		 * 导致这样的情况下当前连接无效也不会重连的问题
		 * 
		 */
		long lastHeartbeatTime = getLastHeartbeatTime(session);
		if(System.currentTimeMillis() - lastHeartbeatTime >= HEARBEAT_TIME_OUT)
		{
			session.closeNow();
			Log.e(TAG, "****************CIM心跳超时 ,即将重新连接......"+" NID:"+session.getId());
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		
		Log.e(TAG, "****************CIM连接出现未知异常:"+session.getLocalAddress()+" NID:"+session.getId());
		
		if(cause!=null && cause.getMessage()!=null){
			Log.e(TAG, cause.getMessage());
		}
	}

	@Override
	public void messageReceived(IoSession session, Object obj){
		
		if (obj instanceof Message) {

			Intent intent = new Intent();
			intent.setAction(CIMConstant.IntentAction.ACTION_MESSAGE_RECEIVED);
			intent.putExtra(Message.class.getName(), (Message) obj);
			context.sendBroadcast(intent);
		 
		}
		if (obj instanceof ReplyBody) {
			
			Intent intent = new Intent();
			intent.setAction(CIMConstant.IntentAction.ACTION_REPLY_RECEIVED);
			intent.putExtra(ReplyBody.class.getName(), (ReplyBody) obj);
			context.sendBroadcast(intent);
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) {
		if(message instanceof SentBody)
		{
			Intent intent = new Intent();
			intent.setAction(CIMConstant.IntentAction.ACTION_SENT_SUCCESSED);
			intent.putExtra(SentBody.class.getName(), (SentBody) message);
			context.sendBroadcast(intent);
		}
	}
	
	
	private void setLastHeartbeatTime(IoSession session)
	{
		session.setAttribute(KEY_LAST_HEART_TIME, System.currentTimeMillis());
	}
	
	private long  getLastHeartbeatTime(IoSession session)
	{
		long time = 0;
		Object value = session.getAttribute(KEY_LAST_HEART_TIME);
		if(value !=null){
			time = Long.parseLong(value.toString());
		}
		return time;
	}
	
	public static boolean isNetworkConnected(Context context) {
		try {
			ConnectivityManager nw = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = nw.getActiveNetworkInfo();
			return networkInfo != null;
		} catch (Exception e) {}

		return false;
	}

	
	@Override
	public Object getRequest(IoSession arg0) {
		return null;
	}

	@Override
	public Object getResponse(IoSession session, Object arg1) {
		return HeartbeatResponse.getInstance();
	}

	@Override
	public boolean isRequest(IoSession session, Object data) {
		
		setLastHeartbeatTime(session);
		
		return data instanceof HeartbeatRequest;
	}

	@Override
	public boolean isResponse(IoSession arg0, Object arg1) {
		return false;
	}
}
