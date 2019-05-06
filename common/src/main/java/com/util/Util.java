package com.util;

import com.alibaba.fastjson.JSON;
import com.pojo.ServerInfo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName Util
 * @Description 通用工具类
 * @Author dafeng
 * @Date 2019/1/28 11:29
 **/
public class Util {
    /**
     * 通用 Map根据Value排序，注意返回的是LinkedHashMap
     *
     * @param map 需要排序的map
     * @param <K>
     * @param <V>
     * @return 有序的map
     */
    public static <K, V extends Comparable> LinkedHashMap<K, V> mapValueSort(Map<K, V> map) {
        return (LinkedHashMap<K, V>) map.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .collect(Collectors.toMap((Map.Entry<K, V> k) -> k.getKey(), (Map.Entry<K, V> v) -> v.getValue(), (k, v) -> v, LinkedHashMap::new));

    }
    
    
    /**
     * 通用 Map根据Value排序，返回值的List
     *
     * @param map 需要排序的map
     * @param <K>
     * @param <V>
     * @return value的List
     */
    public static <K, V extends Comparable> List<V> mapValueSortReturnList(Map<K, V> map) {
        List<V> collect=map.values().stream().sorted().collect(Collectors.toList());
        return collect;
    }

    /**
     * 数组转List
     *
     * @param us  要操作的数组
     * @param <T>
     * @return List
     */
    public static <T> List<T> arrayToList(T[] us) {
        return Arrays.stream(us).collect(Collectors.toList());
    }


    public static ServerInfo transToServerInfo(String serverString) {
        ServerInfo serverInfo = JSON.parseObject(serverString.split("==")[1], ServerInfo.class);
        return serverInfo;
    }
}
