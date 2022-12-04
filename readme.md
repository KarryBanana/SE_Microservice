## OJ第五版启动和部署教程
### 运行OJ第五版后端需要安装的软件
* Java 8
* Mysql 5.7
* Nacos 1.3.2
* Elasticsearch 7.6.2, Logstash 7.6.2
* Maven，Git，lombok插件

### 运行OJ第五版步骤
1. clone项目到本地：https://e.coding.net/songbuaalab/oj5th_back_microservice/oj5th_back.git
2. 在Mysql中新建数据库oj5th和oj5th_test,运行sql目录下的脚本文件
3. 启动nacos并导入配置文件，配置文件在nacos-config目录下
4. 修改配置文件中DataSource，改为自己的数据库配置
5. 启动elasticsearch，如果是初次部署并且需要测试搜索相关的接口时，需要启动logstash将题目数据从mysql导入到elasticsearch，详细步骤见下面
6. 启动服务

### logstash导入数据步骤
logstash是一个数据采集，转换，发送的工具，可以将数据从一个源迁移到另一个源。本项目使用logstash将题目等需要搜索的数据从mysql数据库迁移到elasticsearch数据库。
利用logstash导入数据步骤如下：
* 从官网下载并解压Logstash 7.6.2
* 在config目录下新建配置文件，文件内容如下：

```
input {
  stdin {}
  jdbc {
    jdbc_driver_library => "D:\Deve\ELK\logstash-7.6.2\config\mysql-connector-java-5.1.49.jar"
    jdbc_driver_class => "com.mysql.jdbc.Driver"
    jdbc_connection_string => "jdbc:mysql://localhost:3306/oj5th_test?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
    jdbc_user => "root"
    jdbc_password => "1234"
    statement => "select * from problem"
    type => "problem"
  }
}

filter {
    
}

output {

    if[type] == "problem" {
        elasticsearch {
            hosts => ["localhost:9200"]
            index => "oj5th_test_problem"
            document_id => "%{id}"
        }
    }

    stdout {
        codec => json_lines
    }
}
```
注意：配置文件中涉及路径，用户名，密码等地方需要根据自己的情况修改，用logstash导入数据时需要java连接mysql的jar包
* 切换到logstash根目录下，运行命令： ./bin/logstash -f ./config/jdbc.conf ，其中jdbc.conf是第二步编写的配置文件

