package com.da.iplimiter.controller;

import com.da.iplimiter.annotation.IpLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 接口测试
 * Controller 是一个控制器，用于处理传入的请求，并返回响应。
 */
@Controller
public class IpController {
    // private static final Logger LOGGER 作用是
    // 创建一个名为LOGGER的Logger对象，并将其初始化为IpController类。
    // 这样，我们就可以在代码中使用LOGGER对象来记录日志了。
    // Logger是Spring框架中的一个日志记录工具类，它提供了一组方便的方法来记录不同级别的日志信息。
    // 例如，可以使用LOGGER.info()方法来记录一条信息级别的日志，使用LOGGER.error()方法来记录一条错误级别的日志，等等。
    // 在这个例子中，我们使用LoggerFactory.getLogger(IpController.class)来创建一个Logger对象，并将其命名为LOGGER。
    // 这样，我们就可以在IpController类中使用LOGGER来记录日志了。
    private static final Logger LOGGER = LoggerFactory.getLogger(IpController.class);
    private static final String MESSAGE = "请求失败,你的IP访问太频繁";

    // ResponseBody
    // 作用是：将返回的数据直接写入HTTP响应中，而不是将数据封装在某个模型或视图中。
    // 也就是说，@ResponseBody注解的作用是将控制器方法的返回值直接写入HTTP响应中，而不是将返回值转换为视图
    // RequestMapping
    // 作用是：将一个请求URL映射到控制器类或一个特定的控制器方法上。
    // 也就是说，@RequestMapping注解的作用是将一个URL映射到一个控制器类或一个特定的控制器方法上。
    // 它可以通过value、method、params等属性来指定URL映射的详细信息。
    // IpLimiter
    // 作用是：对IP进行限流。
    // 也就是说，@IpLimiter注解的作用是对IP进行限流，限制其访问频率。
    // 它可以通过ipAdress、limit、time等属性来指定IP限流的详细信息。
    @ResponseBody
    @RequestMapping("iplimiter")
    @IpLimiter(ipAdress = "196.168.0.22", limit = 5, time = 10, message = MESSAGE)
    public String sendPayment(HttpServletRequest request) throws Exception {
        return "请求成功";
    }


    @ResponseBody
    @RequestMapping("iplimiter1")
    @IpLimiter(ipAdress = "196.168.0.54", limit = 4, time = 10, message = MESSAGE)
    public String sendPayment1(HttpServletRequest request) throws Exception {
        return "请求成功";
    }
}
