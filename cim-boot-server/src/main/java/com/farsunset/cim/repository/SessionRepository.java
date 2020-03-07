package com.farsunset.cim.repository;

import com.farsunset.cim.sdk.server.model.CIMSession;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/*
 * 正式场景下，使用redis或者数据库来存储session信息
 */
@Repository
public class SessionRepository {

    private ConcurrentHashMap<String, CIMSession> map = new ConcurrentHashMap<>();


    public void save(CIMSession session){
        map.put(session.getAccount(),session);
    }

    public CIMSession get(String account){
        return map.get(account);
    }

    public void remove(String account){
        map.remove(account);
    }

    public List<CIMSession> findAll(){
        return new LinkedList<>(map.values());
    }
}
