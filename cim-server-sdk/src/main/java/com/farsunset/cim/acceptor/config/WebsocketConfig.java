package com.farsunset.cim.acceptor.config;

import com.farsunset.cim.constant.WebsocketProtocol;
import com.farsunset.cim.handler.CIMRequestHandler;
import com.farsunset.cim.handshake.HandshakeEvent;

import java.util.function.Predicate;

public class WebsocketConfig {

    private static final int DEFAULT_PORT = 34567;

    private static final String DEFAULT_PATH = "/";

    private static final WebsocketProtocol DEFAULT_PROTOCOL = WebsocketProtocol.PROTOBUF;

    private Integer port;
    private String path;
    private WebsocketProtocol protocol;
    private CIMRequestHandler outerRequestHandler;
    private Predicate<HandshakeEvent> handshakePredicate;
    private boolean enable;

    public Integer getPort() {
        return port == null ? DEFAULT_PORT : port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPath() {
        return path == null ? DEFAULT_PATH : path;
    }

    public WebsocketProtocol getProtocol() {
        return protocol == null ? DEFAULT_PROTOCOL : protocol;
    }


    public CIMRequestHandler getOuterRequestHandler() {
        return outerRequestHandler;
    }

    public Predicate<HandshakeEvent> getHandshakePredicate() {
        return handshakePredicate;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setProtocol(WebsocketProtocol protocol) {
        this.protocol = protocol;
    }

    public void setOuterRequestHandler(CIMRequestHandler outerRequestHandler) {
        this.outerRequestHandler = outerRequestHandler;
    }

    public void setHandshakePredicate(Predicate<HandshakeEvent> handshakePredicate) {
        this.handshakePredicate = handshakePredicate;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
