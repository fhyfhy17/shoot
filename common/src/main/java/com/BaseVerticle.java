package com;

import com.entry.PlayerEntry;
import com.enums.CacheEnum;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.node.Node;
import com.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public abstract class BaseVerticle {

    @Autowired
    private Config hazelCastConfig;

    private HazelcastInstance hazelcastInstance;

    @PostConstruct
    void init() {
        log.info("启动node");
        hazelcastInstance = Hazelcast.newHazelcastInstance(hazelCastConfig);
        IMap<Long, PlayerEntry> map = hazelcastInstance.getMap(CacheEnum.PlayerEntryCache.name());


        Node node = new Node();
        node.setBaseReceiver(SpringUtils.getBeansOfType(BaseReceiver.class).values().iterator().next());
        new Thread(node::start).start();
    }


    @Bean(destroyMethod = "", name = "ha")
    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

}
