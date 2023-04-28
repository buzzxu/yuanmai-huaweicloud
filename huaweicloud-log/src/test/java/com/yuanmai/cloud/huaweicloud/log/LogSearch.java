package com.yuanmai.cloud.huaweicloud.log;

import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.lts.v2.LtsClient;
import com.huaweicloud.sdk.lts.v2.model.*;
import com.yuanmai.components.cloud.log.CloudLogService;
import com.yuanmai.components.cloud.log.objects.Item;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

/**
 * @author xux
 * @date 2023年04月25日 14:03:10
 */
@Configuration
@Slf4j
@SpringJUnitConfig(classes = LogSearch.class,initializers = YuanmaiSpringInitializer.class)
public class LogSearch {


    @Autowired
    private CloudLogService cloudLogService;
    @Bean
    public CloudLogService cloudLogService(){
        return new HuaweiLtsClient();
    }


    @Test
    public void search1(){
        List<Item> items = cloudLogService.query("e0a38102-b1f7-4e85-b7f6-a82dd3123b74","",1682294400000L,1682674820974L,"SELECT keywords ,count(1) as number group by keywords");
        items.forEach(v->log.info(v.json()));
    }
}
