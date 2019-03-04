package com.config;

import com.dao.cache.PlayerDBStore;
import com.dao.cache.UnionDBStore;
import com.dao.cache.UserDBStore;
import com.entry.PlayerEntry;
import com.entry.UnionEntry;
import com.entry.UserEntry;
import com.enums.CacheEnum;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.builders.WriteBehindConfigurationBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class EhcacheCacheConfig {

    private CacheManager cacheManager;

    @Bean("cacheManager")
    public CacheManager getCacheManager() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
        return cacheManager;
    }


    @Autowired
    private PlayerDBStore playerDBStore;
    @Autowired
    private UserDBStore userDBStore;
    @Autowired
    private UnionDBStore unionDBStore;


    @Bean
    public Cache playerEntryCache() {

        Cache<Long, PlayerEntry> playerEntryCache = cacheManager.createCache(CacheEnum.PlayerEntryCache.name(),
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, PlayerEntry.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, MemoryUnit.GB))
                        .withLoaderWriter(playerDBStore)
                        .add(WriteBehindConfigurationBuilder
                                .newBatchedWriteBehindConfiguration(1, TimeUnit.SECONDS, 1000)
                                .queueSize(3000)
                                .concurrencyLevel(1)
                                .enableCoalescing()

                        )
                        .build());

        return playerEntryCache;
    }

    @Bean
    public Cache userEntryCache() {
        Cache<Long, UserEntry> userEntryCache = cacheManager.createCache(CacheEnum.UserEntryCache.name(),
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, UserEntry.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, MemoryUnit.GB))
                        .withLoaderWriter(userDBStore)
                        .add(WriteBehindConfigurationBuilder
                                .newBatchedWriteBehindConfiguration(1, TimeUnit.SECONDS, 1000)
                                .queueSize(3000)
                                .concurrencyLevel(1)
                                .enableCoalescing()

                        )
                        .build());

        return userEntryCache;
    }

    @Bean
    public Cache unionEntryCache() {
        Cache<Long, UnionEntry> unionEntryCache = cacheManager.createCache(CacheEnum.UnionEntryCache.name(),
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, UnionEntry.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, MemoryUnit.GB))
                        .withLoaderWriter(unionDBStore)
                        .add(WriteBehindConfigurationBuilder
                                .newBatchedWriteBehindConfiguration(1, TimeUnit.SECONDS, 1000)
                                .queueSize(3000)
                                .concurrencyLevel(1)
                                .enableCoalescing()

                        )
                        .build());

        return unionEntryCache;
    }

}
