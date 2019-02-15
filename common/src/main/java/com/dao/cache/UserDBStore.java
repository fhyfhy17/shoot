package com.dao.cache;

import com.dao.UserRepository;
import com.entry.UserEntry;
import com.google.common.collect.Maps;
import com.hazelcast.core.MapStoreAdapter;
import com.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.cache.integration.CacheLoaderException;
import java.util.Collection;
import java.util.Map;

@Repository
public class UserDBStore extends MapStoreAdapter<Long, UserEntry> {

    @Autowired
    private UserRepository userRepo;
    private static Logger logger = LoggerFactory.getLogger(UserDBStore.class);

    @Override
    public UserEntry load(Long key) throws CacheLoaderException {

        logger.info(String.valueOf(userRepo));
        return userRepo.findById(key).orElse(null);
    }


    @Override
    public void delete(Long key) {
        UserRepository userRepository = SpringUtils.getBean(UserRepository.class);
        userRepository.deleteById(key);
    }

    @Override
    public void store(Long key, UserEntry userEntry) {
        UserRepository userRepository = SpringUtils.getBean(UserRepository.class);
        userRepository.save(userEntry);
    }

    @Override
    public void storeAll(Map<Long, UserEntry> map) {
        UserRepository userRepository = SpringUtils.getBean(UserRepository.class);
        userRepository.saveAll(map.values());
    }

    @Override
    public void deleteAll(Collection<Long> keys) {
        UserRepository userRepository = SpringUtils.getBean(UserRepository.class);
        keys.forEach(userRepository::deleteById);
    }

    @Override
    public Map<Long, UserEntry> loadAll(Collection<Long> keys) {
        UserRepository userRepository = SpringUtils.getBean(UserRepository.class);
        Iterable<UserEntry> allById = userRepository.findAllById(keys);
        Map<Long, UserEntry> map = Maps.newHashMap();
        allById.forEach(userEntry -> map.put(userEntry.getId(), userEntry));
        return map;
    }

}