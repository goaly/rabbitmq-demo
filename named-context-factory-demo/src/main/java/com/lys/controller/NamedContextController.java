package com.lys.controller;

import com.lys.mybean.ITestContext;
import com.lys.mybean.MyShowContextBean;
import com.lys.mybean.context.MyContextBeanFactory;
import com.lys.namedcontext.NamedHttpClient;
import com.lys.namedcontext.NamedHttpContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 命名上下文控制器
 * @author lys
 */
@Api(tags = "命名上下文控制器")
@RestController
@RequestMapping("namedContext")
public class NamedContextController {

    @Resource
    private NamedHttpContext namedHttpContext;

    @Resource
    private MyContextBeanFactory myContextBeanFactory;

    @ApiOperation("根据名称获取Http客户端")
    @GetMapping("/getHttpClient/{name}")
    public String getHttpClient(@PathVariable("name") String name) {
        NamedHttpClient namedHttpClient = namedHttpContext.getInstance(name, NamedHttpClient.class);
        return "name:" + namedHttpClient.getServiceName()
                + ", socketTimeout:" + namedHttpClient.getRequestConfig().getSocketTimeout();
    }

    @ApiOperation("根据名称获取Bean服务")
    @GetMapping("/getMyBeanService/{name}")
    public String getMyBeanService(@PathVariable("name") String name) {

        ITestContext contextBean = myContextBeanFactory.getInstance(name, ITestContext.class);
        contextBean.show();
        return contextBean.getServiceName();
    }
}
