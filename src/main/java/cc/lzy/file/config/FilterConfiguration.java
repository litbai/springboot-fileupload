/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 */
package cc.lzy.file.config;

import cc.lzy.file.filter.AuthorityFilter;
import com.google.common.collect.Lists;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author taigai
 * @version : FilterConfiguration.java, v 0.1 2021年05月23日 14:22 taigai Exp $
 */
@Configuration
public class FilterConfiguration {

    @Bean
    public FilterRegistrationBean authorityFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new AuthorityFilter());
        bean.addInitParameter("name", "lzy");
        bean.setUrlPatterns(Lists.newArrayList("/upload/register.json"));
        bean.setOrder(1);
        return bean;
    }
}