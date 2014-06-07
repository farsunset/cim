package com.farsunset.cim.nio.filter;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
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
public class ServerMessageDecoder extends CumulativeProtocolDecoder {

	private final Charset charset = Charset.forName("UTF-8");
    private IoBuffer buff = IoBuffer.allocate(320).setAutoExpand(true);
	@Override
	public boolean doDecode(IoSession iosession, IoBuffer iobuffer, ProtocolDecoderOutput out) throws Exception {
		boolean complete = false;
		while (iobuffer.hasRemaining()) {
            byte b = iobuffer.get();
            /**
			 * CIMConstant.MESSAGE_SEPARATE 为消息界限
			 * 当一次收到多个消息时，以此分隔解析多个消息
			 */
            if (b == CIMConstant.MESSAGE_SEPARATE ) {
            	
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
	        String message = new String(bytes, charset);
	        buff.clear();
			
			SentBody body = new SentBody();
			
	    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
	        DocumentBuilder builder = factory.newDocumentBuilder();  
	        Document doc = builder.parse(new ByteArrayInputStream(message.getBytes(charset)));
	        body.setKey(doc.getElementsByTagName("key").item(0).getTextContent());
	        NodeList items = doc.getElementsByTagName("data").item(0).getChildNodes();  
	        for (int i = 0; i < items.getLength(); i++) {  
	            Node node = items.item(i);  
	            body.getData().put(node.getNodeName(), node.getTextContent());
	        }
	        
	        out.write(body);
		}
	    return complete;
	}

}
