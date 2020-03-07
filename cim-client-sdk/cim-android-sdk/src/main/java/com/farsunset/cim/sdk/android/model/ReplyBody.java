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
package com.farsunset.cim.sdk.android.model;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * 请求应答对象
 */
public class ReplyBody implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 请求key
     */
    private String key;

    /**
     * 返回码
     */
    private String code;

    /**
     * 返回说明
     */
    private String message;

    private long timestamp;

    /**
     * 返回数据集合
     */
    private final Hashtable<String, String> data = new Hashtable<String, String>();

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void put(String k, String v) {
        data.put(k, v);
    }

    public String get(String k) {
        return data.get(k);
    }

    public void remove(String k) {
        data.remove(k);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void putAll(Map<String, String> map) {
        data.putAll(map);
    }

    public Set<String> getKeySet() {
        return data.keySet();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("#ReplyBody#").append("\n");
        buffer.append("key:").append(this.getKey()).append("\n");
        buffer.append("timestamp:").append(timestamp).append("\n");
        buffer.append("code:").append(code).append("\n");

        buffer.append("data{").append("\n");
        for (String key : getKeySet()) {
            buffer.append(key).append(":").append(this.get(key)).append("\n");
        }
        buffer.append("}");

        return buffer.toString();
    }

}
