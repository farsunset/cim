package com.farsunset.cim.client.filter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

import com.farsunset.cim.client.constant.CIMConstant;
import com.farsunset.cim.client.model.Message;
import com.farsunset.cim.client.model.ReplyBody;
/**
 *  客户端消息解码
 *  @author 3979434@qq.com
 *
 */
public class ClientMessageDecoder extends ObjectDecoder {


	public ClientMessageDecoder(ClassResolver classResolver) {
		super(classResolver);
	}

	@Override
	public Object decode(ChannelHandlerContext arg0, ByteBuf  buffer) throws Exception   {
		 
		int length = buffer.readableBytes();
		/**
		 * CIMConstant.MESSAGE_SEPARATE 为消息界限
		 * 当一次收到多个消息时，以此分隔解析多个消息
		 */
		if (buffer.isReadable()&& length > 0 &&  CIMConstant.MESSAGE_SEPARATE == buffer.getByte(length-1)) {
			
			byte[] data = new byte[length-1];
			buffer.readBytes(data);
			String message = new String(new String(data,CIMConstant.UTF8));
			Log.i(ClientMessageDecoder.class.getSimpleName(), message.toString());
			//将末尾的消息分隔符读取掉
			buffer.readByte();
			
			Object msg = mappingMessageObject(message);
			
			data = null;
			message = null;
			return msg;
		}

		return null;
	}

      private Object mappingMessageObject(String  message) throws Exception {
		
		if(message.equals(CIMConstant.CMD_HEARTBEAT_REQUEST))//如果是心跳请求命令则直接返回
		{
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
			body.setType(doc.getElementsByTagName("type").item(0).getTextContent());
			body.setContent(doc.getElementsByTagName("content").item(0).getTextContent());
			body.setFile(doc.getElementsByTagName("file").item(0).getTextContent());
			body.setFileType(doc.getElementsByTagName("fileType").item(0).getTextContent());
			body.setTitle(doc.getElementsByTagName("title").item(0).getTextContent());
			body.setSender(doc.getElementsByTagName("sender").item(0).getTextContent());
			body.setReceiver(doc.getElementsByTagName("receiver").item(0).getTextContent());
			body.setFormat(doc.getElementsByTagName("format").item(0).getTextContent());
			body.setMid(doc.getElementsByTagName("mid").item(0).getTextContent());
			body.setTimestamp(Long.valueOf(doc.getElementsByTagName("timestamp").item(0).getTextContent()));

			return body;
		}
		
        return null;
	}

 

 
}
