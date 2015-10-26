/**
 * probject:cim-android-sdk
 * @version 2.0.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.client.filter;

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

import android.util.Log;

import com.farsunset.cim.client.constant.CIMConstant;
import com.farsunset.cim.client.model.Message;
import com.farsunset.cim.client.model.ReplyBody;
/**
 *  客户端消息解码
 */
public class ClientMessageDecoder extends CumulativeProtocolDecoder {

	final static String TAG = ClientMessageDecoder.class.getSimpleName();
	
	private IoBuffer buff = IoBuffer.allocate(320).setAutoExpand(true);

	@Override
	public boolean doDecode(IoSession iosession, IoBuffer iobuffer,
			ProtocolDecoderOutput out) throws Exception {
		boolean complete = false;

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
			
            //打印出收到的消息
			Log.i(TAG,message);
			
			try
			{
				Object msg = mappingMessageObject(message);
				out.write(msg);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}

		return complete;
	}

      private Object mappingMessageObject(String  message) throws Exception {
		
		if(CIMConstant.CMD_HEARTBEAT_REQUEST.equals(message))
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
