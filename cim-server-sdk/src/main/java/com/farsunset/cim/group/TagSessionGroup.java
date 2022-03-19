package com.farsunset.cim.group;


import com.farsunset.cim.constant.ChannelAttr;
import io.netty.channel.Channel;

public class TagSessionGroup extends SessionGroup {

    @Override
    protected String getKey(Channel channel){
        return channel.attr(ChannelAttr.TAG).get();
    }
}