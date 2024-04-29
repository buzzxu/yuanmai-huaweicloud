package com.yuanmai.cloud.huaweicloud.log;

import com.google.common.collect.Lists;
import com.huaweicloud.lts.producer.exception.LogException;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.http.HttpConfig;
import com.huaweicloud.sdk.lts.v2.LtsClient;
import com.huaweicloud.sdk.lts.v2.model.ListStructuredLogsWithTimeRangeRequest;
import com.huaweicloud.sdk.lts.v2.model.ListStructuredLogsWithTimeRangeResponse;
import com.huaweicloud.sdk.lts.v2.model.QueryLtsStructLogParamsNew;
import com.huaweicloud.sdk.lts.v2.model.TimeRange;
import com.huaweicloud.sdk.lts.v2.region.LtsRegion;
import com.yuanmai.components.cloud.log.CloudLogService;
import com.yuanmai.components.cloud.log.objects.Item;
import com.yuanmai.util.Dates;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xux
 * @date 2023年04月28日 9:57:32
 */
public class HuaweiLtsClient implements CloudLogService {

    @Value("${cloud.huawei.lts.project:null}")
    private String project;
    @Value("${cloud.huawei.access-key-id:null}")
    private String accessKeyId;
    @Value("${cloud.huawei.access-key-secret:null}")
    private String accessKeySecret;
    @Value("${cloud.huawei.lts.region:cn-east-2}")
    private String region;
    private LtsClient client;

    @Override
    public List<Item> query(String group, String topic, LocalDateTime from, LocalDateTime to, String query) {
        return this.query(group, topic, from.toInstant(ZoneOffset.UTC).toEpochMilli(),  to.toInstant(ZoneOffset.UTC).toEpochMilli(), query);
    }

    @Override
    public List<Item> query(String group, String topic, LocalDateTime from, LocalDateTime to, String query, int limit, int offset, boolean reverse, boolean powerSql) {
        return query(group,topic,from,to,query);
    }

    @Override
    public List<Item> query(String group, String topic, long from,long to,String query) {
        return getLogs(group,from,to,query);
    }

    private List<Item> getLogs(String logStore, long from, long to, String query){
        try {
            ListStructuredLogsWithTimeRangeRequest request = new ListStructuredLogsWithTimeRangeRequest();
            request.setLogStreamId(logStore);
            QueryLtsStructLogParamsNew paramsNew = new QueryLtsStructLogParamsNew();
            request.withBody(paramsNew.withWhetherToRows(true)
                    .withQuery(query)
                    .withFormat("k-v")
                    .withTimeRange(new TimeRange().withSqlTimeZone("UTC")
                            .withStartTime(String.valueOf(from))
                            .withEndTime(String.valueOf(to))
                            .withStartTimeGt(true)
                            .withEndTimeLt(true)));
            ListStructuredLogsWithTimeRangeResponse response = client.listStructuredLogsWithTimeRange(request);
            if(response.getHttpStatusCode() == 200){
                Map<String, List<Object>> body ;
                if((body = response.getBody()) != null){
                   return HuaweicloudLogMapStructs.INSTANCE.toItem(body);
                }
            }
            return Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    @PostConstruct
    public void init(){
        // 配置客户端属性
        HttpConfig config = HttpConfig.getDefaultHttpConfig();
        config.withIgnoreSSLVerification(true);

        // 创建认证
        BasicCredentials auth = new BasicCredentials()
                .withAk(accessKeyId)
                .withSk(accessKeySecret)
//                .withProjectId(project)
                ;
        client =  LtsClient.newBuilder()
//                .withHttpConfig(config)
                .withCredential(auth)
                .withRegion(LtsRegion.valueOf(region))
                .build();
    }

}
