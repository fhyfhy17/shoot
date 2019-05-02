package com.pojo;

import com.part.BagPart;
import com.part.BasePart;
import com.part.PlayerPart;
import lombok.Data;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Data
public class Player {

    private long playerId;
    private long uid;

    public PlayerPart playerPart;
    public BagPart bagPart;

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private List<BasePart> parts;

    public Player() {

    }

    public void initParts() {
        parts.forEach(x -> {
            x.setPlayer(this);
            x.onLoad();

        });
    }


    public void onDaily() {
        parts.forEach(BasePart::onDaily);
    }

    public void onLogin() {
        parts.forEach(BasePart::onLogin);
    }

    public void onLogout() {
        parts.forEach(BasePart::onLogout);
    }

    public void onActivityOpen() {
        parts.forEach(BasePart::onActivityOpen);
    }

    public void onActivityClose() {
        parts.forEach(BasePart::onActivityClose);
    }

    public void onActivityReset() {
        parts.forEach(BasePart::onActivityReset);
    }

    public void onLevelUp() {
        parts.forEach(BasePart::onLevelUp);
    }

    public void onSecond() {
        parts.forEach(BasePart::onSecond);
    }
}
