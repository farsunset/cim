/*
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
package com.farsunset.cim.sdk.android;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import com.farsunset.cim.sdk.android.coder.ClientMessageDecoder;
import com.farsunset.cim.sdk.android.coder.ClientMessageEncoder;
import com.farsunset.cim.sdk.android.constant.BundleKey;
import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.constant.IntentAction;
import com.farsunset.cim.sdk.android.logger.CIMLogger;
import com.farsunset.cim.sdk.android.model.*;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * 连接服务端管理，cim核心处理类，管理连接，以及消息处理
 */
class CIMConnectManager {

    private static final int READ_BUFFER_SIZE = 2048;

    private static final int WRITE_BUFFER_SIZE = 1024;

    private static final int CONNECT_TIME_OUT = 5 * 1000;

    /*
     服务端在连接写空闲120秒的时候发送心跳请求给客户端，所以客户端在空闲150秒后都没有收到任何数据，则关闭链接，并重新创建
     */
    private static final int CONNECT_ALIVE_TIME_OUT = 120 * 1000;

    private static final CIMLogger LOGGER = CIMLogger.getLogger();

    private SocketChannel socketChannel;

    private final Context context;

    private final ByteBuffer headerBuffer = ByteBuffer.allocate(CIMConstant.DATA_HEADER_LENGTH);

    private final ExecutorService workerExecutor = Executors.newFixedThreadPool(1, r -> new Thread(r, "worker-"));

    private final ExecutorService bossExecutor = Executors.newFixedThreadPool(1, r -> new Thread(r, "boss-"));

    private final ClientMessageEncoder messageEncoder = new ClientMessageEncoder();
    private final ClientMessageDecoder messageDecoder = new ClientMessageDecoder();

    private final Random random = new Random();

    private final AtomicBoolean connecting = new AtomicBoolean(false);

    public CIMConnectManager(Context context) {
        this.context = context;
    }

