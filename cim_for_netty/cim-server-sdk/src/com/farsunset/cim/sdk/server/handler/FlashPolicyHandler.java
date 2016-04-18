 /**
 * probject:cim-server-sdk
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */  
package com.farsunset.cim.sdk.server.handler;

import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.model.ReplyBody;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.session.CIMSession;


/**
 *返回flash安全验证
 * 
 * @author
 */
public class FlashPolicyHandler implements CIMRequestHandler {


	public ReplyBody process(CIMSession session, SentBody message) {

		session.write(CIMConstant.FLEX_POLICY_RESPONSE);
		return null;
	}
	
 
	
}