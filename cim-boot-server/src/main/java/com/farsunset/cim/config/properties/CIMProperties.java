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
package com.farsunset.cim.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "cim")
public class CIMProperties {

    private final App app = new App();

    private final Websocket websocket = new Websocket();

    public App getApp() {
        return app;
    }

    public Websocket getWebsocket() {
        return websocket;
    }

    public static class App {

        private Integer port;

        public void setPort(Integer port) {
            this.port = port;
        }

        public Integer getPort() {
            return port;
        }
    }

    public static class Websocket {
        private Integer port;
        private String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }
    }

    public Integer getAppPort() {
        return  app.port;
    }

    public Integer getWebsocketPort() {
        return  websocket.port;
    }

    public String getWebsocketPath() {
        return  websocket.path;
    }

}
