package com.config;


import com.Constant;
import com.alibaba.fastjson.JSON;
import com.manager.ServerInfoManager;
import com.pojo.ServerInfo;
import com.util.ContextUtil;
import com.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class ZookeeperConfig {

    @Autowired
    private ServerInfo serverInfo;


    @PostConstruct
    public void curatorFramework() throws Exception {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(300, 3, 1000);
        CuratorFramework curator = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1")
                .namespace("shoot")
                .sessionTimeoutMs(2000)
                .retryPolicy(retryPolicy).build();

        curator.start();

        //递规创建路径，用在第一次在系统中启动时创建路径
        curator.checkExists().creatingParentContainersIfNeeded().forPath(Constant.ZOOKEEPER_PATH);

        //加入路径监听
        final PathChildrenCache childrenCache = new PathChildrenCache(curator, Constant.ZOOKEEPER_PATH, true);
        try {
            childrenCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


        childrenCache.getListenable().addListener(
                (client, event) -> {
                    switch (event.getType()) {
                        case CHILD_ADDED:
                            ServerInfoManager.addServer(Util.transToServerInfo(event.getData().getPath()));
                            break;
                        case CHILD_REMOVED:
                            ServerInfoManager.removeServer(Util.transToServerInfo(event.getData().getPath()));
                            break;
                        default:
                            break;
                    }
                }
        );
        //创建临时节点
        String uuid = ContextUtil.id + "==" + JSON.toJSONString(serverInfo);
        curator.create().withMode(CreateMode.EPHEMERAL).forPath(Constant.ZOOKEEPER_PATH + "/" + uuid, uuid.getBytes());

    }
}