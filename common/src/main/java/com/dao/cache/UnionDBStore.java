package com.dao.cache;

import com.dao.UnionRepository;
import com.entry.UnionEntry;
import com.util.SpringUtils;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.springframework.stereotype.Repository;

import javax.cache.integration.CacheLoaderException;

@Repository
public class UnionDBStore implements CacheLoaderWriter<Long, UnionEntry> {

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
    public void write(Long key, UnionEntry unionEntry) {
        UnionRepository unionRepository = SpringUtils.getBean(UnionRepository.class);
        unionRepository.save(unionEntry);
    }


}