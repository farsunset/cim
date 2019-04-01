/**
 * Copyright 2013-2019 Xia Jun(3979434@qq.com).
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
package com.farsunset.cim.sdk.android.filter;

import java.net.InetSocketAddress;
import java.net.SocketAddress;


import android.util.Log;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleStateEvent;
@Sharable
public class CIMLoggingHandler extends ChannelDuplexHandler{
	
	private final static String TAG = "CIM";
	private boolean debug = true;
	
	public static CIMLoggingHandler getLogger() {
		return LoggerHolder.logger;
	}
	
	private CIMLoggingHandler() {
		
	}
	
	private static class LoggerHolder{
		private static CIMLoggingHandler logger = new CIMLoggingHandler();
	}
	
	public void  debugMode(boolean mode) {
		debug = mode;
	}
	
	@Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        ctx.fireChannelUnregistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	if(debug) {
			Log.i(TAG,"OPENED" + getSessionInfo(ctx.channel()));
		}
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	if(debug) {
			Log.i(TAG,"CLOSED" + getSessionInfo(ctx.channel()));
		}
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	
    	if(debug) {
			Log.d(TAG,String.format("EXCEPTION" + getSessionInfo(ctx.channel()) + "\n%s", cause.getClass().getName() + ":" + cause.getMessage()));
		}
        
    	ctx.channel().close();
    }

    public void connectFailure(InetSocketAddress remoteAddress,long interval)  {
		if(debug) {
			Log.d(TAG,"CONNECT FAILURE TRY RECONNECT AFTER " + interval +"ms");
		}
	}
	
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    	if(debug && evt instanceof IdleStateEvent) {
			Log.d(TAG,String.format("IDLE %s" + getSessionInfo(ctx.channel()) , ((IdleStateEvent)evt).state().toString()));
		}
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.bind(localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx,SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
    	if(debug) {
			Log.d(TAG,"START CONNECT REMOTE HOST: " + remoteAddress.toString());
		} 
        ctx.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
       
        ctx.disconnect(promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.close(promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.deregister(promise);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if(debug) {
			Log.i(TAG,String.format("RECEIVED" + getSessionInfo(ctx.channel()) + "\n%s", msg.toString()));
		}
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    	if(debug) {
			Log.i(TAG,String.format("SENT" + getSessionInfo(ctx.channel()) + "\n%s", msg.toString()));
		}
        ctx.write(msg, promise);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelWritabilityChanged();
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    
    private String getSessionInfo(Channel session) {
		StringBuilder builder = new StringBuilder();
		if (session == null) {
			return "";
		}
		builder.append(" [");
		builder.append("id:").append(session.id().asShortText());
		
		if (session.localAddress() != null) {
			builder.append(" L:").append(session.localAddress().toString());
		}
		
		
		if (session.remoteAddress() != null) {
			builder.append(" R:").append(session.remoteAddress().toString());
		}
		builder.append("]");
		return builder.toString();
	}
    
    
    public void connectState(boolean isConnected)  {
		if(debug) {
			Log.i(TAG,"CONNECTED:" + isConnected);
		}
	}
	 
	public void connectState(boolean isConnected,boolean isManualStop,boolean isDestroyed)  {
		if(debug) {
			Log.i(TAG,"CONNECTED:" + isConnected + " STOPED:"+isManualStop+ " DESTROYED:"+isDestroyed);
		}
	}
}
