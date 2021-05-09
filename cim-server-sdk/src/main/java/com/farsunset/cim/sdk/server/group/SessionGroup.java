package com.farsunset.cim.sdk.server.group;


import com.farsunset.cim.sdk.server.constant.ChannelAttr;
import com.farsunset.cim.sdk.server.model.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class SessionGroup extends ConcurrentHashMap<String, Collection<Channel>> {

    private static final Collection<Channel> EMPTY_LIST = new LinkedList<>();

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

    public void remove(Channel channel){

        String uid = getKey(channel);

        Collection<Channel> collections = getOrDefault(uid,EMPTY_LIST);

        collections.remove(channel);

        if (collections.isEmpty()){
            remove(uid);
        }
    }


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


    public void write(String key,Message message){
        find(key).forEach(channel -> channel.writeAndFlush(message));
    }

    public Collection<Channel> find(String key){
        return this.getOrDefault(key,EMPTY_LIST);
    }

    public Collection<Channel> find(String key,String... channel){
        List<String> channels = Arrays.asList(channel);
        return find(key).stream().filter(item -> channels.contains(item.attr(ChannelAttr.CHANNEL).get())).collect(Collectors.toList());
    }
}