/*
 * Copyright 2013-2022 Xia Jun(3979434@qq.com).
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
package com.farsunset.cim.component.message;

import com.farsunset.cim.component.event.MessageEvent;
import com.farsunset.cim.group.SessionGroup;
import com.farsunset.cim.model.Message;
import com.farsunset.cim.util.JSONUtils;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * 集群环境下，监听redis队列，广播消息到每个实例进行推送
 * 如果使用MQ的情况也，最好替换为MQ消息队列
 */
@Component
public class PushMessageListener implements MessageListener {

    @Resource
    private SessionGroup sessionGroup;

    @Override
    public void onMessage(org.springframework.data.redis.connection.Message redisMessage, byte[] bytes) {

        Message message = JSONUtils.fromJson(redisMessage.getBody(), Message.class);

        this.onMessage(message);

    }

    @EventListener
    public void onMessage(MessageEvent event) {
        this.onMessage(event.getSource());
    }

    public void onMessage(Message message) {

        String uid = message.getReceiver();

        if (uid == null){
            return;
        }

        sessionGroup.write(uid,message);
    }
}
