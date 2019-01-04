# elephant

elephant源自《蜡笔小新》，帮助下载网站的视频。

## 依赖

* Java 8
* MySQL 5.7

## 配置

在`elephant.jar`同目录中创建`application.properties`文件，按照如下示范配置：

```properties
# 数据库URL
spring.datasource.url=jdbc:mysql://localhost/elephant?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&autoReconnect=true
# 数据库用户名
spring.datasource.username=username
# 数据库密码
spring.datasource.password=password

# 站点基准URL
elephant.site.base-url=http://example.com/

# 代理类型 direct http socks
elephant.proxy.type=direct
# 代理主机名
elephant.proxy.hostname=localhost
# 代理端口
elephant.proxy.port=0

# 下载路径
elephant.download.path=./download/
```

## 使用

### 更新存档视频信息

从网站获取所有视频信息，保存&更新到数据库。

```shell
java -jar elephant.jar archive update
```

### 从存档下载视频

按照指定顺序`order`，在存档视频中查找未下载过的视频信息开始下载，到达网站限制后停止。

order类型：
* views 查看数量最多
* favorites 收藏数量最多
* comments 留言数量最多
* point 积分最多
* create_time 存档时间最新

```shell
java -jar elephant.jar archive download [order]
```

### 从网站下载视频

从网站获取最新视频信息，开始下载未下载过的视频，到达网站限制后停止。

```shell
java -jar elephant.jar online download
```
