package com.yuanmai.cloud;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.huaweicloud.lts.appender.JavaSDKAppender;
import com.huaweicloud.lts.producer.Callback;
import com.huaweicloud.lts.producer.Producer;
import com.huaweicloud.lts.producer.model.log.LogContent;
import com.huaweicloud.lts.producer.model.log.LogItem;
import com.huaweicloud.lts.producer.model.log.LogItems;
import com.yuanmai.components.cloud.log.CloudLogProducer;
import com.yuanmai.components.cloud.log.objects.Item;
import com.yuanmai.jackson.Jackson;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.janino.JaninoOption;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author xux
 * @date 2023年04月23日 13:54:20
 */
@Slf4j
public class HuaweicloudLogProducer implements CloudLogProducer, Closeable {

    @Value("${cloud.huawei.sls.project:null}")
    private String project;
    @Value("${cloud.huawei.access-key-id:null}")
    private String accessKeyId;
    @Value("${cloud.huawei.access-key-secret:null}")
    private String accessKeySecret;
    @Value("${cloud.huawei.sls.region:cn-east-2}")
    private String region;
    @Value("${cloud.huawei.lts.region:cn-shanghai.log.aliyuncs.com}")
    private String endpoint;
    @Value("${cloud.huawei.lts.totalSizeInBytes:104857600}")
    private int totalSizeInBytes;
    /**
     * producer发送日志时阻塞时间
     */
    @Value("${cloud.huawei.lts.maxBlockMs:0}")
    private long maxBlockMs;
    /**
     * 执行日志发送任务的线程池大小
     */
    @Value("${cloud.huawei.lts.ioThreadCount:8}")
    private int ioThreadCount;
    @Value("${cloud.huawei.lts.batchSizeThresholdInBytes:524288}")
    private int batchSizeThresholdInBytes;
    /**
     * producer发送单批日志条数上限
     */
    @Value("${cloud.huawei.lts.batchCountThreshold:4096}")
    private int batchCountThreshold;
    @Value("${cloud.huawei.lts.lingerMs:2000}")
    private int lingerMs;
    @Value("${cloud.huawei.lts.retries:3}")
    private int retries;
    @Value("${cloud.huawei.lts.baseRetryBackoffMs:100}")
    private long baseRetryBackoffMs;
    @Value("${cloud.huawei.lts.maxRetryBackoffMs:100}")
    private long maxRetryBackoffMs;
    @Value("${cloud.huawei.lts.test:false}")
    private boolean enableLocalTest;
    private Producer producer;
    @PostConstruct
    public void init(){
        // 构建appender
        JavaSDKAppender appender = JavaSDKAppender.custom()
                // 华为云帐号的项目ID（project id）
                .setProjectId(project)
                // 华为云帐号的AK
                .setAccessKeyId(accessKeyId)
                // 华为云帐号的SK
                .setAccessKeySecret(accessKeySecret)
                // 云日志服务的区域
                .setRegionName(region)
                // 单个Appender能缓存的日志大小上限
                .setTotalSizeInBytes(totalSizeInBytes)
                // producer发送日志时阻塞时间
                .setMaxBlockMs(maxBlockMs)
                // 执行日志发送任务的线程池大小
                .setIoThreadCount(ioThreadCount)
                // producer发送单批日志量上限
                .setBatchSizeThresholdInBytes(batchSizeThresholdInBytes)
                // producer发送单批日志条数上限
                .setBatchCountThreshold(batchCountThreshold)
                // producer发送单批日志等待时间
                .setLingerMs(lingerMs)
                // producer发送日志失败后重试次数
                .setRetries(retries)
                // 首次重试的退避时间
                .setBaseRetryBackoffMs(baseRetryBackoffMs)
                // 重试的最大退避时间
                .setMaxRetryBackoffMs(maxRetryBackoffMs)
                // 默认false, true: 可以跨云上报日志, false: 仅能在华为云ecs主机上报日志
                 .setEnableLocalTest(enableLocalTest)
                // 超过1M的日志, 拆分后丢弃大于1M的数据
                // .setGiveUpExtraLongSingleLog(true)
                .builder();
        producer = appender.getProducer();
    }



    @Override
    public void send(String group, String topic, String source, Item item, Object callback) {
        if(!Strings.isNullOrEmpty(source)){
            if(item.getLabels() == null){
                item.setLabels(Maps.newHashMapWithExpectedSize(1));
            }
            item.add("source",source);
        }
        send(group,topic,HuaweicloudLogMapStructs.INSTANCE.to(item), (Callback) callback);
    }

    @Override
    public void send(String group, String topic, String source, List<Item> items, Object callback) {
        if(!Strings.isNullOrEmpty(source)){
            items.forEach(item->{
                if(item.getLabels() == null){
                    item.setLabels(Maps.newHashMapWithExpectedSize(1));
                }
                item.add("source",source);
            });
        }
        send(group,topic,HuaweicloudLogMapStructs.INSTANCE.toLogItems(items), (Callback) callback);
    }

    @Override
    public void sendλ(String group, String topic, String source, Map<String, String> map, Object callback) {
        Map<String,String> labels = Maps.newHashMapWithExpectedSize(1);
        if(!Strings.isNullOrEmpty(source)){
            labels.put("source",source);
        }
        LogItem logItem = new LogItem();
        logItem.setLabels(Jackson.object2Json(labels));
        logItem.setContents(List.of(HuaweicloudLogMapStructs.INSTANCE.toContents(map)));
        send(group,topic,logItem, (Callback) callback);
    }

    @Override
    public void sendλ(String group, String topic, String source, List<Map<String, String>> list, Object callback) {
        Map<String,String> labels = Maps.newHashMapWithExpectedSize(1);
        if(!Strings.isNullOrEmpty(source)){
            labels.put("source",source);
        }
        LogItem logItem = new LogItem();
        logItem.setLabels(Jackson.object2Json(labels));
        logItem.setContents(HuaweicloudLogMapStructs.INSTANCE.maps2List(list));
        send(group,topic,logItem, (Callback) callback);
    }




    private void send(String logStore, String topic, LogItem logItem, Callback callback){
        send(logStore,topic,List.of(logItem),callback);
    }
    private void send(String logStore,String topic,List<LogItem> items,Callback callback){
        try {
            producer.send(logStore,topic, items,callback);
        }catch (Exception ex){
            log.error("Failed to send log, project={}, logStore={},topic={}",project,logStore,topic, ex);
        }
    }

    @PreDestroy
    @Override
    public void close() throws IOException {
        if (producer != null) {
            try {
                producer.close();
            } catch (Exception e) {
                log.error("Failed to close LoghubAppender.", e);
            }
        }
    }
}
