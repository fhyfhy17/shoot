
# Shoot ,[distributeed_game_server](https://github.com/fhyfhy17/distributed_game_server)的进一步封装逻辑的代码


-----

## 分布式Java游戏服务器

### 所用框架
- `Zookeeper`    分布式发现与注册
- `Springboot`       采用非web方式启动，注解编程，方便嫁接Spring系列和其它第三方框架
- `protobuf3`       协议
- `netty`       tcp连接，毕竟Vert.x不熟，暂时先使用netty做为tcp服务器包，后期会继续变更
- `MongoDB`      数据库。
- `ZeroMq`       进程间通信
- `Ehcache`     缓存

####  
