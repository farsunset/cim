/**
 * probject:cim-server-sdk
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */  
package com.farsunset.cim.sdk.server.filter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import java.io.ByteArrayInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.model.SentBody;
/**
 *  服务端接收消息解码，可在此解密消息
 */
public class ServerMessageDecoder extends ObjectDecoder  {
	protected final Logger logger = Logger.getLogger(ServerMessageDecoder.class.getSimpleName());
	public ServerMessageDecoder(ClassResolver classResolver) {
		super(classResolver);
	}

	@Override
	public Object decode(ChannelHandlerContext arg0, ByteBuf  buffer) throws Exception   {
		
		final ByteBuf  tBuffer = PooledByteBufAllocator.DEFAULT.buffer(320);
		
		buffer.markReaderIndex();
		boolean complete = false;  
		
		while(buffer.isReadable()){
			byte b = buffer.readByte();
			if (b == CIMConstant.MESSAGE_SEPARATE || b == CIMConstant.FLEX_DATA_SEPARATE) {
				complete = true;
				break;
			} else {
				tBuffer.writeByte(b);
			}
		}
		
		if(complete){
			
			String message = new String(new String(ByteBufUtil.getBytes(tBuffer),CIMConstant.UTF8));
			
			logger.info(message);
			
			
			Object body = parserMessageToSentBody(message);
			return body;
			
		}else{
		 
			buffer.resetReaderIndex();
			return null;
		}
		 
	}
	
	private Object  parserMessageToSentBody(String message) throws Exception
	{
		
		SentBody body = new SentBody();
		/*
		 * 如果是心跳响应，则让HeartbeatHandler去处理
		 */
		if(message.equalsIgnoreCase(CIMConstant.CMD_HEARTBEAT_RESPONSE)){
		 
			body.setKey(CIMConstant.RequestKey.CLIENT_HEARTBEAT);
			return body;
		}
		
		/*
		 * flex 客户端安全策略请求，需要返回特定报文
		 */
		if(CIMConstant.FLEX_POLICY_REQUEST.equalsIgnoreCase(message)){
		
			body.setKey(CIMConstant.RequestKey.CLIENT_FLASH_POLICY);
			return body;
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
        DocumentBuilder builder = factory.newDocumentBuilder();  
        Document doc = builder.parse(new ByteArrayInputStream(message.toString().getBytes(CIMConstant.UTF8)));
        body.setKey(doc.getElementsByTagName("key").item(0).getTextContent());
        NodeList dataNodeList = doc.getElementsByTagName("data");
        if(dataNodeList!=null && dataNodeList.getLength()>0){
         
	        NodeList items = dataNodeList.item(0).getChildNodes();  
	        for (int i = 0; i < items.getLength(); i++) {  
	            Node node = items.item(i);  
	            body.getData().put(node.getNodeName(), node.getTextContent());
	        }
        }
        
        return body;
	}

}
