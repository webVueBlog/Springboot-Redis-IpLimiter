# Springboot-Redis-IpLimiter

`场景`

为了防止我们的接口被人恶意访问，比如有人通过JMeter工具频繁访问我们的接口，导致接口响应变慢甚至崩溃

所以我们需要对一些特定的接口进行IP限流,即一定时间内同一IP访问的次数是有限的。

`实现原理`

用Redis作为限流组件的核心的原理,将用户的IP地址当Key,一段时间内访问次数为value,同时设置该Key过期时间。

比如某接口设置`相同IP10秒`内请求`5次`，超过5次不让访问该接口。

#### 技术架构

项目总体技术选型

```
SpringBoot2.1.3 + Maven3.5.4 + Redis + lombok(插件) 
```

自定义注解+AOP方式实现。

![image](https://github.com/webVueBlog/Springboot-Redis-IpLimiter/assets/59645426/216ec5b8-9eae-4d60-bf2d-f29e4d2a2b88)
