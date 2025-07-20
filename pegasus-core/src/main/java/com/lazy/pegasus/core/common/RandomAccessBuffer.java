package com.lazy.pegasus.core.common;

import java.nio.ByteBuffer;

public interface RandomAccessBuffer {

    /**
     * Gets a byte at the given index of this buffer.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to read.
     * @return a single byte at index.
     */
    byte getByte(int index);

    /**
     * Gets an unsigned byte at the given index of this buffer.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to read.
     * @return a single unsigned byte at index.
     */
    short getUnsignedByte(int index);

    /**
     * Gets a 16-bit short integer at given index of this buffer.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to read.
     * @return a 16-bit integer at index.
     */
    short getShort(int index);

    /**
     * Gets an unsigned 16-bit integer at given index of this buffer.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to read.
     * @return an unsigned 16-bit integer at index.
     */
    int getUnsignedShort(int index);

    /**
     * Gets a 32-bit integer at given index of this buffer.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to read.
     * @return a 32-bit integer at index.
     */
    int getInt(int index);

    /**
     * Gets an unsigned 32-bit integer at given index of this buffer.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to read.
     * @return an unsigned 32-bit at index.
     */
    long getUnsignedInt(int index);

    /**
     * Gets a 64-bit integer at given index of this buffer.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to read.
     * @return a 64-bit integer at index.
     */
    long getLong(int index);

    /**
     * Gets  a char at the given index of this buffer.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to read.
     * @return a single char at index.
     */
    char getChar(int index);

    /**
     * Gets a 32-bit floating number at the given index of this buffer.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to read.
     * @return a 32-bit floating number at index.
     */
    float getFloat(int index);

    /**
     * Gets a 64-bit floating number of the given index of this buffer.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to read.
     * @return a 64-bit floating number at index.
     */
    double getDouble(int index);

    /**
     * Sets the specified byte value at the specified index in this buffer.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to write.
     * @param b     byte value to write.
     */
    void setByte(int index, byte b);

    /**
     * Sets the specified 16-bit integer value at the specified index in this buffer.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to write.
     * @param i16   16-bit short integer value to write.
     */
    void setShort(int index, short i16);

    /**
     * Sets the specified 32-bit integer value at the specified index in this buffer.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to write.
     * @param i32   32-bit integer value to write.
     */
    void setInt(int index, int i32);

    /**
     * Sets the specified 64-bit integer value at the specified index in this buffer.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to write.
     * @param i64   64-bit integer value to write.
     */
    void setLong(int index, long i64);

    /**
     * Sets the specified 32-bit floating value at the specified index in this buffer.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to write.
     * @param f32   32-bit floating value to write.
     */
    void setFloat(int index, float f32);

    /**
     * Sets the specified 64-bit floating value at the specified index in this buffer.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to write.
     * @param f64   64-bit floating value to write.
     */
    void setDouble(int index, double f64);

    /**
     * Sets the specified char value at the specified index in this buffer.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to write.
     * @param c     char value to write.
     */
    void setChar(int index, char c);


    /**
     * Transfers this buffer's data to the given destination at the specified index until the destination buffer become
     * non-writable or reach end of data source.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to start transferring in source.
     * @param dest  destination buffer.
     */
    void getBytes(int index, PegasusBuffer dest);


    /**
     * Transfers this buffer's data of range {@code [index, index + len)} to the given destination at the specified index.
     * This method increase the {@code writeIndex} of destination buffer by {@code len}.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to start transferring in source.
     * @param dest  destination buffer.
     * @param len   number of bytes to transfer.
     */
    void getBytes(int index, PegasusBuffer dest, int len);

    /**
     * Transfer this buffer's data of range {@code [index, index + len)} to the given destination at the specified index.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index     index to start transferring in source.
     * @param dest      destination buffer.
     * @param destStart index of destination buffer start to write.
     * @param len       number of bytes to transfer.
     */
    void getBytes(int index, PegasusBuffer dest, int destStart, int len);

    /**
     * Transfers this buffer's data into a destination of byte array at the specified index.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index start to transfer in this buffer.
     * @param dest  destination byte array.
     */
    void getBytes(int index, byte[] dest);

    /**
     * Transfers this buffer's data into a destination of byte array at the specified index. The destination range will be
     * {@code [destStart, destStart + len)}.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index     index start to transfer in this buffer.
     * @param dest      destination byte array.
     * @param destStart index of destination to start write.
     * @param len       number of bytes to transfer.
     */
    void getBytes(int index, byte[] dest, int destStart, int len);

    /**
     * Transfers this buffer's data into a destination of ByteBuffer at the specified index.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to start transfer in this buffer.
     * @param dest  destination ByteBuffer.
     */
    void getBytes(int index, ByteBuffer dest);

    /**
     * Transfers all readable bytes from source to this buffer at the specified index.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to start writing in this buffer.
     * @param src   source buffer to transfer from.
     */
    void setBytes(int index, PegasusBuffer src);


    /**
     * Transfers a number of bytes from source ({@code [readIndex, readIndex + len)}) to this buffer at the specified index.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to start writing in this buffer.
     * @param src   source buffer to transfer from.
     * @param len   number of bytes to transfer.
     */
    void setBytes(int index, PegasusBuffer src, int len);


    /**
     * Transfers a number of bytes from source to this buffer at the specified index.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index    index to start writing in this buffer.
     * @param src      source buffer to transfer from.
     * @param srcStart index of source to start transfer.
     * @param len      number of bytes to transfer.
     */
    void setBytes(int index, PegasusBuffer src, int srcStart, int len);

    /**
     * Transfers all bytes in source byte array to this buffer at the specified index.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index to start writing in this buffer.
     * @param src   source buffer to transfer from.
     */
    void setBytes(int index, byte[] src);

    /**
     * Transfers a number of bytes from source byte array ({@code [srcStart, srcStart + len)}) to this buffer
     * at the specified index.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index    index start writing in this buffer.
     * @param src      source buffer to transfer from.
     * @param srcStart index of source to start transfer.
     * @param len      number of bytes to transfer.
     */
    void setBytes(int index, byte[] src, int srcStart, int len);

    /**
     * Transfer all remaining bytes from ByteBuffer to this buffer at the specified index.
     * <p>
     * Note that this method do NOT modify {@code readIndex} or {@code writeIndex} in this buffer.
     *
     * @param index index start writing in this buffer.
     * @param src   ByteBuffer source.
     */
    void setBytes(int index, ByteBuffer src);

}
