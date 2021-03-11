# 快速搭建Springboot脚手架
## 主要功能
+ 引入 `MybatisPlus `并配置

  + 乐观锁插件
  + 防止全表更新与删除插件
  + 自动填充创建时间(createTime)和修改时间(createTime)

+ 引入Redis(可手动关闭 `manaphy.redis=false`)

  + 配置 redisTemplate 使用 fastjson
  +  配置基于注解的方式
  + 配置二级缓存
  + 配置Redis延迟队列

+ 配置基于Redis的防止并发请求注解 `@NoConcurrentSubmit`

+ 配置防二次提交注解 `@IdempotentSubmit` (幂等提交,只适用于单机应用)

+ 配置接口防刷注解 `@Limiter`

+ 配置统一日志处理(可手动关闭 `manaphy.weblog=false`)

+ 配置全局异常捕获(可手动关闭 `manaphy.exception=false`)

+ 配置RestTemplate(可手动关闭 `manaphy.rest=false`)

+ 统一的接口返回 `ApiResult`

+ 配置pagehelper分页插件 快速进行分页

+ 各种工具类

  + ArithmeticUtil 用于高精确处理常用的数学运算
  + DateTimeUtils
  + HttpClientUtil 
  + JsonUtil
  + SecureUtil 加密工具
  + StringUtils
  + WebUtil (获取request  response session ip地址等)
  + XmlUtil
  
  
