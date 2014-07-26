package com.farsunset.ichat.example.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.farsunset.cim.nio.mutual.Message;
import com.farsunset.ichat.example.app.Constant;
public class API {
	
	public final static String API_URL=Constant.SERVER_URL+"/cgi/";
 
	
/*	public static LinkedHashMap<String,String> sendMessage(Message msg,File file)  throws Exception{
    	Map<String,String> map = new HashMap<String,String>();
    	map.put("content", msg.getContent());
    	map.put("title", msg.getTitle());
    	map.put("sender", msg.getSender());
    	map.put("receiver", msg.getReceiver());
    	map.put("type", msg.getType());
    	map.put("fileType", msg.getFileType());
    	String json = httpPost(API_URL+"message_send.api",map,file);
    	
    	return new Gson().fromJson(json, new TypeToken<LinkedHashMap<String,String>>(){}.getType());
	}*/
	
	public static String httpPost(String url,Map<String,String> map,File file) throws ClientProtocolException, IOException
	{
		 HttpPost httpPost = new HttpPost(url);  
		 
		 MultipartEntity mpEntity = new MultipartEntity(); //文件传输
		 if(file!=null){
			 ContentBody cbFile = new FileBody(file);
			 mpEntity.addPart("file", cbFile);
		 }
		 for(String key:map.keySet())
		 {
			 if(map.get(key)!=null)
			 {
			   StringBody stringBody = new StringBody(map.get(key), Charset.forName("UTF-8"));
			   mpEntity.addPart(key,stringBody);
			 }
		 }
		 
         // 设置参数实体  
         httpPost.setEntity(mpEntity);  
         System.out.println("request params:--->>" + map.toString());
         // 获取HttpClient对象  
         HttpClient httpClient = new DefaultHttpClient();  
         //连接超时  
         httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);  
         //请求超时  
         httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);  
 
         HttpResponse httpResp = httpClient.execute(httpPost);  
         String json = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
         System.out.println(json);
         return json;
	}
	
	 
	
	public static String httpPost(String url,Map<String,String> map) throws ClientProtocolException, IOException
	{
		 return httpPost(url,map,null);
	}

	
}
