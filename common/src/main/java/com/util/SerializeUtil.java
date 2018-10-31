package com.util;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.net.msg.LOGIN_MSG;
import com.pojo.Message;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.*;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;

import static java.lang.String.format;

@Slf4j
public class SerializeUtil {
    private static Kryo k = new Kryo();

    static {
        k.register(Message.class);
        k.setReferences(true);
        k.setInstantiatorStrategy(new StdInstantiatorStrategy());
        k.setDefaultSerializer(DefaultSerializers.ByteSerializer.class);

    }

    //kyro 1  proto 2 fast 3 colfer 4
    public static final int type = 4;


    public static Message stm(byte[] s) {
        if (type == 1) {
            return kryoStm(s);
        } else if (type == 2) {
            return protoStm(s);
        } else if (type == 3) {
            return fastStm(s);
        } else {
            return colferStm(s);
        }
//        return null;
    }

    public static byte[] mts(Message m) {
        if (type == 1) {
            return kryoMts(m);
        } else if (type == 2) {
            return protoMts(m);
        } else if (type == 3) {
            return fastMts(m);
        } else {
            return colferMts(m);
        }

//        return null;
    }

    private static Message colferStm(byte[] s) {
        Message message = new Message();

        ColferMessage m = new ColferMessage();
        m.unmarshal(s, 0);
        message.setId(m.getId());
        message.setUid(m.getUid());
        message.setFrom(m.getFrom());
        message.setData(m.getData());
        return message;
    }

    private static byte[] colferMts(Message message) {
        ColferMessage colfer = new ColferMessage();
        colfer.setId(message.getId());
        colfer.setUid(message.getUid());
        colfer.setFrom(message.getFrom());
        colfer.setData(message.getData());
        byte[] temp = new byte[2048];
        int length = colfer.marshal(temp, 0);
        byte[] body = new byte[length];
        System.arraycopy(temp, 0, body, 0, length);
        return body;
    }

    private static Message kryoStm(byte[] s) {
        Message m = null;
        try (
                ByteArrayInputStream bais = new ByteArrayInputStream(s);
                Input input = new Input(bais)

        ) {
            m = k.readObject(input, Message.class);
        } catch (IOException e) {
            log.error("", e);
        }

        return m;
    }

    private static byte[] kryoMts(Message m) {
        Message m2 = new Message();
        m2.setId(m.getId());
        m2.setUid(m.getUid());
        m2.setFrom(m.getFrom());
        m2.setData(m.getData());

        byte[] bys = null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            @Cleanup Output output = new Output(baos);
            k.writeObject(output, m2);
            output.flush();
            bys = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return bys;
    }


    private static byte[] fastMts(Message m) {
        String s = JSON.toJSONString(m);
        return s.getBytes();
    }

    private static Message fastStm(byte[] s) {
        return JSON.parseObject(s, Message.class);
    }

    static LOGIN_MSG.MyMessage.Builder builder = LOGIN_MSG.MyMessage.newBuilder();

    private static byte[] protoMts(Message m) {
        builder.setUid(m.getUid());
        builder.setId(m.getId());
        builder.setData(ByteString.copyFrom(m.getData()));
        builder.setFrom(m.getFrom());
        byte[] bytes = builder.build().toByteArray();
        return bytes;
    }

    private static Message protoStm(byte[] s) {
        Message m2 = new Message();
        try {
            LOGIN_MSG.MyMessage m = LOGIN_MSG.MyMessage.parseFrom(s);

            m2.setId(m.getId());
            m2.setUid(m.getUid());
            m2.setData(m.getData().toByteArray());
            m2.setFrom(m.getFrom());
            return m2;

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return m2;
    }

    public static void main(String[] args) throws IOException {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 1; i++) {
            sb.append("这是测试");
        }
        Message m = new Message();
        m.setId(1);
        m.setFrom("a");
        m.setUid(sb.toString());
        byte[] bbb = new byte[8];
        m.setData(bbb);
        int count = 10000000;

        byte[] kryoString = kryoMts(m);
        byte[] colferString = colferMts(m);
        long start2 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            kryoMts(m);
        }
        long end2 = System.currentTimeMillis();
        log.info("kryo msg to String = {}", end2 - start2);

