package com;

import com.hazelcast.config.Config;
import com.node.Node;
import com.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public abstract class BaseVerticle {

    @Autowired
    private Config hazelCastConfig;

    @PostConstruct
    void init() throws ExecutionException, InterruptedException {

        start();
    }


    public void start() {
        log.info("启动vertx");

        Node node = new Node();
        node.setBaseReceiver(SpringUtils.getBeansOfType(BaseReceiver.class).values().iterator().next());
        new Thread(node::start).start();
    }

}
