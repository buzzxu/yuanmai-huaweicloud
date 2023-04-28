package com.yuanmai.cloud.huaweicloud.log;

import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.lts.v2.LtsClient;
import com.huaweicloud.sdk.lts.v2.model.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * @author xux
 * @date 2023年04月25日 14:03:10
 */
@Configuration
@Slf4j
@SpringJUnitConfig(classes = LogSearch.class,initializers = YuanmaiSpringInitializer.class)
public class LogSearch {

    @Autowired
    private LtsClient ltsClient;

    @Test
    public void search(){
        ListQueryStructuredLogsRequest request = new ListQueryStructuredLogsRequest();
        request.withLogGroupId("3e844277-9006-455a-8d04-fc4c22727305");
        request.withLogStreamId("e0a38102-b1f7-4e85-b7f6-a82dd3123b74");
        QueryLtsStructLogParams body = new QueryLtsStructLogParams();
        body.withSqlExpression("select shopCode");
        body.withStartTime("1682294400000");
        body.withEndTime("1682532856634");
        body.setOriginalContent(false);
        request.withBody(body);
        try {
            ListQueryStructuredLogsResponse response = ltsClient.listQueryStructuredLogs(request);
            System.out.println(response.toString());
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (RequestTimeoutException e) {
            e.printStackTrace();
        } catch (ServiceResponseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void search1(){
        System.out.println(ltsClient);
        ListStructuredLogsWithTimeRangeRequest request = new ListStructuredLogsWithTimeRangeRequest();
        request.setLogStreamId("e0a38102-b1f7-4e85-b7f6-a82dd3123b74");
        QueryLtsStructLogParamsNew paramsNew = new QueryLtsStructLogParamsNew();
        request.withBody(paramsNew.withQuery("SELECT keywords ,count(1) as number group by keywords").withFormat("k-v").withTimeRange(new TimeRange().withSqlTimeZone("UTC").withStartTime("1682294400000").withEndTime("1682674820974")));
        ListStructuredLogsWithTimeRangeResponse response = ltsClient.listStructuredLogsWithTimeRange(request);
        response.getBody().forEach(log::info);
    }
}
