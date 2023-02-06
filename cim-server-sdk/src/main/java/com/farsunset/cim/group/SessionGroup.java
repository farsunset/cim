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
package com.farsunset.cim.group;


import com.farsunset.cim.constant.ChannelAttr;
import com.farsunset.cim.model.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 基于内存管理的channel组
 * 根据UID管理
 */
public class SessionGroup extends ConcurrentHashMap<String, Collection<Channel>> {

    private final transient ChannelFutureListener remover = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future){
            future.removeListener(this);
            remove(future.channel());
        }
    };

    protected String getKey(Channel channel){
        return channel.attr(ChannelAttr.UID).get();
    }

    /**
     * 将channel从内存容器中删除
     * @param channel
     */
    public void remove(Channel channel){

        String uid = getKey(channel);

        if(uid == null){
            return;
        }

        Collection<Channel> collections = getOrDefault(uid,Collections.emptyList());

        collections.remove(channel);

        if (collections.isEmpty()){
            remove(uid);
        }
    }



    /**
     * 将channel加入内存容器中删除
     * @param channel
     */
    public void add(Channel channel){

        String uid = getKey(channel);

        if (uid == null || !channel.isActive()){
            return;
        }

        channel.closeFuture().addListener(remover);

        Collection<Channel> collections = this.putIfAbsent(uid,new ConcurrentLinkedQueue<>(Collections.singleton(channel)));
        if (collections != null){
            collections.add(channel);
        }

        if (!channel.isActive()){
            remove(channel);
        }
    }

    /**
     * 查找到key关联的channel并写入消息体
     * @param key
     * @param message
     */
    public void write(String key, Message message){
        this.write(key, message, channel -> true);
    }

    /**
     * 查找到key关联的channel并写入消息体
     * @param key
     * @param message
     * @param matcher channel筛选条件
     */
    public void write(String key, Message message, Predicate<Channel> matcher){
        find(key).stream().filter(matcher).forEach(channel -> channel.writeAndFlush(message));
    }

    /**
     * 查找到key关联的channel并写入消息体
     * @param key
     * @param message
     * @param excludedSet 排除的UID集合
     */
    public void write(String key, Message message, Collection<String> excludedSet){
        Predicate<Channel> predicate = new ExcludedUidPredicate(excludedSet);
        this.write(key,message,predicate);
    }

    /**
     * 查找到消息接收者关联的channel并写入消息体
     * @param message
     */
    public void write(Message message){
        this.write(message.getReceiver(),message);
    }

    /**
     * 通过key查找channel集合
     * @param key
     * @return
     */
    public Collection<Channel> find(String key){
        return this.getOrDefault(key,Collections.emptyList());
    }

    /**
     * 通过key查找channel集合
     * @param key
     * @param matcher 过滤条件
     * @return
     */
    public Collection<Channel> find(String key,Predicate<Channel> matcher){
        return this.find(key)
                .stream()
                .filter(matcher)
                .collect(Collectors.toList());
    }

    /**
     * 通过key查找channel集合
     * @param key
     * @param channel 连接终端类型过滤条件
     * @return
     */
    public Collection<Channel> find(String key,String... channel){
        List<String> channels = Arrays.asList(channel);
        return this.find(key,channels);
    }

    /**
     * 通过key查找channel集合
     * @param key
     * @param channelSet 连接终端类型过滤条件
     * @return
     */
    public Collection<Channel> find(String key,Collection<String> channelSet){
        Predicate<Channel> predicate = new ChannelPredicate(channelSet);
        return find(key,predicate);
    }

    /**
     * 检查该channel是否存在内存管理当中
     * @param channel
     * @return
     */
    public boolean isManaged(Channel channel){

        String uid = getKey(channel);

        if (uid == null || !channel.isActive()){
            return false;
        }

        return getOrDefault(uid,Collections.emptyList()).contains(channel);
    }

    private static class ExcludedUidPredicate implements Predicate<Channel>{

        private final Collection<String> excludedSet;

        private ExcludedUidPredicate(Collection<String> excludedSet) {
            this.excludedSet = excludedSet == null ? Collections.emptySet() : excludedSet;
        }

        @Override
        public boolean test(Channel channel) {
            return !excludedSet.contains(channel.attr(ChannelAttr.UID).get());
        }
    }

    private static class ChannelPredicate implements Predicate<Channel>{

        private final Collection<String> channelSet;

        private ChannelPredicate(Collection<String> channelSet) {
            this.channelSet = channelSet == null ? Collections.emptySet() : channelSet;
        }


        @Override
        public boolean test(Channel ioChannel) {
            return channelSet.contains(ioChannel.attr(ChannelAttr.CHANNEL).get());
        }
    }
}