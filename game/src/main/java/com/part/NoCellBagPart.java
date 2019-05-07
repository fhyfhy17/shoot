package com.part;

import com.abs.NoCellBagAbs;
import com.abs.impl.CommonNoCellBag;
import com.entry.NoCellBagEntry;
import com.template.TemplateManager;
import lombok.Getter;
import lombok.Setter;
import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class NoCellBagPart extends BasePart {

    private NoCellBagEntry noCellBagEntry;
    @Autowired
    private TemplateManager templateManager;

    private NoCellBagAbs noCellBag;

    @Override
    public void onLoad() {
        player.noCellBagPart = this;
        Cache<Long, NoCellBagEntry> cache = cacheManager.getCache(getCacheName(), Long.class, NoCellBagEntry.class);
        noCellBagEntry = cache.get(player.getPlayerId());
        noCellBag= new CommonNoCellBag();
        noCellBag.init(noCellBagEntry.map, templateManager,player);

    }


    @Override
    public void onLogin() {

    }
}
