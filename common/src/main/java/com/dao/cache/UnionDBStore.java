package com.dao.cache;

import com.dao.UnionRepository;
import com.entry.UnionEntry;
import com.google.common.collect.Maps;
import com.hazelcast.core.MapStoreAdapter;
import com.util.SpringUtils;
import org.springframework.stereotype.Repository;

import javax.cache.integration.CacheLoaderException;
import java.util.Collection;
import java.util.Map;

@Repository
public class UnionDBStore extends MapStoreAdapter<Long, UnionEntry> {

    @Override
    public UnionEntry load(Long key) throws CacheLoaderException {
        UnionRepository unionRepository = SpringUtils.getBean(UnionRepository.class);
        return unionRepository.findById(key).orElse(null);
    }


    @Override
    public void delete(Long key) {
        UnionRepository unionRepository = SpringUtils.getBean(UnionRepository.class);
        unionRepository.deleteById(key);
    }

    @Override
    public void store(Long key, UnionEntry unionEntry) {
        UnionRepository unionRepository = SpringUtils.getBean(UnionRepository.class);
        unionRepository.save(unionEntry);
    }

    @Override
    public void storeAll(Map<Long, UnionEntry> map) {
        UnionRepository unionRepository = SpringUtils.getBean(UnionRepository.class);
        unionRepository.saveAll(map.values());
    }

    @Override
    public void deleteAll(Collection<Long> keys) {
        UnionRepository unionRepository = SpringUtils.getBean(UnionRepository.class);
        keys.forEach(unionRepository::deleteById);
    }

    @Override
    public Map<Long, UnionEntry> loadAll(Collection<Long> keys) {
        UnionRepository unionRepository = SpringUtils.getBean(UnionRepository.class);
        Iterable<UnionEntry> allById = unionRepository.findAllById(keys);
        Map<Long, UnionEntry> map = Maps.newHashMap();
        allById.forEach(unionEntry -> map.put(unionEntry.getId(), unionEntry));
        return map;
    }

}