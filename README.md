
## 1、[在线文档](https://www.yuque.com/yuanfangxiyang/ma4ytb)

## 2、[产品官网](http://farsunset.com)

---

## 项目介绍

CIM是一套完善的消息推送框架，可应用于信令推送，即时聊天，移动设备指令推送等领域。开发者可沉浸于业务开发，不用关心消息通道长连接、消息编解码协议等繁杂处理。

CIM采用业内主流开源技术构建，易于扩展和使用，并完美支持集群部署支持海量链接，目前支持websocket，android，ios，桌面应用，系统应用等多端接入持,可应用于移动应用，物联网，智能家居，嵌入式开发，桌面应用，WEB应用即时消服务。

用时7年 基于CIM的项目已经运行在全国各个地方，包括上市公司，各地政务系统，警务系统等服务于上百家客户，希望CIM也能为您带来价值，如果您也希望加入项目成为贡献者，请联系我。如果觉得有用欢迎打赏。

<div align="center">
   <img src="http://staticres.oss-cn-hangzhou.aliyuncs.com/qcode/ali_pay.jpg" width="30%"  />
   <img src="http://staticres.oss-cn-hangzhou.aliyuncs.com/qcode/wechat_pay.jpg" width="30%"  />
</div>

   
---
## [收费产品介绍](http://farsunset.com)

#### 和信
和信是基于CIM组件开发的一整套完整的产品,面向所有人开放注册的试用场景。具有丰富的功能，聊天、群组、好友列表、黑名单、公众号、朋友圈等功能。不依赖任何第三方服务，可以私有化部署。

#### 侣信
侣信是基于CIM组件开发的一整套完整的产品,面向中小企业和者各类团队组织内部交流使用工具。具有丰富的功能，聊天、群组、部门组织、公众号、内部朋友圈等功能。不依赖任何第三方服务，可以私有化部署。


<div align="center">
   <img src="http://staticres.oss-cn-hangzhou.aliyuncs.com/hoxin/group_video_calling.jpg" width="24%"  />
   <img src="http://staticres.oss-cn-hangzhou.aliyuncs.com/hoxin/single_chatting_light.jpg" width="24%"  />
   <img src="http://staticres.oss-cn-hangzhou.aliyuncs.com/hoxin/single_chatting_dark.jpg" width="24%"  />
   <img src="http://staticres.oss-cn-hangzhou.aliyuncs.com/hoxin/moment_timeline_light.jpg" width="24%"  />
</div>

---  

---
## WEB聊天室

#### [https://gitee.com/farsunset/web-chat-room](https://gitee.com/farsunset/web-chat-room)
该项目是完全开源基于cim开发的一款web匿名聊天室，支持发送表情、图片、文字聊天，供学习使用

<div align="center">
   <img src="https://staticres.oss-cn-hangzhou.aliyuncs.com/chat-room/chat_window.png" width="45%"  />
   <img src="https://staticres.oss-cn-hangzhou.aliyuncs.com/chat-room/room_members.png" width="45%"  />
</div>

---  

## 功能预览

1.控制台页面[http://127.0.0.1:8080](http://127.0.0.1:8080)
![image](https://images.gitee.com/uploads/images/2019/0315/165050_9e269c1c_58912.png)

2.Android客户端
![image](https://images.gitee.com/uploads/images/2019/0315/165050_6f20f69e_58912.png)

3.Web客户端
![image](https://images.gitee.com/uploads/images/2019/0315/165050_dfc33c18_58912.png)


## 相关用户
-------------------------------------------------------------------------------------------
[JFlow](https://gitee.com/opencc/JFlow)

## 更新日志
-------------------------------------------------------------------------------------------
版本:3.5.0/时间:2018-08-22

1.服务端由原来的 spring+struts2修改为springboot工程

2.全面重写websocket的实现，全面拥抱protobuf，替换json序列化方式，更加高效

-------------------------------------------------------------------------------------------
版本:3.6.0/时间:2019-04-17

1.服务端springboot升级2.1.4,protobuf升级3.7.0

2.android sdk升级，适配android8.0+，修复一些之前的兼容性问题

3.消息的id字段名由mid修改为id，类型由String修改为long;


-------------------------------------------------------------------------------------------
版本:3.7.0/时间:2019-05-13

1.服务端cim-boot-server修改为idea maven工程

2.android sdk优化升级，去除mina或netty相关包的依赖

3.java sdk优化升级，去除mina或netty相关包的依赖

4.新增web sdk，可以由index.html快速启动demo

5.修正文档中一些疏漏
 

-------------------------------------------------------------------------------------------
版本:3.7.5/时间:2019-11-13

1.android sdk 优化，使用protobuf-lite版本替代较为臃肿的protobut-java版本



-------------------------------------------------------------------------------------------
版本:3.8.0/时间:2020-01-17

1.服务端sdk将websocket的服务端口和原生socket的端口分离，可以禁用其中一个或者同时启用

2.web端的sdk简化流程不再需要心跳响应,修改了连接成功回调方法名称和创建连接方法名

3.andoid sdk修改几个广播action的名称以及回调方法名称，详见cim-client-android工程

4.所有sdk均使用maven构建，idea工具开发，发现多处代码单词拼写错误，使用阿里语法检测组件优化了部分代码

5.同步修改了文档


-------------------------------------------------------------------------------------------
版本:4.0.0/时间:2021-04-30

1.websocket支持心跳机制

2.删除mina版本服务端sdk 、删除java版本客户端sdk

3.cim-boot-server重写、加入了推送集群实现。cim-android-client重写演示了更丰富的功能

4.客户端上行数据参数名修改
  account > uid
  device  > deviceName
  CR      > PONG

5.文档放到语雀在线文档
6.其他30多处多处代码优化

-------------------------------------------------------------------------------------------
版本:4.1.0/时间:2022-02-15

1.websocket支持在握手时鉴权验证

https://www.yuque.com/yuanfangxiyang/ma4ytb/vvy3iz#mmdUX

2.支持自定义配置websocketPath

 
