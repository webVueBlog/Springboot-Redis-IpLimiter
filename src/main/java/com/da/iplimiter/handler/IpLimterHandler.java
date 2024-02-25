package com.da.iplimiter.handler;

import com.google.common.base.Preconditions;
import com.da.iplimiter.annotation.IpLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 限流处理器
 * 处理IpLimter注解的AOP
 * Aspect是一个切面，它包含横切关注点的具体实现，并可以在类型匹配的方法执行之前、之后或出现异常时进行调用。
 * 在这里，我们使用Aspect来处理IpLimter注解。
 */
@Aspect
@Component
public class IpLimterHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(IpLimterHandler.class);

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * getRedisScript 读取脚本工具类
     * 这里设置为Long,是因为ipLimiter.lua 脚本返回的是数字类型
     */
    private DefaultRedisScript<Long> getRedisScript;

    // PostConstruct
    // 初始化方法，在bean创建完成并且属性赋值完成之后执行
    // 这里可以进行一些初始化操作，比如读取配置文件、初始化缓存等
    @PostConstruct
    public void init() {
        getRedisScript = new DefaultRedisScript<>();
        getRedisScript.setResultType(Long.class);// 设置返回值类型
        getRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("ipLimiter.lua")));// 设置脚本路径
        LOGGER.info("IpLimterHandler[分布式限流处理器]脚本加载完成");
    }

    /**
     * 这个切点可以不要，因为下面的本身就是个注解
     */
//    @Pointcut("@annotation(IpLimiter)")
//    public void rateLimiter() {}

    /**
     * 如果保留上面这个切点，那么这里可以写成
     * @Around("rateLimiter()&&@annotation(ipLimiter)")
     * Around作用是
     * 环绕通知，在目标方法执行之前和之后执行自定义的逻辑。
     * 这里的目标方法是带有IpLimiter注解的方法。
     * 环绕通知可以用于在目标方法执行之前和之后进行一些自定义的操作，比如记录日志、修改请求参数等。
     * 它还可以用于修改目标方法的执行结果，或者甚至阻止目标方法的执行。
     * 需要注意的是，环绕通知需要返回目标方法的执行结果。
     * 此外，环绕通知的参数必须是ProceedingJoinPoint类型，表示目标方法的执行点。
     * 最后，环绕通知的返回值类型必须是Object类型，表示目标方法的执行结果。
     * @param proceedingJoinPoint 目标方法的执行点
     * @param ipLimiter 带有IpLimiter注解的方法
     * @return 目标方法的执行结果
     * @throws Throwable 目标方法可能抛出的异常
     *
     * annotation
     * 注解类型，用于定义注解。
     * 注解可以用于修饰类、方法、字段、参数等，可以包含一些属性来指定注解的详细信息。
     * 注解可以通过反射机制获取注解的属性值。
     * 注解可以用于修饰代码，提供额外的信息或功能。
     * 注解可以用于编译期、类加载期和运行期。
     * 注解可以用于处理注解的处理器。
     * 注解可以用于生成文档。
     * 注解可以用于实现元数据。
     */
    @Around("@annotation(ipLimiter)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, IpLimiter ipLimiter) throws Throwable {
        if (LOGGER.isDebugEnabled()) {// 判断是否启用debug模式
            LOGGER.debug("IpLimterHandler[分布式限流处理器]开始执行限流操作");// 输出debug日志
        }
        Signature signature = proceedingJoinPoint.getSignature();// 获取目标方法的签名
        if (!(signature instanceof MethodSignature)) {// 判断签名是否为方法签名
            throw new IllegalArgumentException("the Annotation @IpLimter must used on method!");// 抛出异常
        }
        /**
         * 获取注解参数
         */
        // 限流模块IP
        String limitIp = ipLimiter.ipAdress();// 获取注解中的ipAdress属性值
        // Preconditions 作用是检查参数是否为空，为空则抛出异常
        Preconditions.checkNotNull(limitIp);// 检查limitIp是否为空，为空则抛出异常
        // 限流阈值
        long limitTimes = ipLimiter.limit();
        // 限流超时时间
        long expireTime = ipLimiter.time();
        if (LOGGER.isDebugEnabled()) {// 判断是否启用debug模式
            LOGGER.debug("IpLimterHandler[分布式限流处理器]参数值为-limitTimes={},limitTimeout={}", limitTimes, expireTime);
        }
        // 限流提示语
        String message = ipLimiter.message();
        /**
         * 执行Lua脚本
         */
        List<String> ipList = new ArrayList();
        // 设置key值为注解中的值
        ipList.add(limitIp);
        /**
         * 调用脚本并执行
         */
        Long result = (Long) redisTemplate.execute(getRedisScript, ipList, expireTime, limitTimes);
        if (result == 0) {
            String msg = "由于超过单位时间=" + expireTime + "-允许的请求次数=" + limitTimes + "[触发限流]";
            LOGGER.debug(msg);
            // 达到限流返回给前端信息
            return message;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("IpLimterHandler[分布式限流处理器]限流执行结果-result={},请求[正常]响应", result);
        }
        // 正常执行
        // proceed 作用是执行被拦截的方法
        return proceedingJoinPoint.proceed();
    }
}
