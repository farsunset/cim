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
package com.farsunset.cim.service.impl;

import com.farsunset.cim.component.redis.KeyValueRedisTemplate;
import com.farsunset.cim.entity.Session;
import com.farsunset.cim.repository.SessionRepository;
import com.farsunset.cim.service.SessionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Service
public class SessionServiceImpl implements SessionService {

    @Resource
    private SessionRepository sessionRepository;

    @Resource
    private KeyValueRedisTemplate keyValueRedisTemplate;


    private final String host;

    public SessionServiceImpl() throws UnknownHostException {
        host = InetAddress.getLocalHost().getHostAddress();
    }

    @Override
    public void add(Session session) {
        session.setBindTime(System.currentTimeMillis());
        session.setHost(host);
        sessionRepository.save(session);
    }

    @Override
    public void delete(String uid, String nid) {
        sessionRepository.delete(uid,nid);
    }

    @Override
    public void deleteLocalhost() {
        sessionRepository.deleteAll(host);
    }

    @Override
    public void updateState(String uid, String nid, int state) {
        sessionRepository.updateState(uid,nid,state);
    }

    @Override
    public void openApns(String uid,String deviceToken) {
        keyValueRedisTemplate.openApns(uid,deviceToken);
        sessionRepository.openApns(uid,Session.CHANNEL_IOS);
    }

    @Override
    public void closeApns(String uid) {
        keyValueRedisTemplate.closeApns(uid);
        sessionRepository.closeApns(uid,Session.CHANNEL_IOS);
    }

    @Override
    public List<Session> findAll() {
        return sessionRepository.findAll();
    }
}
