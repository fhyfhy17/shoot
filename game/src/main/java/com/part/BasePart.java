package com.part;

import com.pojo.Player;
import com.util.objectPool.StringUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BasePart {

    protected Player player;

    public abstract void onLoad();

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public String getCacheName() {
        return StringUtil.cutByRemovePostfix(getName(), "Part") + "EntryCache";
    }

    public void onDaily() {

    }

    public void onSecond() {

    }

    public abstract void onLogin();

    public void onLogout() {

    }

    public void onActivityOpen() {

    }

    public void onActivityClose() {

    }

    public void onActivityReset() {

    }

    public void onLevelUp() {

    }
}
