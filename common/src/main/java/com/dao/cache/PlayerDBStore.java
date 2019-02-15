package com.dao.cache;

import com.dao.PlayerRepository;
import com.entry.PlayerEntry;
import com.google.common.collect.Maps;
import com.hazelcast.core.MapStoreAdapter;
import com.util.SpringUtils;
import org.springframework.stereotype.Repository;

import javax.cache.integration.CacheLoaderException;
import java.util.Collection;
import java.util.Map;

@Repository
public class PlayerDBStore extends MapStoreAdapter<Long, PlayerEntry> {

    @Override
    public PlayerEntry load(Long key) throws CacheLoaderException {
        PlayerRepository playerRepository = SpringUtils.getBean(PlayerRepository.class);
        return playerRepository.findById(key).orElse(null);
    }

    @Override
    public void delete(Long key) {
        PlayerRepository playerRepository = SpringUtils.getBean(PlayerRepository.class);
        playerRepository.deleteById(key);
    }

    @Override
    public void store(Long key, PlayerEntry playerEntry) {
        PlayerRepository playerRepository = SpringUtils.getBean(PlayerRepository.class);
        playerRepository.save(playerEntry);
    }

    @Override
    public void storeAll(Map<Long, PlayerEntry> map) {
        PlayerRepository playerRepository = SpringUtils.getBean(PlayerRepository.class);
        playerRepository.saveAll(map.values());
    }

    @Override
    public void deleteAll(Collection<Long> keys) {
        PlayerRepository playerRepository = SpringUtils.getBean(PlayerRepository.class);
        keys.forEach(playerRepository::deleteById);
    }

    @Override
    public Map<Long, PlayerEntry> loadAll(Collection<Long> keys) {
        PlayerRepository playerRepository = SpringUtils.getBean(PlayerRepository.class);
        Iterable<PlayerEntry> allById = playerRepository.findAllById(keys);
        Map<Long, PlayerEntry> map = Maps.newHashMap();
        allById.forEach(playerEntry -> map.put(playerEntry.getId(), playerEntry));
        return map;
    }

}