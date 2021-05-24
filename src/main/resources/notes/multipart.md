## 文件上传

使用springmvc进行文件上传非常简单，只需3步：

* 前端form表单，enctype设置为 multipart/form-data
* 引入apache commons-fileupload依赖
* 编写Controller，使用MultipartFile作为参数类型接收表单数据或使用MultiPartHttpServletRequest获取请求数据或使用@ModelAttribute接收多个参数（不能使用RequestBody接收multipart/form-data类型的Content-Type）

### enctype

标准的enctype有三种：

* application/x-www-form-urlencoded：不指定时，默认

  ```
  将数据编码为 k1=v1&k2=v2...  这种格式传递给服务端
  
  # 示例
  <form action="/upload/register.json" method="post">
      姓名：
      <input name="username" type="text"><br/>
      年龄:
      <input name="age" type="text"><br/>
      密码:
      <input name="passwd" type="password"><br/>
  
      <input type="submit" value="提交"/>
  </form>
  
  # 请求数据
  username=%E6%9D%8E%26%E6%B3%BD%E6%B4%8B&age=123&passwd=123
  ```

* multipart/form-data：上传文件专用

  ```
  # 请求头：
  content-type: multipart/form-data; boundary=----WebKitFormBoundaryiKBht0WiYH862RLX
  
  multipart/form-data表示表单传递的是文件，boundary是本次的分隔符。
  
  
  # 最终传递给服务端的数据格式如下，服务端需要自行解析
  # 其中 action、ctoken、escapeName是普通表单项，Filedata是文件表单项
  
  ------WebKitFormBoundaryiKBht0WiYH862RLX
  Content-Disposition: form-data; name="action"
  
  batchUp
  ------WebKitFormBoundaryiKBht0WiYH862RLX
  Content-Disposition: form-data; name="ctoken"
  
  gQpbHuXHVvAGZi0O
  ------WebKitFormBoundaryiKBht0WiYH862RLX
  Content-Disposition: form-data; name="escapeName"
  
  kebi.jpeg
  ------WebKitFormBoundaryiKBht0WiYH862RLX
  Content-Disposition: form-data; name="Filedata"; filename="kebi.jpeg"
  Content-Type: image/jpeg
  
  xxxx（数据字节流）
  ------WebKitFormBoundaryiKBht0WiYH862RLX--
  ```

* text/plain：不常用

### demo

