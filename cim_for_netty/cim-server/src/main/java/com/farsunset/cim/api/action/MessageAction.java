/**
 * probject:cim
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.api.action;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.bind.ServletRequestBindingException;

import com.farsunset.cim.push.DefaultMessagePusher;
import com.farsunset.cim.push.SystemMessagePusher;
import com.farsunset.cim.sdk.server.model.Message;
import com.farsunset.cim.util.Constants;
import com.farsunset.cim.util.ContextHolder;
import com.farsunset.cim.util.StringUtil;
import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

/** 
 *
 * @author farsunset (3979434@qq.com)
 */
public class MessageAction  extends  ActionSupport  implements ModelDriven<Message>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Message message = new Message();
 
    /**
     * 关于http参数获取，  struts2  的模型驱动  比如 http 参数 sender=xiaomao&receiver=xiaogou
     * struts自动会将参数的值 存入getModel()返回的对象的对应属性中，即xiaomao会存入message.sender属性,xiaogou会存入message.receiver属性
     * 省去了request.getParameter("sender")方式获取参数，，如果参数名在getModel()返回的对象中不存在，则需要用request.getParameter()获取
     * 其他相关*Action.java中 同理，这里做统一说明!
     */
    public String send() throws Exception {
        
    	HashMap<String,Object> datamap = new HashMap<String,Object>();
    	HashMap<String,String> data = new HashMap<String,String>();
		ServletActionContext.getResponse().setContentType("text/json;charset=UTF-8");
		datamap.put("code", 200);
		
		try{
	        
			checkParams();
			message.setMid(StringUtil.getUUID());
			if(Constants.MessageType.TYPE_2.equals(message.getType()))
			{
				  //向客户端 发送消息
		        ContextHolder.getBean(SystemMessagePusher.class).push(message);
			}else
			{
				  //向客户端 发送消息
		        ((DefaultMessagePusher)ContextHolder.getBean("messagePusher")).push(message);
			}
	              
	        data.put("id", message.getMid());
	        data.put("createTime", String.valueOf(message.getTimestamp()));
	        datamap.put("data", data);
		}catch(Exception e){
			
			datamap.put("code", 500);
			e.printStackTrace();
		}
       
		ServletActionContext.getResponse().getWriter().print(new Gson().toJson(datamap));
        return null;
    }
    
  

    
    /**
     * 文件由客户端发往阿里云 OSS 存储
     * @param messageServiceImpl
     */
   /* private void fileHandler(Message mo, HttpServletRequest request) throws IOException 
    {
    	if(request instanceof MultiPartRequestWrapper){
			MultiPartRequestWrapper pr = (MultiPartRequestWrapper) request;
			if(pr.getFiles("file")!=null)
			{
				File file = pr.getFiles("file")[0];
		         
		        String fileType = request.getParameter("fileType");
		        String dir = dirMap.get(fileType);
		        if(StringUtils.isEmpty(dir))
		        {
		          	  throw new IllegalArgumentException("fileType:" +fileType+" 未定义" );
		          	  
		        }
		        	String path = request.getSession().getServletContext().getRealPath(dir);
		        	String uuid=UUID.randomUUID().toString().replaceAll("-", "");
		 		    File des = new File(path+"/"+uuid);
		 		    FileUtil.copyFile(file, des);
		 		    mo.setFile(dir+"/"+uuid);
		 		    mo.setFileType(fileType);
			}
        }
          
    }*/
    
    
    private void checkParams() throws ServletRequestBindingException  
    {
    	   
          if(StringUtils.isEmpty(message.getReceiver()))
          {
        	  throw new IllegalArgumentException("receiver 不能为空!");
        	  
          }
    }
 
	@Override
	public Message getModel() {
		// TODO Auto-generated method stub
		return message;
	}

 
 
}