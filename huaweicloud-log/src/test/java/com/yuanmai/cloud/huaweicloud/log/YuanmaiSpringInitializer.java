package com.yuanmai.cloud.huaweicloud.log;

import com.yuanmai.annotation.Yuanmai;
import com.yuanmai.test.SpringInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author xux
 * @date 2023年01月12日 10:57:15
 */
@ComponentScan(basePackages = "com.yuanmai.*")
@Yuanmai(propertySource = "log.yml")
public class YuanmaiSpringInitializer extends SpringInitializer {
    protected YuanmaiSpringInitializer() {
        super(YuanmaiSpringInitializer.class);
    }
}
