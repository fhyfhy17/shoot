package com.service;

import com.controller.UidContext;
import com.pojo.OnlineContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@Data
public class BusOnlineService
{
	//TODO bus的在线信息，要game发过来心跳控制,不用每条都发，可以把整个Online的id全发过来，因为Game层已经有
	// 心跳控制了那么可以每次是该服的，不存在的就删除，每20秒同步一次，一分钟没消息全干掉。暂时先这样，这里应该容易出现BUG，以后观察
	
	
	private ConcurrentHashMap<String,List<OnlineContext>> onlineMapByGame=new ConcurrentHashMap<>();
	
	public Optional<OnlineContext> getOnlineContext(UidContext uidContext,long uid)
	{
		List<OnlineContext> onlineContexts=onlineMapByGame.get(uidContext.getFrom());
		
		if(!CollectionUtils.isEmpty(onlineContexts))
		{
			return onlineContexts.stream().filter(x->x.getUid()==uid).findAny();
		}
		return Optional.empty();
	}
	
	public void putOnlineContext(OnlineContext onlineContext)
	{
		onlineMapByGame.computeIfAbsent(onlineContext.getGame(),x->new ArrayList<>()).add(onlineContext);
	}
	
	public void delOnlineContext(UidContext uidContext,long uid)
	{
		getOnlineContext(uidContext,uid).ifPresent(x->onlineMapByGame.get(x.getGame()).remove(x));
	}
	
	public void onHeart(String from,List<Long> uids)
	{
		List<OnlineContext> onlineContexts=onlineMapByGame.get(from);
		if(!CollectionUtils.isEmpty(onlineContexts))
		{
			onlineMapByGame.get(from).removeIf(next->!uids.contains(next.getUid()));
		}
		
	}
	
}
