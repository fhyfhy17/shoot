package com.config;

import com.Constant;
import com.manager.ServerInfoManager;
import com.pojo.ServerInfo;
import com.util.ContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteAtomicSequence;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.DeploymentMode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.events.DiscoveryEvent;
import org.apache.ignite.events.EventType;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.zk.TcpDiscoveryZookeeperIpFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@Slf4j
public class IgniteConfig {

    @Autowired
    private ServerInfo serverInfo;

    @Bean("myIg")
    public Ignite igConfig() {


        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setUserAttributes(Collections.singletonMap(Constant.SERVER_INFO, serverInfo));
        cfg.setIgniteInstanceName(ContextUtil.id);
        cfg
                .setDeploymentMode(DeploymentMode.SHARED)
//                .setPeerClassLoadingEnabled(true)
//                .setBinaryConfiguration(new BinaryConfiguration()
//                        .setIdMapper(new BinaryBasicIdMapper())
//                        .setNameMapper(new BinaryBasicNameMapper())
//                        .setCompactFooter(true))
                .setCommunicationSpi(new TcpCommunicationSpi()
                        .setLocalAddress("localhost"))
                .setDiscoverySpi(new TcpDiscoverySpi()
                        .setIpFinder(new TcpDiscoveryZookeeperIpFinder()
                                .setZkConnectionString("127.0.0.1:2181")
                        )
//                        .setIpFinder(new TcpDiscoveryVmIpFinder()
//                                .setAddresses(Arrays.asList("127.0.0.1:47500..47509")
//                                )
//                        )
                )

                .setMetricsLogFrequency(0);
        Ignite start = Ignition.start(cfg);
        for (ClusterNode node : start.cluster().forRemotes().nodes()) {
            Object attribute = node.attribute(Constant.SERVER_INFO);
            if (notServerInfo(attribute)) {
                continue;
            }
            ServerInfoManager.addServer(node.attribute(Constant.SERVER_INFO));
        }
        start.events().localListen(event -> {
            DiscoveryEvent e = (DiscoveryEvent) event;
            if (e.type() == EventType.EVT_NODE_JOINED) {
                Object attribute = e.eventNode().attribute(Constant.SERVER_INFO);
                if (notServerInfo(attribute)) {
                    return true;
                }
                ServerInfoManager.addServer((ServerInfo) attribute);
                return true;
            }

            if (e.type() == EventType.EVT_NODE_LEFT || e.type() == EventType.EVT_NODE_FAILED) {
                Object attribute = e.eventNode().attribute(Constant.SERVER_INFO);
                if (notServerInfo(attribute)) {
                    return true;
                }

                ServerInfoManager.removeServer((ServerInfo) attribute);
                return true;
            }

            return false;
        }, EventType.EVT_NODE_FAILED, EventType.EVT_NODE_LEFT, EventType.EVT_NODE_JOINED);



        return start;
    }

    private boolean notServerInfo(Object o) {
        return o == null || !o.getClass().isAssignableFrom(ServerInfo.class);
    }
}