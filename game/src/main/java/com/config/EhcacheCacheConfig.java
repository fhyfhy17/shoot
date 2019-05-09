package com.config;

import com.dao.cache.BagDBStore;
import com.dao.cache.NoCellBagDBStore;
import com.dao.cache.PlayerDBStore;
import com.dao.cache.UnionDBStore;
import com.dao.cache.UserDBStore;
import com.entry.BagEntry;
import com.entry.NoCellBagEntry;
import com.entry.PlayerEntry;
import com.entry.UnionEntry;
import com.entry.UserEntry;
import com.enums.CacheEnum;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.PooledExecutionServiceConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.builders.WriteBehindConfigurationBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class EhcacheCacheConfig {

    private CacheManager cacheManager;

    private static final String poolName = "writeBackPool";
    private static final String defaultName = "defaultPool";
    private static final int queueSize = 4096;
    private static final int concurrencyLevel = 3;


    @Bean("cacheManagerMy")
    public CacheManager getCacheManager() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .using(PooledExecutionServiceConfigurationBuilder
                        .newPooledExecutionServiceConfigurationBuilder()
                        .defaultPool(defaultName,1,5)
                        .pool(poolName, 1, 5).build())
                .build(true);
        return cacheManager;
    }


    @Autowired
    private PlayerDBStore playerDBStore;
    @Autowired
    private UserDBStore userDBStore;
    @Autowired
    private UnionDBStore unionDBStore;
    @Autowired
    private BagDBStore bagDBStore;
    @Autowired
    private NoCellBagDBStore noCellBagDBStore;

    @DependsOn("cacheManagerMy")
    @Bean
    public Cache playerEntryCache() {

        Cache<Long, PlayerEntry> playerEntryCache = cacheManager.createCache(CacheEnum.PlayerEntryCache.name(),
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, PlayerEntry.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, MemoryUnit.GB))
                        .withLoaderWriter(playerDBStore)
                        .add(WriteBehindConfigurationBuilder
                                .BatchedWriteBehindConfigurationBuilder
                                .newUnBatchedWriteBehindConfiguration()
                                .queueSize(queueSize)
                                .concurrencyLevel(concurrencyLevel)
                                .useThreadPool(poolName)
                        )
                        .build());

        return playerEntryCache;
    }
    @DependsOn("cacheManagerMy")
    @Bean
    public Cache bagEntryCache() {

        Cache<Long, BagEntry> bagEntryCache = cacheManager.createCache(CacheEnum.BagEntryCache.name(),
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, BagEntry.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, MemoryUnit.GB))
                        .withLoaderWriter(bagDBStore)
                        .add(WriteBehindConfigurationBuilder
                                .newUnBatchedWriteBehindConfiguration()
                                .queueSize(queueSize)
                                .concurrencyLevel(concurrencyLevel)
                                .useThreadPool(poolName)

                        )
                        .build());

        return bagEntryCache;
    }
    @DependsOn("cacheManagerMy")
    @Bean
    public Cache noCellBagEntryCache() {
        
        Cache<Long,NoCellBagEntry> noCellBagEntryCache = cacheManager.createCache(CacheEnum.NoCellBagEntryCache.name(),
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, NoCellBagEntry.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, MemoryUnit.GB))
                        .withLoaderWriter(noCellBagDBStore)
                        .add(WriteBehindConfigurationBuilder
                                .newUnBatchedWriteBehindConfiguration()
                                .queueSize(queueSize)
                                .concurrencyLevel(concurrencyLevel)
                                .useThreadPool(poolName)
                        
                        )
                        .build());
        
        return noCellBagEntryCache;
    }
    
    @DependsOn("cacheManagerMy")
    @Bean
    public Cache userEntryCache() {
        Cache<Long, UserEntry> userEntryCache = cacheManager.createCache(CacheEnum.UserEntryCache.name(),
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, UserEntry.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, MemoryUnit.GB))
                        .withLoaderWriter(userDBStore)
                        .add(WriteBehindConfigurationBuilder
                                .newUnBatchedWriteBehindConfiguration()
                                .queueSize(queueSize)
                                .concurrencyLevel(concurrencyLevel)
                                .useThreadPool(poolName)

                        )
                        .build());

        return userEntryCache;
    }
    @DependsOn("cacheManagerMy")
    @Bean
    public Cache unionEntryCache() {
        Cache<Long, UnionEntry> unionEntryCache = cacheManager.createCache(CacheEnum.UnionEntryCache.name(),
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, UnionEntry.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, MemoryUnit.GB))
                        .withLoaderWriter(unionDBStore)
                        .add(WriteBehindConfigurationBuilder
                                .newUnBatchedWriteBehindConfiguration()
                                .queueSize(queueSize)
                                .concurrencyLevel(concurrencyLevel)
                                .useThreadPool(poolName)

                        )
                        .build());

        return unionEntryCache;
    }

}