        long start5 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            fastMts(m);
        }
        long end5 = System.currentTimeMillis();

        log.info("fast msg to String = {}", end5 - start5);


        long start15 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            colferMts(m);
        }
        long end15 = System.currentTimeMillis();

        log.info("colfer msg to String = {}", end15 - start15);


        long start11 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            protoMts(m);
        }
        long end11 = System.currentTimeMillis();

        log.info("proto msg to String = {}", (end11 - start11));
        log.info("");
        long start4 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            kryoStm(kryoString);
        }
        long end4 = System.currentTimeMillis();

        log.info("kryo String to msg  = {}", (end4 - start4));


        long start14 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            Message m1 = colferStm(colferString);
        }
        long end14 = System.currentTimeMillis();

        log.info("colfer String to msg  = {}", (end14 - start14));


        byte[] fastString = fastMts(m);
        long start6 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            fastStm(fastString);
        }
        long end6 = System.currentTimeMillis();
        log.info("fast String to msg = {}", (end6 - start6));


        byte[] protoString = protoMts(m);
        long start12 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            protoStm(protoString);
        }
        long end12 = System.currentTimeMillis();
        log.info("proto String to msg = {}", (end12 - start12));
        log.info("");
        log.info("kryo String大小 = {}", kryoString.length);
        log.info("colfer String大小 = {}", colferString.length);
        log.info("fast String大小 = {}", fastString.length);
        log.info("prorto String大小 = {}", protoString.length);

    }
}

@javax.annotation.Generated(value = "colf(1)", comments = "Colfer from schema file Message.colf")
class ColferMessage implements Serializable {

    /**
     * The upper limit for serial byte sizes.
     */
    public static int colferSizeMax = 16 * 1024 * 1024;


    public int id;

    public String uid;

    public byte[] data;

    public String from;


    /**
     * Default constructor
     */
    public ColferMessage() {
        init();
    }

    private static final byte[] _zeroBytes = new byte[0];

    /**
     * Colfer zero values.
     */
    private void init() {
        uid = "";
        data = _zeroBytes;
        from = "";
    }

    /**
     * {@link #reset(InputStream) Reusable} deserialization of Colfer streams.
     */
    public static class Unmarshaller {

        /**
         * The data source.
         */
        protected InputStream in;

        /**
         * The read buffer.
         */
        public byte[] buf;

        /**
         * The {@link #buf buffer}'s data start index, inclusive.
         */
        protected int offset;

        /**
         * The {@link #buf buffer}'s data end index, exclusive.
         */
        protected int i;


        /**
         * @param in  the data source or {@code null}.
         * @param buf the initial buffer or {@code null}.
         */
        public Unmarshaller(InputStream in, byte[] buf) {
            // TODO: better size estimation
            if (buf == null || buf.length == 0)
                buf = new byte[Math.min(ColferMessage.colferSizeMax, 2048)];
            this.buf = buf;
            reset(in);
        }

        /**
         * Reuses the marshaller.
         *
         * @param in the data source or {@code null}.
         * @throws IllegalStateException on pending data.
         */
        public void reset(InputStream in) {
            if (this.i != this.offset) throw new IllegalStateException("colfer: pending data");
            this.in = in;
            this.offset = 0;
            this.i = 0;
        }

