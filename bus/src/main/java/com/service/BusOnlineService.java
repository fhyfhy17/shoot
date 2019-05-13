package com.service;

import com.controller.UidContext;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.pojo.OnlineContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@Data
public class BusOnlineService {
    //TODO bus的在线信息，要game发过来心跳控制,不用每条都发，可以把整个Online的id全发过来，因为Game层已经有
    // 心跳控制了那么可以每次是该服的，不存在的就删除。暂时先这样，这里应该容易出现BUG，以后观察

   
    private Multimap<String,OnlineContext> onlineMapByGame = ArrayListMultimap.create();
    
    public OnlineContext getOnlineContext(UidContext uidContext,long uid)
    {
        Collection<OnlineContext> onlineContexts=onlineMapByGame.get(uidContext.getFrom());
        if(!CollectionUtils.isEmpty(onlineContexts))
        {
            return onlineContexts.stream().filter(x->x.getUid()==uid).findAny().orElse(null);
        }
        return null;
    }

    public void putOnlineContext(OnlineContext onlineContext) {
        onlineMapByGame.put(onlineContext.getGame(),onlineContext);
    }

    public void delOnlineContext(UidContext uidContext,long uid) {
        OnlineContext onlineContext = getOnlineContext(uidContext,uid);

        if(onlineContext != null){
            String game = onlineContext.getGame();
            onlineMapByGame.get(game).remove(onlineContext);
        }
    }

    public void onHeart(String from , List<Long> uids){
        Collection<OnlineContext> onlineContexts = onlineMapByGame.get(from);


    }

}
