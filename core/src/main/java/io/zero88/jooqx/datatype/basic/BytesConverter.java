package io.zero88.jooqx.datatype.basic;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.buffer.Buffer;
import io.zero88.jooqx.datatype.JooqxConverter;

public final class BytesConverter implements JooqxConverter<Buffer, byte[]> {

    @Override
    public byte[] from(Buffer vertxObject) { return vertxObject == null ? null : vertxObject.getBytes(); }

    @Override
    public Buffer to(byte[] jooqObject) { return jooqObject == null ? null : Buffer.buffer(jooqObject); }

    @Override
    public @NotNull Class<Buffer> fromType() { return Buffer.class; }

    @Override
    public @NotNull Class<byte[]> toType() { return byte[].class; }

}
