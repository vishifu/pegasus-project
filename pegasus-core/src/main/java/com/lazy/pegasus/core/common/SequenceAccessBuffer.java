package com.lazy.pegasus.core.common;

import io.netty.buffer.ByteBuf;

import java.io.DataInput;
import java.nio.ByteBuffer;

public interface SequenceAccessBuffer extends DataInput {

    /**
     * Gets a single byte at the current {@code readIndex} and increases the {@code readIndex} by 1 in this buffer.
     *
     * @return single byte at the current {@code readIndex}
     */
    @Override
    byte readByte();

    /**
     * Gets an unsigned byte at the current {@code readIndex} and increases the {@code readIndex} by 1 in this buffer.
     *
     * @return unsigned byte at the current {@code readIndex}
     */
    @Override
    int readUnsignedByte();

    /**
     * Gets a 16-bit integer at the current {@code readIndex} and increases the {@code readIndex} by 2 in this buffer.
     *
     * @return 16-bit short integer at the current {@code readIndex}
     */
    @Override
    short readShort();

    /**
     * Gets an unsigned 16-bit integer at the current {@code readIndex} and increases the {@code readIndex} by 2 in this buffer.
     *
     * @return unsigned 16-bit short integer at the current {@code readIndex}
     */
    @Override
    int readUnsignedShort();

    /**
     * Gets a 32-bit integer at the current {@code readIndex} and increases the {@code readIndex} by 4 in this buffer.
     *
     * @return 32-bit integer at the current {@code readIndex}
     */
    @Override
    int readInt();

    /**
     * Gets an unsigned 32-bit integer at the current {@code readIndex} and increases the {@code readIndex} by 4 in this buffer.
     *
     * @return unsigned 32-bit integer at the current {@code readIndex}
     */
    long readUnsignedInt();

    /**
     * Gets a potentially null of 32-bit integer at the current {@code readIndex} and increases the {@code readIndex} by 4
     * in this buffer.
     *
     * @return 32-bit integer at the current {@code readIndex}, if possible
     */
    Integer readNullableInt();

    /**
     * Gets an 64-bit integer at the current {@code readIndex} and increases the {@code readIndex} by 8 in this buffer.
     *
     * @return 64-bit integer at the current {@code readIndex}
     */
    @Override
    long readLong();

    /**
     * Gets a potentially null of 64-bit integer at the current {@code readIndex} and increases the {@code readIndex} by 4
     * in this buffer.
     *
     * @return 64-bit integer at the current {@code readIndex}, if possible
     */
    Long readNullableLong();

    /**
     * Gets a char at the current {@code readIndex} and increases the {@code readIndex} by 2 in this buffer.
     *
     * @return char value at the current {@code readIndex}
     */
    @Override
    char readChar();

    /**
     * Gets a 32-bit float value at the current {@code readIndex} and increases the {@code readIndex} by 4 in this buffer.
     *
     * @return 32-bit float number at the current {@code readIndex}
     */
    @Override
    float readFloat();

    /**
     * Gets a 64-bit float value at the current {@code readIndex} and increases the {@code readIndex} by 4 in this buffer.
     *
     * @return 64-bit float number at the current {@code readIndex}
     */
    @Override
    double readDouble();

    /**
     * Gets a boolean value at the current {@code readIndex} and increases the {@code readIndex} by 1 in this buffer.
     *
     * @return boolean value at the current {@code readIndex}
     */
    @Override
    boolean readBoolean();

    /**
     * Gets a potentially null boolean value at the current {@code readIndex} and increases the {@code readIndex}
     * by 1 in this buffer.
     *
     * @return nullable boolean value at the current {@code readIndex}
     */
    Boolean readNullableBoolean();

    /**
     * Gets a non-null String at the current {@code readIndex} and increases the {@code readIndex} by the string's length.
     *
     * @return a non-null String at the current {@code readIndex}
     */
    String readString();

    /**
     * Gets a {@link SpanString} at the current {@code readIndex} and increases the {@code readIndex} by the SpanString's length.
     *
     * @return non-null {@link SpanString} at the current {@code readIndex}
     */

    SpanString readSpanString();

    /**
     * Gets the UTF-8 String at the current {@code readIndex} and increases the {@code readIndex} by the string's length.
     *
     * @return UTF-8 String value at the current {@code readIndex}
     */
    @Override
    String readUTF();

