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
package com.farsunset.cim.mvc.response;

import org.springframework.http.HttpStatus;

public class ResponseEntity<T> {
	private int code = HttpStatus.OK.value();
	private String message;
	private T data;
	private String token;
	private Long timestamp;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getToken() {
		return token;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public static ResponseEntity<Void> make(){
		return new ResponseEntity<>();
	}

	public static ResponseEntity<Void> make(int code){
		return make(code,null);
	}

	public static <T> ResponseEntity<T> make(int code,String message){
		ResponseEntity<T> result = new ResponseEntity<>();
		result.setCode(code);
		result.setMessage(message);
		return result;
	}

	public static ResponseEntity<Void> make(HttpStatus status){
		ResponseEntity<Void> result = new ResponseEntity<>();
		result.setCode(status.value());
		result.setMessage(status.getReasonPhrase());
		return result;
	}

	public static <Q> ResponseEntity<Q> make(HttpStatus status,String message){
		ResponseEntity<Q> result = new ResponseEntity<>();
		result.setCode(status.value());
		result.setMessage(message);
		return result;
	}


	public static <Q> ResponseEntity<Q> ok(Q data){
		ResponseEntity<Q> result = new ResponseEntity<>();
		result.setData(data);
		return result;
	}

}
