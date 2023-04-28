package com.yuanmai.cloud.huaweicloud.log;

import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.http.HttpConfig;
import com.huaweicloud.sdk.core.region.Region;
import com.huaweicloud.sdk.lts.v2.LtsClient;
import com.huaweicloud.sdk.lts.v2.region.LtsRegion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xux
 * @date 2023年04月25日 13:54:27
 */
@Configuration
public class HuaweiLtsConfigure {


    @Value("${cloud.huawei.lts.project:null}")
    private String project;
    @Value("${cloud.huawei.access-key-id:null}")
    private String accessKeyId;
    @Value("${cloud.huawei.access-key-secret:null}")
    private String accessKeySecret;
    @Value("${cloud.huawei.lts.region:cn-east-2}")
    private String region;

    @Bean
    public LtsClient ltsClient(){
// 配置客户端属性
        HttpConfig config = HttpConfig.getDefaultHttpConfig();
        config.withIgnoreSSLVerification(true);

        // 创建认证
        BasicCredentials auth = new BasicCredentials()
                .withAk(accessKeyId)
                .withSk(accessKeySecret)
//                .withProjectId(project)
                ;
        return LtsClient.newBuilder()
//                .withHttpConfig(config)
                .withCredential(auth)
                .withRegion(LtsRegion.valueOf(region))
                .build();
    }
}
