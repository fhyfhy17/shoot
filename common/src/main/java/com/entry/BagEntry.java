package com.entry;

import com.annotation.SeqClassName;
import com.entry.po.ItemPo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document
@Getter
@Setter
@SeqClassName(name = "seq.BagEntry")
@ToString
public class BagEntry extends BaseEntry {


    public Map<Integer, ItemPo> indexMap = new HashMap<>();
    //TODO 每一个entry应该对应自己的数据。这个不应该放在这，每个entry 用接口都是操作自己的数据
    public Map<Integer,Long> currencyMap = new HashMap<>();

    public BagEntry(long id) {
        super(id);
    }

}
