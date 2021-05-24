## Filter

Filter依赖于Servlet容器，是Servlet的规范，可以拦截请求，设置一些通用的配置，比如设置字符编码。



## 配置方式

SpringBoot注册Filter，有两种方式，步骤如下：

* 编写filter：实现javax.servlet.Filter接口

* 注册Filter，有两种方式
  * 使用FilterRegisterBean
  * @ServletComponentScan(basePackages="xxx")  结合 @WebFilter(urlPatterns = "/upload/*")

### 编写Filter

```java
@WebFilter(urlPatterns = "/upload/*")
public class PostDataFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        /**
         * ------WebKitFormBoundaryYf2K0aXOm2hzvI44
         * Content-Disposition: form-data; name="username"
         *
         * 1234
         * ------WebKitFormBoundaryYf2K0aXOm2hzvI44
         * Content-Disposition: form-data; name="age"
         *
         * 124
         * ------WebKitFormBoundaryYf2K0aXOm2hzvI44
         * Content-Disposition: form-data; name="passwd"
         *
         * 234
         * ------WebKitFormBoundaryYf2K0aXOm2hzvI44
         * Content-Disposition: form-data; name="logo"; filename="name.txt"
         * Content-Type: text/plain
         *
         * this is lzy.
         *
         *  int cent = (int) 123456789014L;
         *         System.out.println(cent);
         *
         *         int cent2 = (int) (123456789014L % 100);
         *         System.out.println(cent2);
         *
         * ------WebKitFormBoundaryYf2K0aXOm2hzvI44
         * Content-Disposition: form-data; name="certification"; filename=""
         * Content-Type: application/octet-stream
         *
         *
         * ------WebKitFormBoundaryYf2K0aXOm2hzvI44--
         */
        //String multipartPostData = IOUtils.toString(request.getInputStream());
        //System.out.println(multipartPostData);

        System.out.println("PostDataFilter, request uri: " + ((HttpServletRequest)request).getRequestURI());
        System.out.println("PostDataFilter, request url: " + ((HttpServletRequest)request).getRequestURL());
        chain.doFilter(request, response);
    }
}
```



### 注册Filter

```java
// 编写Filter，标记 @WebFilter
@WebFilter(urlPatterns = "/upload/*")
public class PostDataFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //String s = IOUtils.toString(request.getInputStream());
        System.out.println("PostDataFilter, request uri: " + ((HttpServletRequest)request).getRequestURI());
        System.out.println("PostDataFilter, request url: " + ((HttpServletRequest)request).getRequestURL());
        chain.doFilter(request, response);
    }
}

// 在Configuration上标注 @ServletComponentScan
@SpringBootApplication(scanBasePackages = "cc.lzy.file")
@ServletComponentScan("cc.lzy.file")
public class BootStrap {
    public static void main(String[] args) {
        SpringApplication.run(BootStrap.class, args);
    }
}
```



```java
// 注入 FilterRegistrationBean
@Configuration
public class FilterConfiguration {

    @Bean
    public FilterRegistrationBean authorityFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new AuthorityFilter());
        bean.setUrlPatterns(Lists.newArrayList("/upload/register.json"));
        bean.setOrder(1);
        return bean;
    }
}
```



### 生命周期

* 容器启动时，调用其init方法，相当于饿汉式。注意跟servlet不同，servlet再其初次被访问时，才调用init方法。
* 每次访问servlet时，调用doFilter方法。
* 容器关闭时，调用destroy方法。