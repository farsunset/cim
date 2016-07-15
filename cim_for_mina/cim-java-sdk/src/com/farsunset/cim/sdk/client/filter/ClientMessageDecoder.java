/**
 * probject:cim-java-sdk
 * @version 2.0.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.sdk.client.filter;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.farsunset.cim.sdk.client.constant.CIMConstant;
import com.farsunset.cim.sdk.client.model.Message;
import com.farsunset.cim.sdk.client.model.ReplyBody;
/**
 *  客户端消息解码
 */
public class ClientMessageDecoder extends CumulativeProtocolDecoder {


	@Override
	public boolean doDecode(IoSession iosession, IoBuffer iobuffer,
			ProtocolDecoderOutput out) throws Exception {
		
		IoBuffer buff = IoBuffer.allocate(320).setAutoExpand(true);

		
		boolean complete = false;
		
		
		iobuffer.mark();
		
		 
		while (iobuffer.hasRemaining()) {
			byte b = iobuffer.get();
			/**
			 * CIMConstant.MESSAGE_SEPARATE 为消息界限
			 * 当一次收到多个消息时，以此分隔解析多个消息
			 */
			if (b == CIMConstant.MESSAGE_SEPARATE) {

				complete = true;
				break;
			} else {
				buff.put(b);
			}
		}

		if (complete) {
			buff.flip();
			byte[] bytes = new byte[buff.limit()];
			buff.get(bytes);
			String message = new String(bytes, CIMConstant.UTF8);
			buff.clear();
			
			try
			{
				Object msg = mappingMessageObject(message);
				out.write(msg);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}else{
			//读取了一部分发现消息没有结束，则重置为没有读取
			iobuffer.reset();
		}

		return complete;
	}

      private Object mappingMessageObject(String  message) throws Exception {
		
    	  
    	
    	  
		if(CIMConstant.CMD_HEARTBEAT_REQUEST.equalsIgnoreCase(message))
		{
			return message;
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder = factory.newDocumentBuilder();  
        Document doc = (Document) builder.parse(new ByteArrayInputStream(message.getBytes(CIMConstant.UTF8)));
        
		String name = doc.getDocumentElement().getTagName();
		if (name.equals("reply")) {
			ReplyBody reply = new ReplyBody();
			reply.setKey(doc.getElementsByTagName("key").item(0).getTextContent());
			reply.setCode(doc.getElementsByTagName("code").item(0).getTextContent());
			NodeList items = doc.getElementsByTagName("data").item(0).getChildNodes();  
		     for (int i = 0; i < items.getLength(); i++) {  
		            Node node = items.item(i);  
		            reply.getData().put(node.getNodeName(), node.getTextContent());
		    }  
			return reply;
		}
		if (name.equals("message")) {

			Message body = new Message();
			NodeList nodeList = doc.getElementsByTagName("message").item(0).getChildNodes();
			int count = nodeList.getLength();
			for(int i = 0;i < count; i++){
				Node node = nodeList.item(i);
				
				if(node.getNodeName().equals("mid"))
				{
					body.setMid(node.getTextContent());
				}
				
				if(node.getNodeName().equals("type"))
				{
					body.setType(node.getTextContent());
				}
				
				if(node.getNodeName().equals("file"))
				{
					body.setFile(node.getTextContent());
				}
				
				if(node.getNodeName().equals("fileType"))
				{
					body.setFileType(node.getTextContent());
				}
				
				if(node.getNodeName().equals("content"))
				{
					body.setContent(node.getTextContent());
				}
				
				if(node.getNodeName().equals("sender"))
				{
					body.setSender(node.getTextContent());
				}
				
				if(node.getNodeName().equals("receiver"))
				{
					body.setReceiver(node.getTextContent());
				}
				
				if(node.getNodeName().equals("format"))
				{
					body.setFormat(node.getTextContent());
				}
				
				if(node.getNodeName().equals("timestamp"))
				{
					body.setTimestamp(Long.valueOf(node.getTextContent()));
				}
			}

			return body;
		}
		
        return null;
	}

 
}
