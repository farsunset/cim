
package  
{
	import com.adobe.serialization.json.JSON;
	
	
	public class ReplyBody
	{
		public  var key:String;
		public  var code:String;
		public  var message:String;
		public  var data:Object = new Object();
		public  var timestamp:Number;
		
		public static function mappingToJSON(xml:XML):String
		{
			var  body:ReplyBody = new ReplyBody();
			body.key = xml["key"];
			body.code = xml["code"];
			body.timestamp = xml["timestamp"];
			var list:XMLList  = xml.elements("data").children();
			for each(var item:XML in list) {
				body.data[item.name().toString()] =item.toString();
			}
			
			return com.adobe.serialization.json.JSON.encode(body);
		}
		
	}
	
}