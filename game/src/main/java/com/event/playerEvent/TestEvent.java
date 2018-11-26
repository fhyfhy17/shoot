package com.event.playerEvent;

import com.event.PlayerEventData;
import com.pojo.Player;
import lombok.Data;

@Data
public class TestEvent extends PlayerEventData {
    private String testWord;

    public TestEvent(Player player,String testWord) {
        super(player);
        this.testWord = testWord;
    }
}