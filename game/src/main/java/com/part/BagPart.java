package com.part;

import com.abs.CellBagAbs;
import com.abs.impl.CommonCellBag;
import com.entry.BagEntry;
import com.template.TemplateManager;
import lombok.Getter;
import lombok.Setter;
import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class BagPart extends BasePart {

    private BagEntry bagEntry;
    @Autowired
    private TemplateManager templateManager;

    private CellBagAbs bag;

    @Override
    public void onLoad() {
        player.bagPart = this;
        Cache<Long, BagEntry> cache = cacheManager.getCache(getCacheName(), Long.class, BagEntry.class);
        bagEntry = cache.get(player.getPlayerId());
        bag= new CommonCellBag();
        bag.init(bagEntry.indexMap, templateManager);

    }


    @Override
    public void onLogin() {

    }
}
