 industry4.0-mes 基于 QCADOO的 
## **开源MES，生产制造管理系统,Manufacturing Execution System**

## **机加工MES、食品包装MES、制鞋MES、服装MES、离散MES**

## 新版代码仓库请到：https://github.com/metaxk-company/free-mes

## 开源版不再提供演示、不提供技术支持和问题解答
<img width="1399" alt="image" src="https://github.com/ricefishtech/industry4.0-mes/assets/26316047/03e11976-aa8b-4c0f-863f-30b9bb4f7efd">

## **商业版联系我们：** 
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
