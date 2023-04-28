package com.yuanmai.cloud.huaweicloud.log;

import com.yuanmai.util.Dates;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author xux
 * @date 2023年04月25日 14:30:08
 */
public class SimpleTest {


    @Test
    public void aa(){
        System.out.println( LocalDateTime.parse("2023-04-24 00:00:00", Dates.DATE_FORMAT_DATETIME).toInstant(ZoneOffset.UTC).toEpochMilli());
        System.out.println( LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
    }
}