        /**
         * Deserializes the following object.
         *
         * @return the result or {@code null} when EOF.
         * @throws IOException            from the input stream.
         * @throws SecurityException      on an upper limit breach defined by {@link #colferSizeMax}.
         * @throws InputMismatchException when the data does not match this object's schema.
         */
        public ColferMessage next() throws IOException {
            if (in == null) return null;

            while (true) {
                if (this.i > this.offset) {
                    try {
                        ColferMessage o = new ColferMessage();
                        this.offset = o.unmarshal(this.buf, this.offset, this.i);
                        return o;
                    } catch (BufferUnderflowException e) {
                    }
                }
                // not enough data

                if (this.i <= this.offset) {
                    this.offset = 0;
                    this.i = 0;
                } else if (i == buf.length) {
                    byte[] src = this.buf;
                    // TODO: better size estimation
                    if (offset == 0) this.buf = new byte[Math.min(ColferMessage.colferSizeMax, this.buf.length * 4)];
                    System.arraycopy(src, this.offset, this.buf, 0, this.i - this.offset);
                    this.i -= this.offset;
                    this.offset = 0;
                }
                assert this.i < this.buf.length;

                int n = in.read(buf, i, buf.length - i);
                if (n < 0) {
                    if (this.i > this.offset)
                        throw new InputMismatchException("colfer: pending data with EOF");
                    return null;
                }
                assert n > 0;
                i += n;
            }
        }

    }


    /**
     * Serializes the object.
     *
     * @param out the data destination.
     * @param buf the initial buffer or {@code null}.
     * @return the final buffer. When the serial fits into {@code buf} then the return is {@code buf}.
     * Otherwise the return is a new buffer, large enough to hold the whole serial.
     * @throws IOException           from {@code out}.
     * @throws IllegalStateException on an upper limit breach defined by {@link #colferSizeMax}.
     */
    public byte[] marshal(OutputStream out, byte[] buf) throws IOException {
        // TODO: better size estimation
        if (buf == null || buf.length == 0)
            buf = new byte[Math.min(ColferMessage.colferSizeMax, 2048)];

        while (true) {
            int i;
            try {
                i = marshal(buf, 0);
            } catch (BufferOverflowException e) {
                buf = new byte[Math.min(ColferMessage.colferSizeMax, buf.length * 4)];
                continue;
            }

            out.write(buf, 0, i);
            return buf;
        }
    }

