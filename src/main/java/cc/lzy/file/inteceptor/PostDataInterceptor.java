/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 */
package cc.lzy.file.inteceptor;

import cc.lzy.file.controller.FileUploadController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 拦截器
 *
 * @author taigai
 * @version : PostDataInterceptor.java, v 0.1 2021年05月23日 12:22 taigai Exp $
 */
public class PostDataInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 可以获取Controller
        FileUploadController fileUploadController = (FileUploadController) handlerMethod.getBean();
        // 可以获取Method
        Method method = handlerMethod.getMethod();

        System.out.println("PostDataInterceptor->preHandle");

        // 业务逻辑，比如鉴权..

        // 返回true，表示继续执行下一个拦截器逻辑
        return true;
    }
}