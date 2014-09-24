package com.farsunset.cim.nio.filter;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.farsunset.cim.nio.constant.CIMConstant;
import com.farsunset.cim.nio.mutual.SentBody;
/**
 *  服务端接收消息解码，可在此解密消息
 *  @author 3979434@qq.com
 *
 */
public class ServerMessageDecoder extends FrameDecoder  {

	@Override
	protected Object decode(ChannelHandlerContext arg0, Channel channel,ChannelBuffer  buffer) throws Exception {
		
		 
		int length = buffer.readableBytes();
		
		/**
		 * CIMConstant.MESSAGE_SEPARATE 为消息界限
		 * 当一次收到多个消息时，以此分隔解析多个消息
		 */
		if (buffer.readable()&&length > 0 &&  CIMConstant.MESSAGE_SEPARATE == buffer.getByte(length-1)) {
			
			byte[] data = new byte[length-1];
			buffer.readBytes(data);
			String message = new String(new String(data,CIMConstant.ENCODE_UTF8));
			System.out.println("[ServerMessageDecoder]:"+message);
			buffer.readByte();
			
			SentBody body = new SentBody();
	    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
	        DocumentBuilder builder = factory.newDocumentBuilder();  
	        Document doc = builder.parse(new ByteArrayInputStream(message.toString().getBytes()));
	        body.setKey(doc.getElementsByTagName("key").item(0).getTextContent());
	        NodeList dataNodeList = doc.getElementsByTagName("data");
	        if(dataNodeList!=null && dataNodeList.getLength()>0)
	        {
		        NodeList items = dataNodeList.item(0).getChildNodes();  
		        for (int i = 0; i < items.getLength(); i++) {  
		            Node node = items.item(i);  
		            body.getData().put(node.getNodeName(), node.getTextContent());
		        }
	        }
	        data = null;
	        message = null;
	        return body;
		}
		
		/**
		 * CIMConstant.FLEX_DATA_SEPARATE 为FLEX客户端socket验证消息界限
		 * 
		 */
		if (buffer.readable()&& length > 0 &&  CIMConstant.FLEX_DATA_SEPARATE == buffer.getByte(length-1)) {
			
			byte[] data = new byte[length-1];
			buffer.readBytes(data);
			String message = new String(new String(data,CIMConstant.ENCODE_UTF8));
			
			System.out.println("[ServerMessageDecoder]:"+message);
			
			//将末尾的消息分隔符读取掉
			buffer.readByte();
			data = null;
			
			return message;
		}
        
		return null;
	}

}
