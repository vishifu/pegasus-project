package com.lazy.pegasus.core.common;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

/**
 * A {@link PegasusBuffer} wraps netty ChannelBuffer and is used throughout code base here.
 * <p>
 * Most of its implementations come from netty ChannelBuffer
 * <p>
 * Instance of this can be obtained from factory.
 */
public interface PegasusBuffer extends SequenceAccessBuffer, RandomAccessBuffer {

    /**
     * @return number of allocated bytes in this buffer.
     */
    int capacity();

    /**
     * @return the underlying netty's buffer.
     */
    ByteBuf byteBuf();

    /**
     * @return the current read pointer position of this buffer.
     */
    int readIndex();

    /**
     * Seek the reader pointer to given index.
     *
     * @param index new read pointer index.
     */
    void readIndex(int index);

    /**
     * @return current write pointer position of this buffer.
     */
    int writeIndex();

    /**
     * Seeks write pointer to given index.
     *
     * @param index new write pointer index.
     */
    void writeIndex(int index);

    /**
     * Seeks the {@code readPointer} and {@code writePointer} of this buffer to given positions.
     * Useful when ignore the invocation order of read and write pointer order. This call will fail in case:
     *
     * <pre>
     *     1. create a buffer with writer, reader and capacity respectively 0, 0, 8
     *     2. buf.readIndex(2) -> IndexOutOfBound due to seek readIndex to 2 which is greater than writeIndex (current is 0).
     * </pre>
     *
     * <pre>
     *     1. create a buffer with initial bytes, buf = new Buffer(bytes[8])
     *     2. buf.writeIndex(4) -> IndexOutOfBound due to both writer and reader is at index 8 now, could not seek
     *     writer to 4 (which is < reader)
     * </pre>
     *
     * <p>
     * This method give a guarantee that it never throws an IndexOutOfBound exception due to illegal state of buffer.
     * </p>
     *
     * <pre>
     *     No matter what the current state of buffer is, this call always succeed as long as the index is not out of
     *     bound [0, capacity).
     * </pre>
     *
     * @param readIndex  read pointer index
     * @param writeIndex write pointer index
     */
    void setIndex(int readIndex, int writeIndex);

    /**
     * @return the number of bytes that can be read (equals to writerIndex - readerIndex).
     */
    int readableBytes();

    /**
     * @return the number of bytes that can be written (equals to capacity - writerIndex).
     */
    int writableBytes();

    /**
     * @return true if readable bytes is greater than 0, otherwise false.
     */
    boolean readable();

    /**
     * @return true if writable bytes is greater than 0, otherwise false.
     */
    boolean writeable();

    /**
     * Sets the {@code readIndex} and {@code writeIndex} to 0, this method is identical with {@link #setIndex(int, int) setIndex(0, 0)}
     */
    void clear();

    /**
     * Marks the current position of {@code readIndex} in this buffer, the initial marked value is 0.
     */
    void markReadIndex();

    /**
     * Resets the read pointer to marked position.
     */
    void resetReadIndex();

    /**
     * Marks the current position of {@code writeIndex} in this buffer, the initial marked value is 0.
     */
    void markWriteIndex();

    /**
     * Resets the write pointer to marked position.
     */
    void resetWriteIndex();

    /**
     * Discards the bytes between the 0th index and {@code readIndex}, move the bytes between {@code readIndex}
     * and {@code writeIndex} to the 0th index, and sets the read and write pointer respectively
     * to {@code 0} and {@code oldWriteIndex - oldReadIndex}.
     */
    void discardReadBytes();

    /**
     * Gets a new slice of this buffer's sub-region starting at the current {@code readIndex} and increases the {@code readIndex}
     * by size of new slice (len).
     *
     * @param len size of new slice.
     * @return newly created slice buffer.
     */
    PegasusBuffer readSlice(int len);

    /**
     * Creates a copy of this buffer's readable bytes. Modifying content of returned buffer or this buffer do not affect
     * each other.
     * <p>
     * This method is similar to {@link #copy(int, int) copy(buf.readIndex(), buf.writeIndex())}.
     * <p>
     * This method do not modify {@code readIndex} or {@code writeIndex}.
     *
     * @return newly created buffer from this buffer.
     */
    PegasusBuffer copy();

    /**
     * Creates a copy of an amount of this buffer's bytes from the specified index. Modifying content of returned buffer
     * or this buffer do not affect each other.
     * <p>
     * This method do not modify {@code readIndex} or {@code writeIndex}.
     *
     * @return newly created buffer from this buffer.
     */
    PegasusBuffer copy(int index, int len);

    /**
     * Creates a view of this buffer's readable bytes, modifying the content of returned buffer or this buffer affects each
     * other's content while they maintain separate index pointers and marks.
     * <p>
     * This method is similar to {@link #slice(int, int) buf.slice(buf.readIndex(), buf.writeIndex())}.
     * <p>
     * This method do not modify {@code readIndex} or {@code writeIndex}.
     *
     * @return newly created buffer from this buffer
     */
    PegasusBuffer slice();

    /**
     * Creates a view of a sub-region on this buffer's at the specified index, modifying the content of returned buffer
     * or this buffer affects each other's content while they maintain separate index pointers and marks.
     * <p>
     * This method do not modify {@code readIndex} or {@code writeIndex}.
     *
     * @return newly created buffer from this buffer
     */
    PegasusBuffer slice(int index, int len);

    /**
     * Creates a buffer which shares the whole region of this buffer, modifying the content of returned buffer or this buffer
     * affects each other's content, while they maintain separate index pointers and marks.
     * <p>
     * This method is similar to {@link #slice(int, int) buf.slice(0, buf.capacity())}.
     * <p>
     * This method do not modify {@code readIndex} or {@code writeIndex}.
     *
     * @return newly created buffer from this buffer.
     */
    PegasusBuffer duplicate();

    /**
     * Converts this buffer's readable bytes into a NIO ByteBuffer, the returned ByteBuffer might or might not share the
     * content of this buffer.
     * <p>
     * This method is similar to {@link #toBuffer(int, int) buf.toBuffer(buf.readIndex(), buf.writeIndex())}.
     * <p>
     * This method do not modify {@code readIndex} or {@code writeIndex}.
     *
     * @return newly create NIO ByteBuffer from this buffer.
     */
    ByteBuffer toBuffer();

    /**
     * Converts an amount of this buffer's from a specified index into a NIO ByteBuffer, the returned ByteBuffer might
     * or might not share the content of this buffer.
     * <p>
     * This method do not modify {@code readIndex} or {@code writeIndex}.
     *
     * @return newly create NIO ByteBuffer from this buffer.
     */
    ByteBuffer toBuffer(int index, int len);

    /**
     * Release any underlying resources in this buffer.
     */
    void release();

}
