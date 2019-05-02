package com.part;

import com.entry.PlayerEntry;
import lombok.Getter;
import lombok.Setter;
import org.ehcache.Cache;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class BagPart extends BasePart {

    private PlayerEntry playerEntry;

    @Override
    public void onLoad() {
        player.bagPart = this;
        Cache<Long, PlayerEntry> cache = cacheManager.getCache(getCacheName(), Long.class, PlayerEntry.class);
        playerEntry = cache.get(player.getPlayerId());
    }


    @Override
    public void onLogin() {

    }
}
