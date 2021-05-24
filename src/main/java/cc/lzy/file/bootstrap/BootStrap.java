/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 */
package cc.lzy.file.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 *
 * @author taigai
 * @version : BootStrap.java, v 0.1 2021年05月22日 17:30 taigai Exp $
 */
@SpringBootApplication(scanBasePackages = "cc.lzy.file")
@ServletComponentScan("cc.lzy.file")
public class BootStrap {
    public static void main(String[] args) {
        SpringApplication.run(BootStrap.class, args);
    }
}