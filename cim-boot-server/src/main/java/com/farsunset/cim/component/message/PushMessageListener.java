package com.farsunset.cim.component.message;

import com.farsunset.cim.group.SessionGroup;
import com.farsunset.cim.model.Message;
import com.farsunset.cim.util.JSONUtils;
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

        String uid = message.getReceiver();

        sessionGroup.write(uid,message);

    }
}
