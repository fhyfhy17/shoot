package com;

import com.enums.TypeEnum;
import com.handler.BusMessageHandler;
import com.handler.MessageGroup;
import com.handler.MessageThreadHandler;
import com.pojo.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
@Slf4j
public class BusReceiver extends BaseReceiver {
    private MessageGroup m;


    @PostConstruct
    public void startup() {
        m = new MessageGroup(TypeEnum.GroupEnum.BUS_GROUP.name()) {
            @Override
            public MessageThreadHandler getMessageThreadHandler() {
                return new BusMessageHandler();
            }
            @Override
            public void messageReceived(Message msg) {
        
                // 分配执行器执行
                int index = msg.getFrom().hashCode() % handlerCount;
                
                MessageThreadHandler handler = hanlderList.get(index);
                handler.messageReceived(msg);
            }
            
    
        };
        m.startup();

    }

    @Override
    public void onReceive(Message message) {
        m.messageReceived(message);
    }

}
