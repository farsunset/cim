package com.farsunset.cim.component.predicate;

import com.farsunset.cim.handshake.HandshakeEvent;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;


/**
 * WS链接握手鉴权验证
 */
@Component
public class HandshakePredicate implements Predicate<HandshakeEvent> {

    /**
     *
     * @param event
     * @return true验证通过 false验证失败
     */
    @Override
    public boolean test(HandshakeEvent event) {

        /*
            可通过header或者uri传递参数
            String token = event.getHeader("token");
            String token = event.getParameter("token");
            do auth....
         */

        return true;
    }
}
