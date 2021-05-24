/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 */
package cc.lzy.file.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 *
 * @author taigai
 * @version : AuthorityFilter.java, v 0.1 2021年05月23日 23:36 taigai Exp $
 */
public class AuthorityFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AuthorityFilter init, init params: " + filterConfig.getInitParameter("name"));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("this is AuthorityFilter, uri: " + ((HttpServletRequest) request).getRequestURI());
        System.out.println("this is AuthorityFilter, url: " + ((HttpServletRequest) request).getRequestURL());
        request.setCharacterEncoding("GBK");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("AuthorityFilter destroy");
    }
}