    /**
     * Transfers all readable bytes of this buffer's data into destination buffer at the current {@code readIndex}, then
     * increases the {@code readIndex} of this buffer by the number of readable bytes.
     *
     * @param dest destination buffer, the {@code writeIndex} of destination increases by the written bytes
     */
    void readBytes(PegasusBuffer dest);

    /**
     * Transfers a number of bytes of this buffer's data into destination buffer at the current {@code readIndex}, then
     * increases the {@code readIndex} of this buffer by {@code len}.
     *
     * @param dest destination buffer, the {@code writeIndex} of destination increases by the written bytes
     * @param len  number of bytes to transfer
     */
    void readBytes(PegasusBuffer dest, int len);

    /**
     * Transfers a number of bytes of this buffer's data into destination buffer at the current {@code readIndex}, then
     * increases the {@code readIndex} of this buffer by {@code len}.
     *
     * @param dest      destination buffer, the {@code writeIndex} of destination increases by the written bytes
     * @param destStart index to start writing of destination
     * @param len       number of bytes to transfer
     */
    void readBytes(PegasusBuffer dest, int destStart, int len);

    /**
     * Transfers readable bytes of this buffer's data into destination byte array at the current {@code readIndex},
     * until can not read anymore or fulfill bytes array, then increases the {@code readIndex} of this buffer
     * by the transfer bytes.
     *
     * @param dest destination byte array
     */
    void readBytes(byte[] dest);


    /**
     * Transfers readable bytes of this buffer's data into destination byte array at the current {@code readIndex},
     * until can not read anymore or reach {@code len}, then increases the {@code readIndex} of this buffer
     * by the transfer bytes.
     *
     * @param dest      destination byte array
     * @param destStart index to start writing in destination
     * @param len       number of bytes to transfer
     */
    void readBytes(byte[] dest, int destStart, int len);

    /**
     * Transfer readable bytes of this buffer's data into destination ByteBuffer at the current {@code readIndex},
     * until can not read anymore or fulfill ByteBuffer, then increases the {@code readIndex} of this buffer by the
     * number of transfer bytes.
     *
     * @param dest destination ByteBuffer
     */
    void readBytes(ByteBuffer dest);

    /**
     * Increases the current {@code readIndex} by {@code n} in this buffer.
     *
     * @param n the number of bytes to be skipped
     * @return the number of byte actually skips.
     */
    @Override
    int skipBytes(int n);

    /**
     * Sets the specified byte value at the current {@code writeIndex}, then increases the {@code writeIndex} by 1 in
     * this buffer.
     *
     * @param b byte value to set
     */
    void writeByte(byte b);

    /**
     * Sets the specified short 16-bit value at the current {@code writeIndex}, then increases the {@code writeIndex} by 2
     * in this buffer.
     *
     * @param i16 16-bit value to set
     */
    void writeShort(short i16);

    /**
     * Sets the specified 32-bit value at the current {@code writeIndex}, then increases the {@code writeIndex} by 4 in
     * this buffer.
     *
     * @param i32 32-bit value to set
     */
    void writeInt(int i32);

    /**
     * Sets the specified (potentially null) 32-bit integer at the current {@code writeIndex}, then increases the
     * {@code writeIndex} by 4 in this buffer.
     *
     * @param i32 nullable 32-bit integer
     */
    void writeNullableInt(Integer i32);

    /**
     * Sets the specified 64-bit value at the current {@code writeIndex}, then increases the {@code writeIndex} by 8
     * in this buffer.
     *
     * @param i64 64-bit value to set
     */
    void writeLong(long i64);

    /**
     * Sets the specified (potentially null) 64-bit integer at the current {@code writeIndex}, then increases the
     * {@code writeIndex} by 4 in this buffer.
     *
     * @param i64 nullable 64-bit integer
     */
    void writeNullableLong(Long i64);

    /**
     * Sets the specified 32-bit float value at the current {@code writeIndex}, then increases the {@code writeIndex} by 4
     * in this buffer.
     *
     * @param f32 32-bit floating value to set
     */
    void writeFloat(float f32);

    /**
     * Sets the specified 64-bit float value at the current {@code writeIndex}, then increases the {@code writeIndex} by 8
     * in this buffer.
     *
     * @param f64 64-bit value to set
     */
    void writeDouble(float f64);

    /**
     * Sets the specified char at the current {@code writeIndex}, then increases the {@code writeIndex} by 2 in this buffer.
     *
     * @param c character to set
     */
    void writeChar(char c);

    /**
     * Sets the specified bool value at the current {@code writeIndex}, then increases the {@code writeIndex} by 1 in
     * this buffer.
     *
     * @param bool bool value to set
     */
    void writeBoolean(boolean bool);

