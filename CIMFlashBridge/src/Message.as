
package  
{
	import com.adobe.serialization.json.JSON;
	
	public class Message
	{
	 
		
		/**
		 * 消息类型，用户自定义消息类别
		 */
		public var mid:String;
		
		
		/**
		 * 消息类型，用户自定义消息类别
		 */
		public var type:String;
		/**
		 * 消息标题
		 */
		public var title:String;
		/**
		 * 消息类容，于type 组合为任何类型消息，content 根据 format 可表示为 text,json ,xml数据格式
		 */
		public var content:String = "";
		
		
		/**
		 * 消息发送者账号
		 */
		public var sender:String;
		/**
		 * 消息发送者接收者
		 */
		public var receiver:String;
		
		/**
		 * 文件 url
		 */
		public var file:String;
		/**
		 * 文件类型
		 */
		public var fileType:String;
		
		/**
		 * content 内容格式
		 */
		public var format:String = "txt";
		
		
		public var timestamp:Number;
		
		public static function mappingToJSON(xml:XML):Object
		{
			var  message:Message = new Message();
			message.mid = xml["mid"];
			message.type = xml["type"];
			message.title = xml["title"];
			message.sender = xml["sender"];
			message.receiver = xml["receiver"];
			message.file = xml["file"];
			message.fileType = xml["fileType"];
			message.timestamp = xml["timestamp"];
			message.content =xml["content"];
			return com.adobe.serialization.json.JSON.encode(message).toString();
		}

		
		 
	}
	
	
	
}