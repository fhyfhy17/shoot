package com.config;

import com.Constant;
import com.hazelcast.config.*;
import com.pojo.ServerInfo;
import com.util.ContextUtil;
import com.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Configuration
@Slf4j
public class HazelCastConfig {

    @Autowired
    private ServerInfo serverInfo;
    @Autowired(required = false)
    private List<MapConfig> mapConfigs;

    @Bean(name = "hazelcast")
    public Config config() {
        ClasspathXmlConfig classpathXmlConfig = new ClasspathXmlConfig("cluster.xml");
        //本地服务名
        classpathXmlConfig.setInstanceName(ContextUtil.id);
        classpathXmlConfig.getMemberAttributeConfig().setAttributes(Collections.singletonMap(Constant.SERVER_INFO, serverInfo));
        //自身IP
        TcpIpConfig tcpIpConfig = classpathXmlConfig.getNetworkConfig().getJoin().getTcpIpConfig();
        tcpIpConfig.setEnabled(true);
        tcpIpConfig.addMember(IpUtil.getHostIp());
        //发现IP
        InterfacesConfig interfaces = classpathXmlConfig.getNetworkConfig().getInterfaces();

        interfaces.setEnabled(true);
        interfaces.addInterface("10.0.*.*");

        if (!Objects.isNull(mapConfigs)) {
            for (MapConfig mapConfig : mapConfigs) {
                classpathXmlConfig.addMapConfig(mapConfig);
            }
        }

        classpathXmlConfig.addListenerConfig(
                new ListenerConfig("com.config.ClusterMembershipListener"));
        return classpathXmlConfig;
    }
}

