/**
 * probject:cim-java-sdk
 * @version 2.0.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.sdk.client.model;

import java.io.Serializable;
import java.util.HashMap;
/**
 * java |android 客户端请求结构
 *
 */
public class Intent implements Serializable {

	private static final long serialVersionUID = 1L;

	private String action;

	private HashMap<String, Object> data = new HashMap<String, Object>();

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void putExtra(String key , Object value){
		data.put(key, value);
	}
	public Object getExtra(String key){
		return data.get(key);
	}
}
