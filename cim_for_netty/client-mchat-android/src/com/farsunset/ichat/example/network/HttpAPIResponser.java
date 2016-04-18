/**
 * probject:cim
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */  
package com.farsunset.ichat.example.network;

import java.util.Map;

 
public interface HttpAPIResponser {
		public void onSuccess(String resulet,String url);
		public void onFailed(Exception e,String url);
		public Map<String,Object> getRequestParams();
		public void  onRequest();
}
