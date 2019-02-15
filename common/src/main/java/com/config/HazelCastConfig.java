package com.config;

import com.Constant;
import com.hazelcast.config.*;
import com.pojo.ServerInfo;
import com.util.ContextUtil;
import com.util.IpUtil;
import com.util.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@Slf4j
public class HazelCastConfig {

    @Autowired
    private ServerInfo serverInfo;
    @Autowired(required = false)
    private List<MapConfig> mapConfigs;

    @Bean(name = "hazelcast")
    public Config config() {

        Config config = new Config(ContextUtil.id + "-hazelcastConfig");
        //本地服务名
        config.setInstanceName(ContextUtil.id);
        config.getMemberAttributeConfig().setAttributes(Collections.singletonMap(Constant.SERVER_INFO, serverInfo));

        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getAwsConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getGcpConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getAzureConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getKubernetesConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getEurekaConfig().setEnabled(false);

        AtomicInteger i = new AtomicInteger(20);
        ReflectionUtil.getSerializeClasses().forEach(x -> {
            config.getSerializationConfig().addSerializerConfig(new SerializerConfig()
                    .setImplementation(new PstStreamSerializer<>(x, i.incrementAndGet()))
                    .setTypeClass(x));

        });


        //自身IP
        TcpIpConfig tcpIpConfig = config.getNetworkConfig().getJoin().getTcpIpConfig();
        tcpIpConfig.setEnabled(true);
        tcpIpConfig.addMember(IpUtil.getHostIp());
        //发现IP
        InterfacesConfig interfaces = config.getNetworkConfig().getInterfaces();

        interfaces.setEnabled(true);
        interfaces.addInterface("10.0.*.*");

        if (!Objects.isNull(mapConfigs)) {
            for (MapConfig mapConfig : mapConfigs) {
                config.addMapConfig(mapConfig);
            }
        }

        config.addListenerConfig(
                new ListenerConfig("com.config.ClusterMembershipListener"));
        return config;
    }
}

