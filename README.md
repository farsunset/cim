#### 项目介绍
CIM项目是基于mina或者netty框架下的推送系统，我们平常使用第三方的推送SDK，如极光推送，百度推送，小米推送，以及腾讯信鸽等来支撑自己的移动端的业务，或许有一些用户自己实现即时通讯系统的需求，那么CIM为您提供了一个解决方案或者思路，目前CIM支持 websocket，android，ios，桌面应用，系统应用等多端接入支持，目前CIM服务端使用springboot搭建仅仅拥有消息推送的功能，关于数据缓存与持久化都需要使用者自己开发，但是配备了比较完整的使用文档。最后希望CIM能为您带来一些价值。
   
---
## 以下是基于CIM开发的2款产品，并不开源!!!
## [http://farsunset.com](http://farsunset.com)
#### 和信
和信是基于CIM组件开发的一整套完整的产品,面向所有人开放注册的试用场景。具有丰富的功能，聊天、群组、好友列表、黑名单、公众号、朋友圈等功能。不依赖任何第三方服务，可以私有化部署。

#### 侣信
侣信是基于CIM组件开发的一整套完整的产品,面向中小企业和者各类团队组织内部交流使用工具。具有丰富的功能，聊天、群组、部门组织、公众号、内部朋友圈等功能。不依赖任何第三方服务，可以私有化部署。


<div align="center">
   <img src="http://staticres.oss-cn-hangzhou.aliyuncs.com/hoxin/call_video_incoming.jpg" width="24%"  />
   <img src="http://staticres.oss-cn-hangzhou.aliyuncs.com/hoxin/single_chatting_light.jpg" width="24%"  />
   <img src="http://staticres.oss-cn-hangzhou.aliyuncs.com/hoxin/single_chatting_dark.jpg" width="24%"  />
   <img src="http://staticres.oss-cn-hangzhou.aliyuncs.com/hoxin/moment_timeline_light.jpg" width="24%"  />
</div>

---  

#### 目录说明

1.cim-use-examples是各个客户端使用示例

2.cim-client-sdk 是各个客户端的SDK源码

3.cim-server-sdk 是服务端SDK源码,分为 mina和netty 两个版本，二者任选其一

4.cim-boot-server是springboot服务端工程源码,使用Idea工具开发

其中所有的sdk均为IntelliJ IDEA工程，Maven打包成jar导出引入到对应的客户端或服务端工程



#### 建议反馈

智者千虑必有一失，如果在使用或者学习过程中发现任何问题或者有优化建议，您可以通过QQ3979434或者邮箱3979434@qq.com向我反馈，当然目前还缺少IOS客户端sdk和.Net客户端SDK，如果您有兴趣可以参与开发，CIM将会越来越好。
 

#### 功能预览

1.控制台页面[http://127.0.0.1:8080](http://127.0.0.1:8080)
![image](https://images.gitee.com/uploads/images/2019/0315/165050_9e269c1c_58912.png)
2.Android客户端
![image](https://images.gitee.com/uploads/images/2019/0315/165050_6f20f69e_58912.png)
3.Web客户端
![image](https://images.gitee.com/uploads/images/2019/0315/165050_dfc33c18_58912.png)


#### 更新日志
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
 
