package com.config;

import com.hazelcast.nio.serialization.ByteArraySerializer;
import com.util.ProtostuffUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public class PstStreamSerializer<U> implements ByteArraySerializer<U> {

    @Getter
    @Setter
    private final Class<U> type;
    private final int typeId;

    public PstStreamSerializer(Class<U> type, int typeId) {
        this.type = type;
        this.typeId = typeId;
    }


    @Override
    public int getTypeId() {
        return typeId;
    }

    @Override
    public void destroy() {

    }

    @Override
    public byte[] write(U object) throws IOException {
        return ProtostuffUtil.serializeObject(object, type);
    }

    @Override
    public U read(byte[] buffer) throws IOException {
        return ProtostuffUtil.deserializeObject(buffer, type);
    }
}
