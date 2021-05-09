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
package com.farsunset.cim.repository;

import com.farsunset.cim.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional(rollbackFor = Exception.class)
public interface SessionRepository extends JpaRepository<Session, Long> {

	@Modifying
	@Query("delete from Session where uid = ?1 and nid = ?2")
	void delete(String uid,String nid);

	@Modifying
	@Query("delete from Session where host = ?1 ")
	void deleteAll(String host);

	@Modifying
	@Query("update Session set state = ?3 where uid = ?1 and nid = ?2")
	void updateState(String uid,String nid,int state);

	@Modifying
	@Query("update Session set state = " + Session.STATE_APNS + " where uid = ?1 and channel = ?2")
	void openApns(String uid,String channel);

	@Modifying
	@Query("update Session set state = " + Session.STATE_ACTIVE + " where uid = ?1 and channel = ?2")
	void closeApns(String uid,String channel);
}
