package org.fellow99.tpl.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * tpl-workspace 管理后台启动类
 *
 * @author tpl-manage
 */
@SpringBootApplication(scanBasePackages = {"org.fellow99.tpl", "org.dromara"})
public class TplManageApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(TplManageApplication.class, args);
    }

}
