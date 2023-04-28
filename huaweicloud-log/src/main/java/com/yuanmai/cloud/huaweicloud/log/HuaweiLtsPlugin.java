package com.yuanmai.cloud.huaweicloud.log;

import com.yuanmai.Plugin;

import java.util.Set;

/**
 * @author xux
 * @date 2023年04月25日 14:01:59
 */
public class HuaweiLtsPlugin implements Plugin {

    @Override
    public Set<Class<?>> classes() {
        return Set.of(HuaweiLtsConfigure.class);
    }
}