    /**
     * Serializes the object.
     *
     * @param buf    the data destination.
     * @param offset the initial index for {@code buf}, inclusive.
     * @return the final index for {@code buf}, exclusive.
     * @throws BufferOverflowException when {@code buf} is too small.
     * @throws IllegalStateException   on an upper limit breach defined by {@link #colferSizeMax}.
     */
    public int marshal(byte[] buf, int offset) {
        int i = offset;

        try {
            if (this.id != 0) {
                int x = this.id;
                if (x < 0) {
                    x = -x;
                    buf[i++] = (byte) (0 | 0x80);
                } else
                    buf[i++] = (byte) 0;
                while ((x & ~0x7f) != 0) {
                    buf[i++] = (byte) (x | 0x80);
                    x >>>= 7;
                }
                buf[i++] = (byte) x;
            }

            if (!this.uid.isEmpty()) {
                buf[i++] = (byte) 1;
                int start = ++i;

                String s = this.uid;
                for (int sIndex = 0, sLength = s.length(); sIndex < sLength; sIndex++) {
                    char c = s.charAt(sIndex);
                    if (c < '\u0080') {
                        buf[i++] = (byte) c;
                    } else if (c < '\u0800') {
                        buf[i++] = (byte) (192 | c >>> 6);
                        buf[i++] = (byte) (128 | c & 63);
                    } else if (c < '\ud800' || c > '\udfff') {
                        buf[i++] = (byte) (224 | c >>> 12);
                        buf[i++] = (byte) (128 | c >>> 6 & 63);
                        buf[i++] = (byte) (128 | c & 63);
                    } else {
                        int cp = 0;
                        if (++sIndex < sLength) cp = Character.toCodePoint(c, s.charAt(sIndex));
                        if ((cp >= 1 << 16) && (cp < 1 << 21)) {
                            buf[i++] = (byte) (240 | cp >>> 18);
                            buf[i++] = (byte) (128 | cp >>> 12 & 63);
                            buf[i++] = (byte) (128 | cp >>> 6 & 63);
                            buf[i++] = (byte) (128 | cp & 63);
                        } else
                            buf[i++] = (byte) '?';
                    }
                }
                int size = i - start;
                if (size > ColferMessage.colferSizeMax)
                    throw new IllegalStateException(format("colfer: demo.colferMessage.uid size %d exceeds %d UTF-8 bytes", size, ColferMessage.colferSizeMax));

                int ii = start - 1;
                if (size > 0x7f) {
                    i++;
                    for (int x = size; x >= 1 << 14; x >>>= 7) i++;
                    System.arraycopy(buf, start, buf, i - size, size);

                    do {
                        buf[ii++] = (byte) (size | 0x80);
                        size >>>= 7;
                    } while (size > 0x7f);
                }
                buf[ii] = (byte) size;
            }

            if (this.data.length != 0) {
                buf[i++] = (byte) 2;

                int size = this.data.length;
                if (size > ColferMessage.colferSizeMax)
                    throw new IllegalStateException(format("colfer: demo.colferMessage.data size %d exceeds %d bytes", size, ColferMessage.colferSizeMax));

                int x = size;
                while (x > 0x7f) {
                    buf[i++] = (byte) (x | 0x80);
                    x >>>= 7;
                }
                buf[i++] = (byte) x;

                int start = i;
                i += size;
                System.arraycopy(this.data, 0, buf, start, size);
            }

            if (!this.from.isEmpty()) {
                buf[i++] = (byte) 3;
                int start = ++i;

                String s = this.from;
                for (int sIndex = 0, sLength = s.length(); sIndex < sLength; sIndex++) {
                    char c = s.charAt(sIndex);
                    if (c < '\u0080') {
                        buf[i++] = (byte) c;
                    } else if (c < '\u0800') {
                        buf[i++] = (byte) (192 | c >>> 6);
                        buf[i++] = (byte) (128 | c & 63);
                    } else if (c < '\ud800' || c > '\udfff') {
                        buf[i++] = (byte) (224 | c >>> 12);
                        buf[i++] = (byte) (128 | c >>> 6 & 63);
                        buf[i++] = (byte) (128 | c & 63);
                    } else {
                        int cp = 0;
                        if (++sIndex < sLength) cp = Character.toCodePoint(c, s.charAt(sIndex));
                        if ((cp >= 1 << 16) && (cp < 1 << 21)) {
                            buf[i++] = (byte) (240 | cp >>> 18);
                            buf[i++] = (byte) (128 | cp >>> 12 & 63);
                            buf[i++] = (byte) (128 | cp >>> 6 & 63);
                            buf[i++] = (byte) (128 | cp & 63);
                        } else
                            buf[i++] = (byte) '?';
                    }
                }
                int size = i - start;
                if (size > ColferMessage.colferSizeMax)
                    throw new IllegalStateException(format("colfer: demo.colferMessage.from size %d exceeds %d UTF-8 bytes", size, ColferMessage.colferSizeMax));

                int ii = start - 1;
                if (size > 0x7f) {
                    i++;
                    for (int x = size; x >= 1 << 14; x >>>= 7) i++;
                    System.arraycopy(buf, start, buf, i - size, size);

                    do {
                        buf[ii++] = (byte) (size | 0x80);
                        size >>>= 7;
                    } while (size > 0x7f);
                }
                buf[ii] = (byte) size;
            }

            buf[i++] = (byte) 0x7f;
            return i;
        } catch (ArrayIndexOutOfBoundsException e) {
            if (i - offset > ColferMessage.colferSizeMax)
                throw new IllegalStateException(format("colfer: demo.colferMessage exceeds %d bytes", ColferMessage.colferSizeMax));
            if (i > buf.length) throw new BufferOverflowException();
            throw e;
        }
    }

