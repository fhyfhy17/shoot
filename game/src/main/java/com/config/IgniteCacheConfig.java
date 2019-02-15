package com.config;

import com.dao.cache.PlayerDBStore;
import com.dao.cache.UnionDBStore;
import com.dao.cache.UserDBStore;
import com.enums.CacheEnum;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IgniteCacheConfig {

    @Autowired
    private PlayerDBStore playerDBStore;
    @Autowired
    private UserDBStore userDBStore;
    @Autowired
    private UnionDBStore unionDBStore;

    @Bean
    public MapConfig playerEntryCache() {
        MapConfig mapConfig = new MapConfig(CacheEnum.PlayerEntryCache.name());
        MapStoreConfig mapStoreConfig = new MapStoreConfig();

        mapStoreConfig.setClassName(CacheEnum.PlayerEntryCache.name())
                .setEnabled(true)
                .setWriteBatchSize(20)
                .setWriteCoalescing(true)
                .setWriteDelaySeconds(1)
                .setImplementation(playerDBStore);

        mapConfig.setMapStoreConfig(mapStoreConfig);

        return mapConfig;
    }

    @Bean
    public MapConfig userEntryCache() {
        MapConfig mapConfig = new MapConfig(CacheEnum.UserEntryCache.name());
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setClassName(CacheEnum.UserEntryCache.name())
                .setEnabled(true)
                .setWriteBatchSize(20)
                .setWriteCoalescing(true)
                .setWriteDelaySeconds(1)
                .setImplementation(userDBStore);
        mapConfig.setMapStoreConfig(mapStoreConfig);
        return mapConfig;
    }

    @Bean
    public MapConfig unionEntryCache() {
        MapConfig mapConfig = new MapConfig(CacheEnum.UnionEntryCache.name());
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setClassName(CacheEnum.UnionEntryCache.name())
                .setEnabled(true)
                .setWriteBatchSize(20)
                .setWriteCoalescing(true)
                .setWriteDelaySeconds(1)
                .setImplementation(unionDBStore);
        mapConfig.setMapStoreConfig(mapStoreConfig);
        return mapConfig;
    }

}
