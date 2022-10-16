 industry4.0-mes 基于 QCADOO的 
## **开源MES，生产制造管理系统,Manufacturing Execution System**

## **机加工MES、食品包装MES、制鞋MES、服装MES、离散MES**

## 最新的低代码MES,欢迎咨询


## **升级的最新版本演示**：
## DEMO 版本是完整的商业版本 
http://demo.cloudmes.io/login.html \
test001/123456 \
test002/123456 \
test003/123456 \
test004/123456 \
test005/123456 \
test006/123456

## **联系我们：** 
QQ群：622319616 \
微信：17898898894 \
contact@cloudmes.io

截图：



## **编译说明：**
**1、安装postgresql 12.2及以上的版本** \
执行以下脚本导入db-init.sql "C:\Program Files\PostgreSQL\12\bin\psql.exe" -U 数据库用户名 -d 数据库名称 < db-init.sql

**2、解压tomcat-9.0.20.zip** \
解压到一个文件夹，配置文件在mesconf文件夹里面，修改db.properties中的数据库配置

**3、编译源码** \
1）导入maven project,调整JDK、tomcat安装文件夹 \
2) 将lib文件夹里面的内容复制到maven repository文件夹里面，比如/users/aa/.m2/repository \
3）maven install顺序：\
   qcadoo-super-pom-open \
   qcadoo-maven-plugin \
   qcadoo \
   mes

打包后的war包在mes/mes-application/target/，可以直接copy war包到tomcat/webapps下面

## **答疑：**
Q：是个人还是公司？ A：公司

Q：商业部分开源吗？ A：商业部分代码付费开源，可以联系 17898898894.

Q：技术架构是怎么样的？ A：JAVA Spring MVC、Jquery、Bootstrap、数据库是 postgresql（可以替代 oracle），每个插件可以使用独立的前端，比如 vue

Q：是否提供现场实施？ A：只提供技术支持、技术培训，不提供现场实施。

Q：是否可以二开？ A：如果需要二开，按照需求付费。

Q：是否可以免费部署到本地？ A：可以，需要申请许可，我们提供部署包，安装服务费用 2000。

Q：如果使用我们的私有云，是否收费？ A：免费。

Q：是否可以对接 ERRP、SCADA 等第三方系统？ A：可以，rest API.

Q：是否有数据大屏？ A：包含数据大屏，也可以自己开发或者使用第三方的数据大屏，或者直接使用我们的数据大屏，数据大屏每个账户 500 元/月。

Q：是否有工资绩效统计？ A：有

Q：是否支持 PDA 或者手机端？ A：报工时候支持 PDA 和手机端，也有 API，可以自己开发手机端。

Q：有使用手册或者流程介绍吗？ A：https://www.ricefish.io/shouce661/index.html
