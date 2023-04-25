package com.yuanmai.cloud.huaweicloud.log;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.huaweicloud.lts.producer.Callback;
import com.huaweicloud.lts.producer.Result;
import com.yuanmai.components.cloud.log.CloudLogProducer;
import com.yuanmai.components.cloud.log.objects.Content;
import com.yuanmai.components.cloud.log.objects.Item;
import com.yuanmai.components.cloud.log.objects.Log;
import com.yuanmai.components.cloud.log.objects.Logs;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Map;

/**
 * @author xux
 * @date 2023年04月23日 22:00:23
 */
@Configuration @Slf4j
@SpringJUnitConfig(classes = LogProducer.class,initializers = YuanmaiSpringInitializer.class)
public class LogProducer {

    @Autowired
    private CloudLogProducer producer;

    @Value("${cloud.huawei.lts.groups.haibao}")
    private String group;
    @Value("${cloud.huawei.lts.streams.keywords}")
    private String keywords;

    @Bean
    public CloudLogProducer cloudLogProducer(){
        return new HuaweicloudLogProducer();
    }

    @Test
    public void keywords(){
        producer.send(group, keywords, "test", new Item(List.of(new Content("userId","123"),new Content("shopCode","888888"),new Content("groupName","河南"),new Content("keywords","卡布灯箱"))), (Callback) result -> {
            log.info("{}",result);
        });
    }

    @Test
    public void keywords1(){
        Map<String,Object> map = Maps.newHashMapWithExpectedSize(3);
        map.put("userId",123);
        map.put("shopCode","888888");
        map.put("groupName","河南");
        map.put("keywords","卡布灯箱");
       producer.send(Log.builder().group(group).topic(keywords).content(map).build());
    }

    @Test
    public void keywords2(){
        List<Map<String,Object>> list = Lists.newArrayListWithCapacity(3);
        Map<String,Object> map = Maps.newHashMapWithExpectedSize(3);
        map.put("userId",123);
        map.put("shopCode","888888");
        map.put("groupName","河南");
        map.put("keywords","卡布灯箱");
        Map<String,Object> map1 = Maps.newHashMapWithExpectedSize(3);
        map1.put("userId",123);
        map1.put("shopCode","888888");
        map1.put("groupName","河南");
        map1.put("keywords","在哪");
        list.add(map1);
        Map<String,Object> map2 = Maps.newHashMapWithExpectedSize(3);
        map2.put("userId",123);
        map2.put("shopCode","888888");
        map2.put("groupName","河南");
        map2.put("keywords","查询");
        list.add(map2);
        producer.send(Logs.<Map<String,Object>>builder().group(group).topic(keywords).contents(list).build());
    }
}
