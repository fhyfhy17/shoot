package com.entry;

import com.annotation.SeqClassName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@SeqClassName(name = "seq.BagEntry")
@ToString
public class BagEntry extends BaseEntry {


    public BagEntry(long id) {
        super(id);
    }

}