    /**
     * Sets the specified (potentially null) bool at the current {@code writeIndex}, the increases the {@code writeIndex}
     * by 1 in this buffer.
     *
     * @param bool nullable bool value to set
     */
    void writeNullableBoolean(Boolean bool);

    /**
     * Sets the specified non-null string at the current {@code writeIndex}, then increases the {@code writeIndex} by the
     * length of string in this buffer.
     *
     * @param s non-null string to set
     */
    void writeString(String s);

    /**
     * Sets the specified (potentially null) string at the current {@code writeIndex}, then increases the {@code writeIndex} by the
     * length of string in this buffer.
     *
     * @param s nullable string to set
     */
    void writeNullableString(String s);

    /**
     * Sets the specified non-null UTF string at the current {@code writeIndex}, then increases the {@code writeIndex} by the
     * length of string in this buffer.
     *
     * @param utf non-null UTF string to set
     */
    void writeUTF(String utf);

    /**
     * Sets the specified non-null {@link SpanString} at the current {@code writeIndex}, then increases the {@code writeIndex}
     * by the length of string in this buffer.
     *
     * @param s non-null {@link SpanString} to set
     */
    void writeSpanString(SpanString s);

    /**
     * Sets the specified (potentially null) {@link SpanString} at the current {@code writeIndex}, then increases the
     * {@code writeIndex} by the length of string in this buffer.
     *
     * @param s nullable {@link SpanString} to set
     */
    void writeNullableSpanString(SpanString s);

    /**
     * Transfers the data from byte array source to this buffer, starting to write at the current {@code writeIndex}.
     * <p>
     * The {@code writeIndex} increases until read fully source.
     *
     * @param src source byte array
     */
    void writeBytes(byte[] src);

    /**
     * Transfers an amount of bytes from byte array source to this buffer, starting to read from source at the given index
     * and start to write at current {@code writeIndex} in this buffer.
     * <p>
     * The {@code writeIndex} increases by the specified len in this buffer.
     *
     * @param src      byte array source
     * @param srcStart index to start transferring data in sourc
     * @param len      number of bytes to transfer
     */
    void writeBytes(byte[] src, int srcStart, int len);

    /**
     * Transfers an amount of bytes from source buffer to this buffer, starting to read at the {@code readIndex}
     * in source buffer and starting to write at current {@code writeIndex} in this buffer.
     * <p>
     * The {@code writeIndex} in this buffer and {@code readIndex} in source buffer increase by specified len.
     *
     * @param src source buffer
     * @param len number of bytes to transfer
     */
    void writeBytes(PegasusBuffer src, int len);

    /**
     * Transfers an amount of bytes from source buffer to this buffer, starting to read at the {@code readIndex} in
     * source buffer and starting to write at current {@code writeIndex} in this buffer.
     * <p>
     * The {@code writeIndex} in this buffer and {@code readIndex} in source buffer increase by specified len.
     *
     * @param src      source buffer
     * @param srcStart index to start transferring in source buffer
     * @param len      number of bytes to transfer
     */
    void writeBytes(PegasusBuffer src, int srcStart, int len);

    /**
     * Transfers an amount of bytes from netty buffer to this buffer, starting to read at current {@code readerIndex} in
     * netty buffer and starting to write ad current {@code writeIndex} in this buffer.
     * <p>
     * The {@code writeIndex} in this buffer and {@code readerIndex} of netty buffer increase by the specified length.
     *
     * @param src source netty buffer
     * @param len number of bytes to transfer
     */
    void writeBytes(ByteBuf src, int len);

    /**
     * Transfers an amount of bytes from netty buffer to this buffer, starting to read at specified index in
     * netty buffer and starting to write ad current {@code writeIndex} in this buffer.
     * <p>
     * The {@code writeIndex} in this buffer increase by the specified length.
     *
     * @param src      source netty buffer
     * @param srcStart index to start transferring in source
     * @param len      number of bytes to transfer
     */
    void writeBytes(ByteBuf src, int srcStart, int len);

    /**
     * Transfer remaining bytes from source ByteBuffer to this buffer, starting to read from current {@code pointer} in source buffer
     * and starting to write from the current {@code writeIndex} in this buffer.
     * <p>
     * The {@code writeIndex} in this buffer and {@code pointer} in ByteBuffer both increase.
     *
     * @param src source ByteBuffer
     */
    void writeBytes(ByteBuffer src);


}
