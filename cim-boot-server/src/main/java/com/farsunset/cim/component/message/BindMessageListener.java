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

import com.farsunset.cim.component.event.SessionEvent;
import com.farsunset.cim.constant.ChannelAttr;
import com.farsunset.cim.entity.Session;
import com.farsunset.cim.group.SessionGroup;
import com.farsunset.cim.model.Message;
import com.farsunset.cim.util.JSONUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOutboundInvoker;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 集群环境下，监控多设备登录情况，控制是否其余终端下线的逻辑
 */
@Component
public class BindMessageListener implements MessageListener {

    private static final String FORCE_OFFLINE_ACTION = "999";

    private static final String SYSTEM_ID = "0";

    /*
     一个账号只能在同一个类型的终端登录
     如: 多个android或ios不能同时在线
         一个android或ios可以和web，桌面同时在线
     */
    private final Map<String,String[]> conflictMap = new HashMap<>();

    /*
     * web可能同一个终端 打开多 tab页面，可以同时保持连接
     */
    private final Set<String> keepLiveChannels = new HashSet<>();

    @Resource
    private SessionGroup sessionGroup;

    public BindMessageListener(){
        conflictMap.put(Session.CHANNEL_ANDROID,new String[]{Session.CHANNEL_ANDROID,Session.CHANNEL_IOS});
        conflictMap.put(Session.CHANNEL_IOS,new String[]{Session.CHANNEL_ANDROID,Session.CHANNEL_IOS});
        conflictMap.put(Session.CHANNEL_WINDOWS,new String[]{Session.CHANNEL_WINDOWS,Session.CHANNEL_WEB,Session.CHANNEL_MAC});
        conflictMap.put(Session.CHANNEL_WEB,new String[]{Session.CHANNEL_WINDOWS,Session.CHANNEL_WEB,Session.CHANNEL_MAC});
        conflictMap.put(Session.CHANNEL_MAC,new String[]{Session.CHANNEL_WINDOWS,Session.CHANNEL_WEB,Session.CHANNEL_MAC});

        keepLiveChannels.add(Session.CHANNEL_WEB);
    }

    @EventListener
    public void onMessage(SessionEvent event) {
        this.handle(event.getSource());
    }

    @Override
    public void onMessage(org.springframework.data.redis.connection.Message redisMessage, byte[] bytes) {

        Session session = JSONUtils.fromJson(redisMessage.getBody(), Session.class);

        this.handle(session);
    }

    private void handle(Session session){

        String uid = session.getUid();

        String[] conflictChannels = conflictMap.get(session.getChannel());

        if (ArrayUtils.isEmpty(conflictChannels)){
            return;
        }

        Collection<Channel> channelList = sessionGroup.find(uid,conflictChannels);

        channelList.removeIf(new KeepLivePredicate(session));

        /*
         * 同设备仅关闭连接，无需通知客户端
         */
        channelList.stream().filter(new SameDevicePredicate(session)).forEach(ChannelOutboundInvoker::close);

        /*
         * 不同设备关闭连接， 通知客户端账号在其他设备登录
         */
        channelList.stream().filter(new DifferentDevicePredicate(session)).forEach(new BreakOffMessageConsumer(uid,session.getDeviceName()));

    }


    private static class BreakOffMessageConsumer implements Consumer<Channel> {

        private final Message message;

        private BreakOffMessageConsumer(String uid,String deviceName) {
            message = new Message();
            message.setAction(FORCE_OFFLINE_ACTION);
            message.setReceiver(uid);
            message.setSender(SYSTEM_ID);
            message.setContent(deviceName);
        }

        @Override
        public void accept(Channel channel) {
            channel.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE);
        }
    }
    private static class SameDevicePredicate implements Predicate<Channel> {

        private final String deviceId;

        private SameDevicePredicate(Session session) {
            this.deviceId = session.getDeviceId();
        }

        @Override
        public boolean test(Channel channel) {
            return Objects.equals(this.deviceId,channel.attr(ChannelAttr.DEVICE_ID).get());
        }
    }

    private static class DifferentDevicePredicate implements Predicate<Channel>{

        private final SameDevicePredicate predicate;

        private DifferentDevicePredicate(Session session) {
            this.predicate = new SameDevicePredicate(session);
        }

        @Override
        public boolean test(Channel channel) {
            return !predicate.test(channel);
        }
    }


    private class KeepLivePredicate implements Predicate<Channel>{
        private final Session session;

        private KeepLivePredicate(Session session) {
            this.session = session;
        }

        @Override
        public boolean test(Channel ioChannel) {

            if (Objects.equals(session.getNid(),ioChannel.attr(ChannelAttr.ID).get())){
                return true;
            }

            String deviceId = ioChannel.attr(ChannelAttr.DEVICE_ID).toString();

            String channel = ioChannel.attr(ChannelAttr.CHANNEL).toString();

            return keepLiveChannels.contains(channel) && Objects.equals(session.getDeviceId(),deviceId);
        }
    }
}
