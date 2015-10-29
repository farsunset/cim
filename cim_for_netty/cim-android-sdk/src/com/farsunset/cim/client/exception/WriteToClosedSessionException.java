/**
 * probject:cim-android-sdk
 * @version 2.0.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.client.exception;

import java.io.Serializable;


public class WriteToClosedSessionException extends Exception implements Serializable {
	 
	private static final long serialVersionUID = 1L;

	public WriteToClosedSessionException() {
		super();
	}
 
	public WriteToClosedSessionException(String s) {
		super(s);
	}
}
