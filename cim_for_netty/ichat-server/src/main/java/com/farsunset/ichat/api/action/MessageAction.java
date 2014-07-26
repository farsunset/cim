 
package com.farsunset.ichat.api.action;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.bind.ServletRequestBindingException;

import com.farsunset.cim.nio.mutual.Message;
import com.farsunset.ichat.cim.push.SystemMessagePusher;
import com.farsunset.ichat.common.util.ContextHolder;
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
     * ����http�����ȡ��  struts2  ��ģ����  ���� http ���� sender=xiaomao&receiver=xiaogou
     * struts�Զ��Ὣ�����ֵ ����getModel()���صĶ���Ķ�Ӧ�����У���xiaomao�����message.sender����,xiaogou�����message.receiver����
     * ʡȥ��request.getParameter("sender")��ʽ��ȡ��������������getModel()���صĶ����в����ڣ�����Ҫ��request.getParameter()��ȡ
     * �������*Action.java�� ͬ�?������ͳһ˵��!
     */
    public String send() throws Exception {
        
    	HashMap<String,Object> datamap = new HashMap<String,Object>();
    	HashMap<String,String> data = new HashMap<String,String>();
		ServletActionContext.getResponse().setContentType("text/json;charset=UTF-8");
		datamap.put("code", 200);
		
		try{
	        
			checkParams();
	        
	        //��ͻ��� ������Ϣ
	        ContextHolder.getBean(SystemMessagePusher.class).pushMessageToUser(message);
	        
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
     * �ļ��ɿͻ��˷������� OSS �洢
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
		          	  throw new IllegalArgumentException("fileType:" +fileType+" δ����" );
		          	  
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
        	  throw new IllegalArgumentException("receiver ����Ϊ��!");
        	  
          }
    }
 
	@Override
	public Message getModel() {
		// TODO Auto-generated method stub
		return message;
	}

 
 
}
