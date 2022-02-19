import 'dart:io';
import 'dart:typed_data';

import '../protobuf/Message.pb.dart' as Message;
import '../protobuf/ReplyBody.pb.dart' as ReplyBody;
import '../protobuf/SentBody.pb.dart' as SentBody;
import 'package:fixnum/fixnum.dart';

late Socket socket;

const APP_VERSION = "1.0.0";
const APP_CHANNEL = "web";
const APP_PACKAGE = "com.farsunset.cim";
const SERVER_URL = "192.168.10.133";
const SERVER_PORT = 23456;

//PONG
const PONG_TYPE = 0;
//发送消息类型
const Message_TYPE = 2;
//强制下线类型
const ACTION_999 = 999;
//响应消息类型
const REPLY_BODY = 4;
//消息发送
const SEND_BODY = 3;
//PING
const PING_TYPE = 1;

const DATA_HEADER_LENGTH = 1;

void connect() async {
  print('-------------开始连接----------------');
  Socket.connect(SERVER_URL, SERVER_PORT).then((Socket sock) {
    // socket = sock;
    sock.listen(dataHandler,
        onError: errorHandler, onDone: doneHandler, cancelOnError: false);
    socket = sock;
    sendLoginMsg();
  }).catchError((e) {
    print("Unable to connect: $e");
  });
}

void dataHandler(Uint8List data) {
  print(data[0]);
  int l = (data[1] & 0xff);
  int h = (data[2] & 0xff);
  int length = (l | h << 8);
  if (data[0] == PING_TYPE) {
    sendPong();
  } else if (data[0] == REPLY_BODY) {
    var message = data.sublist(3, length + 3);
    ReplyBody.Model info = new ReplyBody.Model();
    info.mergeFromBuffer(message);
    print(info.key);
    print(info);
  } else if (data[0] == Message_TYPE) {
    var message = data.sublist(3, length + 3);
    Message.Model model = new Message.Model();
    model.mergeFromBuffer(message);
    print(model.action);
    print(model);
  }
}

void errorHandler(error, StackTrace trace) {
  print(error);
}

void doneHandler() {
  print("-------------连接失败----------------");
}

//发送登录消息
void sendLoginMsg() async {
  Map<String, String> map = {
    "uid": "11111111112346121631126311",
    "channel": APP_CHANNEL,
    "appVersion": APP_VERSION,
    "osVersion": "Android 10",
    "packageName": APP_PACKAGE,
    "deviceId": "121155155a61d6a1s6d1as6d1a6s1da",
    "deviceName": "andoirdPhone",
    "language": "Zh_cn",
  };
  int time = new DateTime.now().millisecondsSinceEpoch;
  Int64 timeStamp = Int64.parseInt(time.toString());
  var body = SentBody.Model(data: map);
  body.key = "client_bind";
  body.timestamp = timeStamp;
  var data = body.writeToBuffer();
  var protobuf = new Uint8List(data.length + 3);
  protobuf[0] = 3;
  protobuf[1] = (data.length & 0xff);
  protobuf[2] = ((data.length >> 8) & 0xff);
  protobuf.setRange(3, data.length + 3, data);
  socket.add(protobuf);
  await socket.flush();
}

//发送PONG响应
void sendPong() async {
  var PONG = new Uint8List(7);
  var PONG_BODY = new Uint8List(4);
  PONG_BODY[0] = 80;
  PONG_BODY[1] = 79;
  PONG_BODY[2] = 78;
  PONG_BODY[3] = 71;
  PONG[0] = PONG_TYPE;
  PONG[1] = (PONG_BODY.length & 0xff);
  PONG[2] = ((PONG_BODY.length >> 8) & 0xff);
  PONG.setRange(3, 6, PONG_BODY);
  socket.add(PONG);
  await socket.flush();
  print("发送Pong");
}
