package com.farsunset.cim.acceptor.config;

import com.farsunset.cim.handler.CIMRequestHandler;

public class AppSocketConfig {

    private static final int DEFAULT_PORT = 23456;

    private Integer port;

    private CIMRequestHandler outerRequestHandler;

    private boolean enable;

    public Integer getPort() {
        return port == null ? DEFAULT_PORT : port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public CIMRequestHandler getOuterRequestHandler() {
        return outerRequestHandler;
    }

    public void setOuterRequestHandler(CIMRequestHandler outerRequestHandler) {
        this.outerRequestHandler = outerRequestHandler;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
