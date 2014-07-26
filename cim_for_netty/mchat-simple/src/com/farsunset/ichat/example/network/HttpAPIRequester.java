 
package com.farsunset.ichat.example.network;
import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

 
public class HttpAPIRequester  {
    HttpAPIResponser responser;
    Type dataType;
    Type dataListType;
    private static BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
	private static ThreadPoolExecutor executor =  new ThreadPoolExecutor(3, 5, 20,	TimeUnit.SECONDS,queue);;
    
    public HttpAPIRequester(HttpAPIResponser responser)
	{
		this.responser = responser;
		
	}
	
    public HttpAPIRequester()
	{}
    
    
    //http api 调用
    public void  execute(final HashMap<String,Object> params, final String url)
	{
	    executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					httpPost(url,params);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		 });
	}
	
    
    /**
     * HTTP api 调用方法，回应将再responser里面处理
     *execute(这里用一句话描述这个方法的作用）
     *（这里描述这个方法是用条件 - 可选）
     * @param dtype 返回数据的 data节点数据泛型
     * @param dltype 返回数据的 dataList节点数据泛型
     * @param url
     *void
     * @exception
     * @since 1.0.0
     */
	public void  execute( Type dtype , Type dltype, final String url)
	{
		
		this.dataType = dtype;
		this.dataListType = dltype;
		responser.onRequest();
	    executor.execute(new Runnable() {
			@Override
			public void run() {
				
				Message message = handler.obtainMessage();
                HashMap<String,Object> data = new HashMap<String,Object>();
				try {
					String dataString = httpPost(url,responser.getRequestParams());
					JSONObject json = JSON.parseObject(dataString);
					data.put("code", json.getString("code"));
					data.put("url", url);
				    if(json.containsKey("data") && dataType!=null)
					{
				    	    dataString = json.getJSONObject("data").toJSONString();
			    		    data.put("data", JSON.parseObject(dataString, dataType));
					} 
					if(json.containsKey("dataList") &&dataListType!=null )
					{
					    	dataString = json.getJSONArray("dataList").toJSONString();
					    	data.put("list", JSON.parseObject(dataString, dataListType));
					} 
					    
					if(json.containsKey("page") &&json.getJSONObject("page")!=null)
				    {
					    	dataString = json.getJSONObject("page").toJSONString();
					    	data.put("page", JSON.parseObject(dataString, Page.class));
				    }
					
				    message.getData().putSerializable("data", data);
					message.what = 0;
				} catch (Exception e) {
					e.printStackTrace();
					message.getData().putSerializable("exception", e);
					message.what = 1;
				}
				handler.sendMessage(message);
			}
		 });

	}
	
	
	public static String httpPost(String url,Map<String,?> map) throws Exception 
	{
		 HttpPost httpPost = new HttpPost(url);  
		 MultipartEntity mpEntity = new MultipartEntity();  
	 
		 for(String key:map.keySet())
		 {
			 if(map.get(key)!=null)
			 {
				 if(map.get(key) instanceof File)
				 {
					 FileBody fileBody = new FileBody((File)map.get(key));
					 mpEntity.addPart(key,fileBody);
				 }else
				 {
					 StringBody stringBody = new StringBody(map.get(key).toString(), Charset.forName("UTF-8"));
				     mpEntity.addPart(key,stringBody);
				 }
			 }
		 }
		 
		 System.out.println(url);
		 System.out.println(JSON.toJSONString(map));
         httpPost.setEntity(mpEntity);  
         HttpClient httpClient = new DefaultHttpClient();  
         httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);  
         httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);  
         HttpResponse httpResp = httpClient.execute(httpPost);  
         String json = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
         httpClient.getConnectionManager().closeIdleConnections(1, TimeUnit.MILLISECONDS);
         System.out.println(json);
         return json;
	}
	
	 Handler handler = new Handler(){
		public void handleMessage(Message message)
		{
		switch(message.what)
			{
				case   0:
					HashMap<String,Object> data =(HashMap<String, Object>) message.getData().getSerializable("data");
					Page page = (Page) data.get("page");
					List<Object> list = (List<Object>) data.get("list");
					String code = String.valueOf(data.get("code"));
				    responser.onSuccess(data.get("data"),list,page,String.valueOf(data.get("code")),data.get("url").toString());

					break;
				case   1:
					responser.onFailed((Exception) message.getData().get("exception"));
					break;
			}
				
		}
	 };
}
