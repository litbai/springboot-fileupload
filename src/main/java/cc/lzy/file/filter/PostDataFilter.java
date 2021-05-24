/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 */
package cc.lzy.file.filter;

import org.apache.commons.io.IOUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author taigai
 * @version : PostDataFilter.java, v 0.1 2021年05月23日 12:51 taigai Exp $
 */
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