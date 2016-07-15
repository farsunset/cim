/**
 * probject:cim-server-sdk
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */  
package com.farsunset.cim.sdk.client.filter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.farsunset.cim.sdk.client.constant.CIMConstant;
import com.farsunset.cim.sdk.client.model.Message;
import com.farsunset.cim.sdk.client.model.ReplyBody;

/**
 *  客户端消息解码
 */
public class ClientMessageDecoder extends ObjectDecoder {


	public ClientMessageDecoder(ClassResolver classResolver) {
		super(classResolver);
	}

	@Override
	public Object decode(ChannelHandlerContext arg0, ByteBuf  buffer) throws Exception   {
		 
		final ByteBuf  tBuffer = PooledByteBufAllocator.DEFAULT.buffer(640);
	
		buffer.markReaderIndex();
		boolean complete = false;  
		
		while(buffer.isReadable()){
			byte b = buffer.readByte();
			if (b == CIMConstant.MESSAGE_SEPARATE) {
				complete = true;
				break;
			} else {
				tBuffer.writeByte(b);
			}
		}
		

		if(complete){
			
			String message = new String(new String(ByteBufUtil.getBytes(tBuffer),CIMConstant.UTF8));
			Object msg = mappingMessageObject(message);
			return msg;
			
		}else{
			
			buffer.resetReaderIndex();
			return null;
			
		}
		
		
	}

      private Object mappingMessageObject(String  message) throws Exception {
		
		if(message.equals(CIMConstant.CMD_HEARTBEAT_REQUEST)){//如果是心跳请求命令则直接返回
		
			return CIMConstant.CMD_HEARTBEAT_REQUEST;
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
				
				if(node.getNodeName().equals("mid")){
				
					body.setMid(node.getTextContent());
				}
				
				if(node.getNodeName().equals("type")){
				 
					body.setType(node.getTextContent());
				}
				
				if(node.getNodeName().equals("content")){
				 
					body.setContent(node.getTextContent());
				}
				
				if(node.getNodeName().equals("file")){
				 
					body.setFile(node.getTextContent());
				}
				
				if(node.getNodeName().equals("fileType")){
				 
					body.setFileType(node.getTextContent());
				}
				
				
				if(node.getNodeName().equals("sender")){
				 
					body.setSender(node.getTextContent());
				}
				
				if(node.getNodeName().equals("receiver")){
				 
					body.setReceiver(node.getTextContent());
				}
				
				if(node.getNodeName().equals("format")){
				 
					body.setFormat(node.getTextContent());
				}
				
				if(node.getNodeName().equals("timestamp")){
				 
					body.setTimestamp(Long.valueOf(node.getTextContent()));
				}
			}

			return body;
		}
		
        return null;
	}

 

 
}
