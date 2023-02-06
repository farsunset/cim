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
     * 验证身份信息，本方法切勿进行耗时操作！！！
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