    public void connect(final String host, final int port) {

        if (!CIMPushManager.isNetworkConnected(context)) {

            Intent intent = new Intent();
            intent.setPackage(context.getPackageName());
            intent.setAction(IntentAction.ACTION_CONNECT_FAILED);
            context.sendBroadcast(intent);

            return;
        }

        if (isConnected() || connecting.get()) {
            return;
        }

        bossExecutor.execute(() -> {

            if (isConnected()) {
                return;
            }

            LOGGER.startConnect(host, port);

            CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_CIM_CONNECTION_STATE, false);

            try {

                connecting.set(true);

                socketChannel = SocketChannel.open();
                socketChannel.configureBlocking(true);
                socketChannel.socket().setTcpNoDelay(true);
                socketChannel.socket().setKeepAlive(true);
                socketChannel.socket().setReceiveBufferSize(READ_BUFFER_SIZE);
                socketChannel.socket().setSendBufferSize(WRITE_BUFFER_SIZE);

                socketChannel.socket().connect(new InetSocketAddress(host, port), CONNECT_TIME_OUT);

                handleConnectedEvent();

                connecting.set(false);

                /*
                 *开始读取来自服务端的消息，先读取3个字节的消息头
                 */
                while (socketChannel.read(headerBuffer) > 0) {
                    handleSocketReadEvent();
                }

                /*
                 *read 返回 <= 0的情况，发生了意外需要断开重链
                 */
                close();

            } catch (ConnectException | SocketTimeoutException | UnknownHostException exception) {
                handleConnectFailedEvent(exception);
            } catch (IOException exception) {
                handleDisconnectedEvent();
            }finally {
                connecting.set(false);
            }
        });
    }

    public void close() {

        if (!isConnected()) {
            return;
        }

        this.closeForce();
    }

    public boolean isConnected() {
        return socketChannel != null && socketChannel.isConnected();
    }

    public void pong() {
        send(Pong.getInstance());
    }

    public void send(final BinaryBody body) {

        if (!isConnected()) {
            return;
        }

        workerExecutor.execute(() -> {
            int result = 0;
            try {

                ByteBuffer buffer = messageEncoder.encode(body);
                while (buffer.hasRemaining()) {
                    result += socketChannel.write(buffer);
                }

            } catch (Exception e) {
                result = -1;
            } finally {

                if (result <= 0) {
                    close();
                } else {
                    onMessageSent(body);
                }
            }
        });

    }

    private void closeForce(){
        try {
            socketChannel.close();
        } catch (IOException ignore) {
        } finally {
            this.onSessionClosed();
        }
    }


    private void onSessionCreated() {
        LOGGER.sessionCreated(socketChannel);

        Intent intent = new Intent();
        intent.setPackage(context.getPackageName());
        intent.setAction(IntentAction.ACTION_CONNECT_FINISHED);
        context.sendBroadcast(intent);

    }

    private void onSessionClosed() {

        idleHandler.removeMessages(0);

        LOGGER.sessionClosed(socketChannel);

        Intent intent = new Intent();
        intent.setPackage(context.getPackageName());
        intent.setAction(IntentAction.ACTION_CONNECTION_CLOSED);
        context.sendBroadcast(intent);

    }

    private void onSessionIdle() {

        LOGGER.sessionIdle(socketChannel);

        close();
    }


    private void onMessageReceived(Object obj) {

        if (obj instanceof Message) {

            Intent intent = new Intent();
            intent.setPackage(context.getPackageName());
            intent.setAction(IntentAction.ACTION_MESSAGE_RECEIVED);
            intent.putExtra(Message.class.getName(), (Message) obj);
            context.sendBroadcast(intent);

        }
        if (obj instanceof ReplyBody) {

            Intent intent = new Intent();
            intent.setPackage(context.getPackageName());
            intent.setAction(IntentAction.ACTION_REPLY_RECEIVED);
            intent.putExtra(ReplyBody.class.getName(), (ReplyBody) obj);
            context.sendBroadcast(intent);
        }
    }


    private void onMessageSent(Object message) {

        LOGGER.messageSent(socketChannel, message);

        if (message instanceof SentBody) {
            Intent intent = new Intent();
            intent.setPackage(context.getPackageName());
            intent.setAction(IntentAction.ACTION_SEND_FINISHED);
            intent.putExtra(SentBody.class.getName(), (SentBody) message);
            context.sendBroadcast(intent);
        }
    }

    private final Handler idleHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message m) {
            workerExecutor.execute(CIMConnectManager.this::onSessionIdle);
        }
    };

    private void handleDisconnectedEvent() {
        closeForce();
    }

    private void handleConnectFailedEvent(Exception exception) {

        long retryAfter;

        if (exception instanceof UnknownHostException){
            /*
             * 通常是网络由WIFI切换为移动网出现这个异常
             */
            retryAfter = 3000L;
        }else {
            /*
             * 随机3-10秒后重连
             */
            retryAfter = 3000L + random.nextInt(7001) ;
        }

        LOGGER.connectFailure(retryAfter);

        Intent intent = new Intent();
        intent.setPackage(context.getPackageName());
        intent.setAction(IntentAction.ACTION_CONNECT_FAILED);
        intent.putExtra(BundleKey.KEY_RECONNECT_AFTER, retryAfter);
        context.sendBroadcast(intent);

    }

    private void handleConnectedEvent() {

        closeCountDown();

        onSessionCreated();

    }

    private void handleSocketReadEvent() throws IOException {

        closeCountDown();

        Object message = messageDecoder.doDecode(headerBuffer,socketChannel);

        LOGGER.messageReceived(socketChannel, message);

        if (message instanceof Ping) {
            send(Pong.getInstance());
            return;
        }

        this.onMessageReceived(message);

    }


    private void closeCountDown() {

        idleHandler.removeMessages(0);

        idleHandler.sendEmptyMessageDelayed(0, CONNECT_ALIVE_TIME_OUT);
    }

}
