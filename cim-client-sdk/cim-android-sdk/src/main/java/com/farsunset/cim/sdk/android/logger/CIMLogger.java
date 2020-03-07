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
package com.farsunset.cim.sdk.android.logger;

import android.util.Log;

import java.nio.channels.SocketChannel;


/**
 * 日志打印，添加session 的id和ip address
 */
public class CIMLogger {
    private final static String TAG = "CIM";
    private boolean debug = true;

    public static CIMLogger getLogger() {
        return LoggerHolder.logger;
    }

    private CIMLogger() {

    }

    private static class LoggerHolder {
        private static final CIMLogger logger = new CIMLogger();
    }

    public void debugMode(boolean mode) {
        debug = mode;
    }

    public void messageReceived(SocketChannel session, Object message) {
        if (debug) {
            Log.i(TAG, String.format("[RECEIVED]" + getSessionInfo(session) + "\n%s", message));
        }
    }

    public void messageSent(SocketChannel session, Object message) {
        if (debug) {
            Log.i(TAG, String.format("[  SENT  ]" + getSessionInfo(session) + "\n%s", message));
        }
    }

    public void sessionCreated(SocketChannel session) {
        if (debug) {
            Log.i(TAG, "[ OPENED ]" + getSessionInfo(session));
        }
    }

    public void sessionIdle(SocketChannel session) {
        if (debug) {
            Log.d(TAG, "[  IDLE  ]" + getSessionInfo(session));
        }
    }

    public void sessionClosed(SocketChannel session) {
        if (debug) {
            Log.w(TAG, "[ CLOSED ] ID = " + session.hashCode());
        }
    }

    public void connectFailure(long interval) {
        if (debug) {
            Log.d(TAG, "CONNECT FAILURE, TRY RECONNECT AFTER " + interval + "ms");
        }
    }

    public void startConnect(String host, int port) {
        if (debug) {
            Log.i(TAG, "START CONNECT REMOTE HOST:" + host + " PORT:" + port);
        }
    }

    public void invalidHostPort(String host, int port) {
        if (debug) {
            Log.d(TAG, "INVALID SOCKET ADDRESS -> HOST:" + host + " PORT:" + port);
        }
    }

    public void connectState(boolean isConnected, boolean isManualStop, boolean isDestroyed) {
        if (debug) {
            Log.d(TAG, "CONNECTED:" + isConnected + " STOPPED:" + isManualStop + " DESTROYED:" + isDestroyed);
        }
    }

    private String getSessionInfo(SocketChannel session) {
        StringBuilder builder = new StringBuilder();
        if (session == null) {
            return "";
        }
        builder.append(" [");
        builder.append("id:").append(session.hashCode());

        try {
            if (session.socket().getLocalAddress() != null) {
                builder.append(" L:").append(session.socket().getLocalAddress()).append(":").append(session.socket().getLocalPort());
            }
        } catch (Exception ignore) {
        }


        try {
            if (session.socket().getRemoteSocketAddress() != null) {
                builder.append(" R:").append(session.socket().getRemoteSocketAddress().toString());
            }
        } catch (Exception ignore) {
        }
        builder.append("]");
        return builder.toString();
    }


}
