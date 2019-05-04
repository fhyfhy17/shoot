package com.entry.po;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemInfo implements Cloneable {

    /**
     * 物品ID
     */
    public long id;

    /**
     * 物品数目
     */
    public int num;


    public ItemInfo() {
    }

    public ItemInfo(long id, int num) {
        this.num = num;
        this.id = id;
    }

}
