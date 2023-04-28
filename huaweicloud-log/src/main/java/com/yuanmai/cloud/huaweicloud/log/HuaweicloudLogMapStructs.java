package com.yuanmai.cloud.huaweicloud.log;

import com.google.common.collect.Lists;
import com.huaweicloud.lts.producer.model.log.LogContent;
import com.huaweicloud.lts.producer.model.log.LogItem;
import com.yuanmai.components.cloud.log.objects.Content;
import com.yuanmai.components.cloud.log.objects.Item;
import com.yuanmai.jackson.Jackson;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xux
 * @date 2023年04月23日 16:11:57
 */
@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
        ,unmappedTargetPolicy = ReportingPolicy.IGNORE
        ,nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
        ,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface HuaweicloudLogMapStructs {
    HuaweicloudLogMapStructs INSTANCE = Mappers.getMapper(HuaweicloudLogMapStructs.class);



    default LogItem to(Item obj){
        LogItem logItem = new LogItem();
        logItem.setLabels(obj.getLabels() != null ? Jackson.object2Json(obj.getLabels()) : "{}");
        logItem.setContents(toContents(obj.getContents()));
        return  logItem;
    }

    default LogContent toContents(Map<String,String> map){
        return new LogContent(System.currentTimeMillis() * 1000000L + System.nanoTime() % 1000000L, Jackson.object2Json(map));
    }
    List<LogItem> toLogItems(List<Item> items);

    List<LogContent> toContents(List<Content> list);
    default LogContent to(Content obj){
        return new LogContent(System.currentTimeMillis() * 1000000L + System.nanoTime() % 1000000L,Jackson.object2Json(Map.of(obj.getKey(),obj.getValue())));
    }

    default List<LogContent> maps2List(List<Map<String,String>> datas){
        return datas.stream().map(this::toContents).toList();
    }


    default List<Item> toItem(Map<String, List<Object>> body){
        List<Object> result = body.get("result");
        return result.stream().map(v->{
            Item item = new Item();
            if(v instanceof Map<?,?> value){
                value.forEach((key,$value)-> item.add((String) key,String.valueOf($value)));
            }else{
                throw new IllegalArgumentException("数据转换失败,");
            }
            return item;
        }).toList();
    }

    default List<Item> toItem1(Map<String, List<Object>> body){
        int size = body.values().stream().findFirst().orElse(Collections.emptyList()).size();
        List<Item> items = Lists.newArrayListWithCapacity(size);
        Set<String> keys = body.keySet();
        for(int i =0;i<size;i++){
            Item item = new Item();
            int tempIndex = i;
            keys.forEach(key->{
                item.add(key,String.valueOf(body.get(key).get(tempIndex)));
            });
            items.add(item);
        }
        return items;
    }
}
