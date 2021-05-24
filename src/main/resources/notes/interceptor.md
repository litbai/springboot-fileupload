## Interceptor

Interceptor是Spring框架提供的拦截器机制，开发者可以使用拦截器对指定的Handler进行统一的业务处理，比如鉴权、日志、时间统计等。

### 配置方式

开发者可以配置任意数量的拦截器，这些拦截器最终组成一条拦截器链，SpringBoot配置步骤如下：

* 编写拦截器，实现 HandlerInterceptor
* 注册拦截器，通过实现WebMvcConfigurer自定义mvc配置

#### 编写拦截器

```java
public class PostDataInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 可以获取Controller
        FileUploadController fileUploadController = (FileUploadController) handlerMethod.getBean();
        // 可以获取Method
        Method method = handlerMethod.getMethod();
        System.out.println(fileUploadController);
        System.out.println(method);
        
        // 业务逻辑，比如鉴权..
       
        // 返回true，表示继续执行下一个拦截器逻辑
        return true;
    }
}
```



#### 注册拦截器

```java
# 必须为 @Configuration
@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer{
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PostDataInterceptor()).addPathPatterns("/upload/*");
    }
}
```



### 源代码

#### 注册拦截器

org.springframework.web.servlet.config.annotation.WebMvcConfigurerComposite#addInterceptors

```java
// delegates 包含了所有的自定义拦截器
@Override
public void addInterceptors(InterceptorRegistry registry) {
	for (WebMvcConfigurer delegate : this.delegates) {
		delegate.addInterceptors(registry);
	}
}
```



#### 调用拦截器

org.springframework.web.servlet.DispatcherServlet#doService

​	-- DispatcherServlet#doDispatch

​		-- org.springframework.web.servlet.HandlerExecutionChain#applyPreHandle

```java
boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
   for (int i = 0; i < this.interceptorList.size(); i++) {
      HandlerInterceptor interceptor = this.interceptorList.get(i);
      if (!interceptor.preHandle(request, response, this.handler)) {
         triggerAfterCompletion(request, response, null);
         return false;
      }
      this.interceptorIndex = i;
   }
   return true;
}
```



org.springframework.web.servlet.DispatcherServlet#doService

​	-- DispatcherServlet#doDispatch

​		-- org.springframework.web.servlet.HandlerExecutionChain#applyPostHandle

```java
// 从后向前执行拦截器链的postHandle
void applyPostHandle(HttpServletRequest request, HttpServletResponse response, @Nullable ModelAndView mv)
			throws Exception {
		for (int i = this.interceptorList.size() - 1; i >= 0; i--) {
			HandlerInterceptor interceptor = this.interceptorList.get(i);
			interceptor.postHandle(request, response, this.handler, mv);
		}
	}
```



org.springframework.web.servlet.DispatcherServlet#doService

​	-- DispatcherServlet#doDispatch

​		-- DispatcherServlet#processDispatchResult

​			-- org.springframework.web.servlet.HandlerExecutionChain#triggerAfterCompletion

```java
// 根据执行preHandle记录的interceptorIndex，从后向前执行afterCompletion
void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, @Nullable Exception ex) {
		for (int i = this.interceptorIndex; i >= 0; i--) {
			HandlerInterceptor interceptor = this.interceptorList.get(i);
			try {
				interceptor.afterCompletion(request, response, this.handler, ex);
			}
			catch (Throwable ex2) {
				logger.error("HandlerInterceptor.afterCompletion threw exception", ex2);
			}
		}
	}
```