    /**
     * Deserializes the object.
     *
     * @param buf    the data source.
     * @param offset the initial index for {@code buf}, inclusive.
     * @return the final index for {@code buf}, exclusive.
     * @throws BufferUnderflowException when {@code buf} is incomplete. (EOF)
     * @throws SecurityException        on an upper limit breach defined by {@link #colferSizeMax}.
     * @throws InputMismatchException   when the data does not match this object's schema.
     */
    public int unmarshal(byte[] buf, int offset) {
        return unmarshal(buf, offset, buf.length);
    }

    /**
     * Deserializes the object.
     *
     * @param buf    the data source.
     * @param offset the initial index for {@code buf}, inclusive.
     * @param end    the index limit for {@code buf}, exclusive.
     * @return the final index for {@code buf}, exclusive.
     * @throws BufferUnderflowException when {@code buf} is incomplete. (EOF)
     * @throws SecurityException        on an upper limit breach defined by {@link #colferSizeMax}.
     * @throws InputMismatchException   when the data does not match this object's schema.
     */
    public int unmarshal(byte[] buf, int offset, int end) {
        if (end > buf.length) end = buf.length;
        int i = offset;

        try {
            byte header = buf[i++];

            if (header == (byte) 0) {
                int x = 0;
                for (int shift = 0; true; shift += 7) {
                    byte b = buf[i++];
                    x |= (b & 0x7f) << shift;
                    if (shift == 28 || b >= 0) break;
                }
                this.id = x;
                header = buf[i++];
            } else if (header == (byte) (0 | 0x80)) {
                int x = 0;
                for (int shift = 0; true; shift += 7) {
                    byte b = buf[i++];
                    x |= (b & 0x7f) << shift;
                    if (shift == 28 || b >= 0) break;
                }
                this.id = -x;
                header = buf[i++];
            }

            if (header == (byte) 1) {
                int size = 0;
                for (int shift = 0; true; shift += 7) {
                    byte b = buf[i++];
                    size |= (b & 0x7f) << shift;
                    if (shift == 28 || b >= 0) break;
                }
                if (size < 0 || size > ColferMessage.colferSizeMax)
                    throw new SecurityException(format("colfer: demo.colferMessage.uid size %d exceeds %d UTF-8 bytes", size, ColferMessage.colferSizeMax));

                int start = i;
                i += size;
                this.uid = new String(buf, start, size, StandardCharsets.UTF_8);
                header = buf[i++];
            }

            if (header == (byte) 2) {
                int size = 0;
                for (int shift = 0; true; shift += 7) {
                    byte b = buf[i++];
                    size |= (b & 0x7f) << shift;
                    if (shift == 28 || b >= 0) break;
                }
                if (size < 0 || size > ColferMessage.colferSizeMax)
                    throw new SecurityException(format("colfer: demo.colferMessage.data size %d exceeds %d bytes", size, ColferMessage.colferSizeMax));

                this.data = new byte[size];
                int start = i;
                i += size;
                System.arraycopy(buf, start, this.data, 0, size);

                header = buf[i++];
            }

            if (header == (byte) 3) {
                int size = 0;
                for (int shift = 0; true; shift += 7) {
                    byte b = buf[i++];
                    size |= (b & 0x7f) << shift;
                    if (shift == 28 || b >= 0) break;
                }
                if (size < 0 || size > ColferMessage.colferSizeMax)
                    throw new SecurityException(format("colfer: demo.colferMessage.from size %d exceeds %d UTF-8 bytes", size, ColferMessage.colferSizeMax));

                int start = i;
                i += size;
                this.from = new String(buf, start, size, StandardCharsets.UTF_8);
                header = buf[i++];
            }

            if (header != (byte) 0x7f)
                throw new InputMismatchException(format("colfer: unknown header at byte %d", i - 1));
        } finally {
            if (i > end && end - offset < ColferMessage.colferSizeMax) throw new BufferUnderflowException();
            if (i < 0 || i - offset > ColferMessage.colferSizeMax)
                throw new SecurityException(format("colfer: demo.colferMessage exceeds %d bytes", ColferMessage.colferSizeMax));
            if (i > end) throw new BufferUnderflowException();
        }

        return i;
    }

    // {@link Serializable} version number.
    private static final long serialVersionUID = 4L;

    // {@link Serializable} Colfer extension.
    private void writeObject(ObjectOutputStream out) throws IOException {
        // TODO: better size estimation
        byte[] buf = new byte[1024];
        int n;
        while (true) try {
            n = marshal(buf, 0);
            break;
        } catch (BufferUnderflowException e) {
            buf = new byte[4 * buf.length];
        }

        out.writeInt(n);
        out.write(buf, 0, n);
    }

    // {@link Serializable} Colfer extension.
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        init();

        int n = in.readInt();
        byte[] buf = new byte[n];
        in.readFully(buf);
        unmarshal(buf, 0);
    }

    // {@link Serializable} Colfer extension.
    private void readObjectNoData() throws ObjectStreamException {
        init();
    }

    /**
     * Gets demo.colferMessage.id.
     *
     * @return the value.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets demo.colferMessage.id.
     *
     * @param value the replacement.
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Sets demo.colferMessage.id.
     *
     * @param value the replacement.
     * @return {link this}.
     */
    public ColferMessage withId(int value) {
        this.id = value;
        return this;
    }

    /**
     * Gets demo.colferMessage.uid.
     *
     * @return the value.
     */
    public String getUid() {
        return this.uid;
    }

    /**
     * Sets demo.colferMessage.uid.
     *
     * @param value the replacement.
     */
    public void setUid(String value) {
        this.uid = value;
    }

    /**
     * Sets demo.colferMessage.uid.
     *
     * @param value the replacement.
     * @return {link this}.
     */
    public ColferMessage withUid(String value) {
        this.uid = value;
        return this;
    }

    /**
     * Gets demo.colferMessage.data.
     *
     * @return the value.
     */
    public byte[] getData() {
        return this.data;
    }

    /**
     * Sets demo.colferMessage.data.
     *
     * @param value the replacement.
     */
    public void setData(byte[] value) {
        this.data = value;
    }

    /**
     * Sets demo.colferMessage.data.
     *
     * @param value the replacement.
     * @return {link this}.
     */
    public ColferMessage withData(byte[] value) {
        this.data = value;
        return this;
    }

    /**
     * Gets demo.colferMessage.from.
     *
     * @return the value.
     */
    public String getFrom() {
        return this.from;
    }

    /**
     * Sets demo.colferMessage.from.
     *
     * @param value the replacement.
     */
    public void setFrom(String value) {
        this.from = value;
    }

    /**
     * Sets demo.colferMessage.from.
     *
     * @param value the replacement.
     * @return {link this}.
     */
    public ColferMessage withFrom(String value) {
        this.from = value;
        return this;
    }

    @Override
    public final int hashCode() {
        int h = 1;
        h = 31 * h + this.id;
        if (this.uid != null) h = 31 * h + this.uid.hashCode();
        for (byte b : this.data) h = 31 * h + b;
        if (this.from != null) h = 31 * h + this.from.hashCode();
        return h;
    }

    @Override
    public final boolean equals(Object o) {
        return o instanceof ColferMessage && equals((ColferMessage) o);
    }

    public final boolean equals(ColferMessage o) {
        if (o == null) return false;
        if (o == this) return true;
        return o.getClass() == ColferMessage.class
                && this.id == o.id
                && (this.uid == null ? o.uid == null : this.uid.equals(o.uid))
                && java.util.Arrays.equals(this.data, o.data)
                && (this.from == null ? o.from == null : this.from.equals(o.from));
    }

}
