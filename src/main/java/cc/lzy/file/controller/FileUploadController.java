/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 */
package cc.lzy.file.controller;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.StringJoiner;

/**
 * 文件上传Controller
 *
 * @author taigai
 * @version : FileUploadController.java, v 0.1 2021年05月22日 17:37 taigai Exp $
 */
@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @GetMapping("/getUser.json")
    public String getInfo(@RequestParam String userId, @RequestParam String userName) {
        return userId + "@" + userName;
    }

    /**
     * 普通表单项, 获取真实提交内容
     */
    @PostMapping("register.json")
    public String register(HttpServletRequest request) throws Exception{

        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        String passwd = request.getParameter("passwd");

        return username + "@" + age + "@" + passwd;
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