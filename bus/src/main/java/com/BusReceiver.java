package com;

import com.enums.TypeEnum;
import com.handler.BusMessageHandler;
import com.handler.MessageGroup;
import com.handler.MessageThreadHandler;
import com.pojo.Message;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class BusReceiver extends BaseReceiver {
    private MessageGroup m;


    @PostConstruct
    public void startup() {
        m = new MessageGroup(TypeEnum.GroupEnum.BUS_GROUP.name()) {
            @Override
            public MessageThreadHandler getMessageThreadHandler() {
                return new BusMessageHandler();
            }
        };
        m.startup();

    }

    @Override
    public void onReceive(Message message) {
        m.messageReceived(message);
    }

}