```java
@RestController
@RequestMapping("/upload")
public class FileUploadController {

    /**
     * 普通表单项, 获取真实提交内容
     */
    @PostMapping("register.json")
    public String register(HttpServletRequest request) throws Exception{
        // 通过流获取
        // username=%E6%9D%8E%E6%B3%BD%E6%B4%8B&age=1024&passwd=3141
        InputStream is = request.getInputStream();
        String s1 = IOUtils.toString(is);

        // s2=s3="", 流不能重复读取
        String s2 = IOUtils.toString(is);
        String s3 = IOUtils.toString(is);

        return s1;
    }

    /**
     * 使用MultipartFile接收文件表单项，使用request.getParameter获取普通表单项
     */
    @PostMapping("upload1.json")
    public String handleByMultipartFile(HttpServletRequest request, @RequestParam("logo") MultipartFile file) throws Exception{
        // 获取普通参数
        String username = request.getParameter("username");
        String passwd = request.getParameter("passwd");

        // 获取文件信息

        // 文件名
        String filename = file.getOriginalFilename();
        // 如果文件没有上传，这里为0
        long size = file.getSize();
        // 如果文件没有上传，这里为空数组而非null
        byte[] bytes = file.getBytes();

        String content = IOUtils.toString(file.getInputStream(), "utf-8");
        return content;
    }

    /**
     * 使用MultipartHttpServletRequest接收表单数据，request.getFile获取文件表单项，request.getParameter获取普通表单项
     */
    @PostMapping("upload2.json")
    public String handleUploadByRequest(MultipartHttpServletRequest request) {

        String username = request.getParameter("username");
        String passwd = request.getParameter("passwd");

        // 根据参数名获取对应的file
        MultipartFile file = request.getFile("logo");

        return file.getOriginalFilename();
    }


    /**
     * 使用@RequestParam指定各个参数
     */
    @PostMapping("upload3.json")
    public String handleUploadByRequestParam(StandardMultipartHttpServletRequest request,
                                             @RequestParam("logo") MultipartFile file,
                                             @RequestParam("username") String username,
                                             @RequestParam("age") int age,
                                             @RequestParam("age") String passwd) throws Exception {

        String fetchPostData = fetchPostData(request.getRequest());
        return fetchPostData;
    }

    /**
     * 使用@ModelAttribute接收参数
     */
    @PostMapping("upload4.json")
    public String handleUploadByModel(@ModelAttribute RegisterParam registerParam) {
        StringJoiner joiner = new StringJoiner("#");
        joiner.add(registerParam.getUsername());
        joiner.add(registerParam.getAge() + "");
        joiner.add(registerParam.getPasswd());
        joiner.add(registerParam.getLogo().getContentType());
        joiner.add(registerParam.getLogo().getSize() + "");

        return joiner.toString();
    }


    /**
     * 使用@RequestBody接收参数，不支持解析multipart/form-data类型
     * Resolved [org.springframework.web.HttpMediaTypeNotSupportedException: Content type 'multipart/form-data;
     * boundary=----WebKitFormBoundary5QNSHUyNF4dTPrKA;charset=UTF-8' not supported]
     */
    @PostMapping("upload5.json")
    public String handleUploadByRequestBody(@RequestBody RegisterParam registerParam) {
        StringJoiner joiner = new StringJoiner("#");
        joiner.add(registerParam.getUsername());
        joiner.add(registerParam.getAge() + "");
        joiner.add(registerParam.getPasswd());
        joiner.add(registerParam.getLogo().getContentType());
        joiner.add(registerParam.getLogo().getSize() + "");

        return joiner.toString();
    }

    /**
     * 使用 MultipartFile[] 或 List<MultipartFile> 接收多文件
     */
    @PostMapping("multiUpload.json")
    public String multiUpload(HttpServletRequest request, MultipartFile[] files) throws Exception{
        String username = request.getParameter("username");
        String passwd = request.getParameter("passwd");

        // 如果没上传文件，files为空数据, fileCount为0
        int fileCount = files.length;

        MultipartFile file = files[0];

        // 参数名
        String paramName = file.getName();
        // 原始文件名
        String fileName = file.getOriginalFilename();
        // 文件类型，text/plain  img/jpg 等
        String contentType = file.getContentType();

        return "0 OF " + fileCount + "#" + paramName + "#" + fileName + "#" + contentType;
    }


    private String fetchPostData(HttpServletRequest request) throws Exception{
        RequestFacade rf = (RequestFacade) request;

        Field filed = RequestFacade.class.getDeclaredField("request");
        filed.setAccessible(true);
        Request innerReq = (Request) filed.get(rf);

        // 通过流获取
        return IOUtils.toString(innerReq.getInputStream());
    }

    static class RegisterParam {
        MultipartFile logo;
        MultipartFile certification;
        String username;
        Integer age;
        String passwd;

        /**
         * Getter method for property <tt>logo</tt>.
         *
         * @return property value of logo
         */
        public MultipartFile getLogo() {
            return logo;
        }

        /**
         * Setter method for property <tt>logo</tt>.
         *
         * @param logo value to be assigned to property logo
         */
        public void setLogo(MultipartFile logo) {
            this.logo = logo;
        }

        /**
         * Getter method for property <tt>certification</tt>.
         *
         * @return property value of certification
         */
        public MultipartFile getCertification() {
            return certification;
        }

        /**
         * Setter method for property <tt>certification</tt>.
         *
         * @param certification value to be assigned to property certification
         */
        public void setCertification(MultipartFile certification) {
            this.certification = certification;
        }

        /**
         * Getter method for property <tt>username</tt>.
         *
         * @return property value of username
         */
        public String getUsername() {
            return username;
        }

        /**
         * Setter method for property <tt>username</tt>.
         *
         * @param username value to be assigned to property username
         */
        public void setUsername(String username) {
            this.username = username;
        }

        /**
         * Getter method for property <tt>age</tt>.
         *
         * @return property value of age
         */
        public Integer getAge() {
            return age;
        }

        /**
         * Setter method for property <tt>age</tt>.
         *
         * @param age value to be assigned to property age
         */
        public void setAge(Integer age) {
            this.age = age;
        }

        /**
         * Getter method for property <tt>passwd</tt>.
         *
         * @return property value of passwd
         */
        public String getPasswd() {
            return passwd;
        }

        /**
         * Setter method for property <tt>passwd</tt>.
         *
         * @param passwd value to be assigned to property passwd
         */
        public void setPasswd(String passwd) {
            this.passwd = passwd;
        }
    }
}
```



### 自定义文件上传参数

springboot的自动装配机制会自动帮我们注入一个MultipartResolver，进行文件上传表单的解析，其中有一些默认配置参数。

一般来说，默认配置参数无法满足特定的业务需求，我们可以通过自行注入MultipartResolver的实现类，来自定义文件上传参数，比如允许的最大文件大小。

```
# bean name 约定为 multipartResolver
@Bean(name = "multipartResolver")
public CommonsMultipartResolver multipartResolver() {
    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
    multipartResolver.setMaxUploadSize(100*1024*1024);
    multipartResolver.setMaxUploadSizePerFile(10*1024*1024)
    return multipartResolver;
}
```

也可以在application.properties中通过配置的方式指定：

```
# 上传的单个文件最大的大小
spring.servlet.multipart.max-file-size=10MB
# 本次请求所有文件的大小
spring.servlet.multipart.max-request-size=100MB
```



自动装配逻辑参考：org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration