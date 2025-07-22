package com.lazy.pegasus.core.buffer;

import com.lazy.pegasus.core.common.DataConstants;
import com.lazy.pegasus.core.common.PegasusBuffer;
import com.lazy.pegasus.core.common.SpanString;
import com.lazy.pegasus.core.utils.BytesUtil;
import com.lazy.pegasus.core.utils.Utf8Util;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PegasusChannelBuffer implements PegasusBuffer {

    protected final ByteBuf buffer;

    private final boolean releasable;

    /**
     * Constructs an instance by a backed netty buffer, automatically disable releasing and pooling.
     *
     * @param buffer backed netty buffer
     */
    public PegasusChannelBuffer(final ByteBuf buffer) {
        this(buffer, false);
    }

    /**
     * Constructs an instance by a backed netty buffer, allow releasing and pooling.
     *
     * @param buffer     backed netty buffer
     * @param releasable allow release
     */
    public PegasusChannelBuffer(final ByteBuf buffer, boolean releasable) {
        if (!releasable) {
            this.buffer = Unpooled.unreleasableBuffer(buffer);
        } else {
            this.buffer = buffer;
        }

        this.releasable = releasable;
    }

    public static ByteBuf unwrap(ByteBuf buffer) {
        ByteBuf parent;

        // unwrap its self
        while ((parent = buffer.unwrap()) != null && parent != buffer) {
            buffer = parent;
        }

        return buffer;
    }

    @Override
    public int capacity() {
        return this.buffer.capacity();
    }

    @Override
    public ByteBuf byteBuf() {
        return this.buffer;
    }

    @Override
    public int readIndex() {
        return this.buffer.readerIndex();
    }

    @Override
    public void readIndex(int index) {
        this.buffer.readerIndex(index);
    }

    @Override
    public int writeIndex() {
        return this.buffer.writerIndex();
    }

    @Override
    public void writeIndex(int index) {
        this.buffer.writerIndex(index);
    }

    @Override
    public void setIndex(int readIndex, int writeIndex) {
        this.buffer.setIndex(readIndex, writeIndex);
    }

    @Override
    public int readableBytes() {
        return this.buffer.readableBytes();
    }

    @Override
    public int writableBytes() {
        return this.buffer.writableBytes();
    }

    @Override
    public boolean readable() {
        return this.buffer.isReadable();
    }

    @Override
    public boolean writeable() {
        return this.buffer.isWritable();
    }

    @Override
    public void clear() {
        this.buffer.clear();
    }

    @Override
    public void markReadIndex() {
        this.buffer.markReaderIndex();
    }

    @Override
    public void resetReadIndex() {
        this.buffer.resetReaderIndex();
    }

    @Override
    public void markWriteIndex() {
        this.buffer.markWriterIndex();
    }

    @Override
    public void resetWriteIndex() {
        this.buffer.resetWriterIndex();
    }

    @Override
    public void discardReadBytes() {
        // TODO: TEST
        this.buffer.discardReadBytes();
    }

    @Override
    public PegasusBuffer readSlice(int len) {
        return new PegasusChannelBuffer(this.buffer.readSlice(len), this.releasable);
    }

    @Override
    public PegasusBuffer copy() {
        return new PegasusChannelBuffer(this.buffer.copy(), this.releasable);
    }

    @Override
    public PegasusBuffer copy(int index, int len) {
        return new PegasusChannelBuffer(this.buffer.copy(index, len), this.releasable);
    }

    @Override
    public PegasusBuffer slice() {
        return new PegasusChannelBuffer(this.buffer.slice(), this.releasable);
    }

    @Override
    public PegasusBuffer slice(int index, int len) {
        return new PegasusChannelBuffer(this.buffer.slice(index, len), this.releasable);
    }

    @Override
    public PegasusBuffer duplicate() {
        return new PegasusChannelBuffer(this.buffer.duplicate(), this.releasable);
    }

    @Override
    public ByteBuffer toBuffer() {
        return this.buffer.nioBuffer();
    }

    @Override
    public ByteBuffer toBuffer(int index, int len) {
        return this.buffer.nioBuffer(index, len);
    }

    @Override
    public void release() {
        if (this.releasable) {
            this.buffer.release();
        }
    }

    @Override
    public byte getByte(int index) {
        return this.buffer.getByte(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        return this.buffer.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        return this.buffer.getShort(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        return this.buffer.getUnsignedShort(index);
    }

    @Override
    public int getInt(int index) {
        return this.buffer.getInt(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return this.buffer.getUnsignedInt(index);
    }

    @Override
    public long getLong(int index) {
        return this.buffer.getLong(index);
    }

    @Override
    public char getChar(int index) {
        return this.buffer.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        return this.buffer.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        return this.buffer.getDouble(index);
    }

    @Override
    public void setByte(int index, byte b) {
        this.buffer.setByte(index, b);
    }

    @Override
    public void setShort(int index, short i16) {
        this.buffer.setShort(index, i16);
    }

    @Override
    public void setInt(int index, int i32) {
        this.buffer.setInt(index, i32);
    }

    @Override
    public void setLong(int index, long i64) {
        this.buffer.setLong(index, i64);
    }

    @Override
    public void setFloat(int index, float f32) {
        this.buffer.setFloat(index, f32);
    }

    @Override
    public void setDouble(int index, double f64) {
        this.buffer.setDouble(index, f64);
    }

    @Override
    public void setChar(int index, char c) {
        this.buffer.setChar(index, c);
    }

    @Override
    public void getBytes(int index, PegasusBuffer dest) {
        this.buffer.getBytes(index, dest.byteBuf());
    }

    @Override
    public void getBytes(int index, PegasusBuffer dest, int len) {
        this.buffer.getBytes(index, dest.byteBuf(), len);
    }

    @Override
    public void getBytes(int index, PegasusBuffer dest, int destStart, int len) {
        this.buffer.getBytes(index, dest.byteBuf(), destStart, len);
    }

    @Override
    public void getBytes(int index, byte[] dest) {
        this.buffer.getBytes(index, dest);
    }

    @Override
    public void getBytes(int index, byte[] dest, int destStart, int len) {
        this.buffer.getBytes(index, dest, destStart, len);
    }

    @Override
    public void getBytes(int index, ByteBuffer dest) {
        this.buffer.getBytes(index, dest);
    }

    @Override
    public void setBytes(int index, PegasusBuffer src) {
        this.buffer.setBytes(index, src.byteBuf());
    }

    @Override
    public void setBytes(int index, PegasusBuffer src, int len) {
        this.buffer.setBytes(index, src.byteBuf(), len);
    }

    @Override
    public void setBytes(int index, PegasusBuffer src, int srcStart, int len) {
        this.buffer.setBytes(index, src.byteBuf(), srcStart, len);
    }

    @Override
    public void setBytes(int index, byte[] src) {
        this.buffer.setBytes(index, src);
    }

    @Override
    public void setBytes(int index, byte[] src, int srcStart, int len) {
        this.buffer.setBytes(index, src, srcStart, len);
    }

    @Override
    public void setBytes(int index, ByteBuffer src) {
        this.buffer.setBytes(index, src);
    }

    @Override
    public byte readByte() {
        return this.buffer.readByte();
    }

    @Override
    public int readUnsignedByte() {
        return this.buffer.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return this.buffer.readShort();
    }

    @Override
    public int readUnsignedShort() {
        return this.buffer.readUnsignedShort();
    }

    @Override
    public int readInt() {
        return this.buffer.readInt();
    }

    @Override
    public long readUnsignedInt() {
        return this.buffer.readUnsignedInt();
    }

    @Override
    public Integer readNullableInt() {
        int b = readByte();
        if (b == DataConstants.NULL) {
            return null;
        }

        return readInt();
    }

    @Override
    public long readLong() {
        return this.buffer.readLong();
    }

    @Override
    public Long readNullableLong() {
        int b = readByte();
        if (b == DataConstants.NULL) {
            return null;
        }

        return readLong();
    }

    @Override
    public char readChar() {
        return this.buffer.readChar();
    }

    @Override
    public float readFloat() {
        return this.buffer.readFloat();
    }

    @Override
    public double readDouble() {
        return this.buffer.readDouble();
    }

    @Override
    public String readLine() throws IOException {
        return null;
    }

    @Override
    public boolean readBoolean() {
        return readByte() != 0;
    }

    @Override
    public Boolean readNullableBoolean() {
        byte b = readByte();
        if (b == DataConstants.NULL) {
            return null;
        }

        return readBoolean();
    }

    @Override
    public String readString() {
        return readString0();
    }

    @Override
    public SpanString readSpanString() {
        return SpanString.of(this.buffer);
    }

    @Override
    public String readUTF() {
        return Utf8Util.readUtf8(this.buffer);
    }

    @Override
    public void readBytes(PegasusBuffer dest) {
        this.buffer.readBytes(dest.byteBuf());
    }

    @Override
    public void readBytes(PegasusBuffer dest, int len) {
        this.buffer.readBytes(dest.byteBuf(), len);
    }

    @Override
    public void readBytes(PegasusBuffer dest, int destStart, int len) {
        this.buffer.readBytes(dest.byteBuf(), destStart, len);
    }

    @Override
    public void readBytes(byte[] dest) {
        this.buffer.readBytes(dest);
    }

    @Override
    public void readBytes(byte[] dest, int destStart, int len) {
        this.buffer.readBytes(dest, destStart, len);
    }

    @Override
    public void readBytes(ByteBuffer dest) {
        this.buffer.readBytes(dest);
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        readBytes(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        readBytes(b, off, len);
    }

    @Override
    public int skipBytes(int n) {
        this.buffer.skipBytes(n);
        return n;
    }

    @Override
    public void writeByte(byte b) {
        this.buffer.writeByte(b);
    }

    @Override
    public void writeShort(short i16) {
        this.buffer.writeShort(i16);
    }

    @Override
    public void writeInt(int i32) {
        this.buffer.writeShort(i32);
    }

    @Override
    public void writeNullableInt(Integer i32) {
        if (i32 == null) {
            this.buffer.writeByte(DataConstants.NULL);
        } else {
            this.buffer.writeByte(DataConstants.NOT_NULL);
            writeInt(i32);
        }
    }

    @Override
    public void writeLong(long i64) {
        this.buffer.writeLong(i64);
    }

    @Override
    public void writeNullableLong(Long i64) {
        if (i64 == null) {
            this.buffer.writeByte(DataConstants.NULL);
        } else  {
            this.buffer.writeByte(DataConstants.NOT_NULL);
            writeLong(i64);
        }
    }

    @Override
    public void writeFloat(float f32) {
        this.buffer.writeFloat(f32);
    }

    @Override
    public void writeDouble(float f64) {
        this.buffer.writeDouble(f64);
    }

    @Override
    public void writeChar(char c) {
        this.buffer.writeChar(c);
    }

    @Override
    public void writeBoolean(boolean bool) {
        this.buffer.writeByte((byte) (bool ? 1 : 0));
    }

    @Override
    public void writeNullableBoolean(Boolean bool) {
        if (bool == null) {
            this.buffer.writeByte(DataConstants.NULL);
        } else {
            this.buffer.writeByte(DataConstants.NOT_NULL);
            writeBoolean(bool);
        }
    }

    @Override
    public void writeString(String s) {
        Utf8Util.writeString(this.buffer, s);
    }

    @Override
    public void writeNullableString(String s) {
        Utf8Util.writeNullableString(this.buffer, s);
    }

    @Override
    public void writeUTF(String utf) {
        Utf8Util.saveUtf(this.buffer, utf);
    }

    @Override
    public void writeSpanString(SpanString s) {
        SpanString.writeSpanString(this.buffer, s);
    }

    @Override
    public void writeNullableSpanString(SpanString s) {
        SpanString.writeNullableSpanString(this.buffer, s);
    }

    @Override
    public void writeBytes(byte[] src) {
        this.buffer.writeBytes(src);
    }

    @Override
    public void writeBytes(byte[] src, int srcStart, int len) {
        this.buffer.writeBytes(src, srcStart,  len);
    }

    @Override
    public void writeBytes(PegasusBuffer src, int len) {
        this.buffer.writeBytes(src.byteBuf(), len);
    }

    @Override
    public void writeBytes(PegasusBuffer src, int srcStart, int len) {
        this.buffer.writeBytes(src.byteBuf(), srcStart, len);
    }

    @Override
    public void writeBytes(ByteBuf src, int len) {
        this.buffer.writeBytes(src, len);
    }

    @Override
    public void writeBytes(ByteBuf src, int srcStart, int len) {
        this.buffer.writeBytes(src, srcStart,  len);
    }

    @Override
    public void writeBytes(ByteBuffer src) {
        this.buffer.writeBytes(src);
    }

    private String readString0() {
        int len = this.buffer.readInt();
        if (len < 9) {
            char[] chars = new char[len];
            for (int i = 0; i < len; i++) {
                chars[i] = (char) buffer.readShort();
            }

            return new String(chars);
        } else if (len < 0xfff) {
            return readUTF();
        } else {
            return new String(BytesUtil.readFromByteBuf(buffer));
        }
    }
}
