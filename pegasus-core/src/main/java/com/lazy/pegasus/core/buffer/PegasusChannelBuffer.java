package com.lazy.pegasus.core.buffer;

import com.lazy.pegasus.core.common.PegasusBuffer;
import com.lazy.pegasus.core.common.SpanString;
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
     * @param pooled     allow pool
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
        this.buffer.setBytes(index, src, srcStart,  len);
    }

    @Override
    public void setBytes(int index, ByteBuffer src) {
        this.buffer.setBytes(index, src);
    }

    @Override
    public byte readByte() {
        return 0;
    }

    @Override
    public int readUnsignedByte() {
        return 0;
    }

    @Override
    public short readShort() {
        return 0;
    }

    @Override
    public int readUnsignedShort() {
        return 0;
    }

    @Override
    public int readInt() {
        return 0;
    }

    @Override
    public long readUnsignedInt() {
        return 0;
    }

    @Override
    public Integer readNullableInt() {
        return null;
    }

    @Override
    public long readLong() {
        return 0;
    }

    @Override
    public Long readNullableLong() {
        return null;
    }

    @Override
    public char readChar() {
        return 0;
    }

    @Override
    public float readFloat() {
        return 0;
    }

    @Override
    public double readDouble() {
        return 0;
    }

    @Override
    public String readLine() throws IOException {
        return null;
    }

    @Override
    public boolean readBoolean() {
        return false;
    }

    @Override
    public Boolean readNullableBoolean() {
        return null;
    }

    @Override
    public String readString() {
        return null;
    }

    @Override
    public SpanString readSpanString() {
        return null;
    }

    @Override
    public String readUTF() {
        return null;
    }

    @Override
    public void readBytes(PegasusBuffer dest) {

    }

    @Override
    public void readBytes(PegasusBuffer dest, int len) {

    }

    @Override
    public void readBytes(PegasusBuffer dest, int destStart, int len) {

    }

    @Override
    public void readBytes(byte[] dest) {

    }

    @Override
    public void readBytes(byte[] dest, int destStart, int len) {

    }

    @Override
    public void readBytes(ByteBuffer dest) {

    }

    @Override
    public void readFully(byte[] b) throws IOException {

    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {

    }

    @Override
    public int skipBytes(int n) {
        return 0;
    }

    @Override
    public void writeByte(byte b) {

    }

    @Override
    public void writeShort(short i16) {

    }

    @Override
    public void writeInt(int i32) {

    }

    @Override
    public void writeNullableInt(Integer i32) {

    }

    @Override
    public void writeLong(long i64) {

    }

    @Override
    public void writeNullableLong(Long i64) {

    }

    @Override
    public void writeFloat(float f32) {

    }

    @Override
    public void writeDouble(float f64) {

    }

    @Override
    public void writeChar(char c) {

    }

    @Override
    public void writeBoolean(boolean bool) {

    }

    @Override
    public void writeNullableBoolean(Boolean bool) {

    }

    @Override
    public void writeString(String s) {

    }

    @Override
    public void writeNullableString(String s) {

    }

    @Override
    public void writeUTF(String utf) {

    }

    @Override
    public void writeSpanString(SpanString s) {

    }

    @Override
    public void writeNullableSpanString(SpanString s) {

    }

    @Override
    public void writeBytes(byte[] src) {

    }

    @Override
    public void writeBytes(byte[] src, int srcStart, int len) {

    }

    @Override
    public void writeBytes(PegasusBuffer src, int len) {

    }

    @Override
    public void writeBytes(PegasusBuffer src, int srcStart, int len) {

    }

    @Override
    public void writeBytes(ByteBuf src, int len) {

    }

    @Override
    public void writeBytes(ByteBuf src, int srcStart, int len) {

    }

    @Override
    public void writeBytes(ByteBuffer src) {

    }
}
