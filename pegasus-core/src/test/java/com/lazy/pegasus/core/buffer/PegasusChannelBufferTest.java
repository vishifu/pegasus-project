package com.lazy.pegasus.core.buffer;

import com.lazy.pegasus.core.common.PegasusBuffer;
import com.lazy.pegasus.core.utils.BytesUtil;
import com.lazy.pegasus.core.utils.RandsUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PegasusChannelBufferTest {

    private byte[] createBytes(int n) {
        return RandsUtil.randBytes(n);
    }

    private PegasusBuffer createBuffer(int len) {
        return new PegasusChannelBuffer(Unpooled.wrappedBuffer(createBytes(len)), false);
    }

    private PegasusBuffer createBuffer() {
        return createBuffer(32);
    }

    private PegasusBuffer createEmptyBuffer(int n) {
        ByteBuf buf = Unpooled.buffer(n);
        return new PegasusChannelBuffer(buf);
    }

    @Test
    void construct() {
        byte[] bytes = createBytes(32);
        PegasusBuffer buf1 = new PegasusChannelBuffer(Unpooled.wrappedBuffer(bytes));
        PegasusBuffer buf2 = new PegasusChannelBuffer(Unpooled.wrappedBuffer(bytes), true);
        PegasusBuffer buf3 = new PegasusChannelBuffer(Unpooled.wrappedBuffer(bytes), true);

        assertNotNull(buf1);
        assertNotNull(buf2);
        assertNotNull(buf3);
    }

    @Test
    void indexes() {
        byte[] bytes = createBytes(32);
        ByteBuf nettyBuf = Unpooled.wrappedBuffer(bytes);
        PegasusBuffer buf = new PegasusChannelBuffer(nettyBuf);

        // asserts indexes
        assertEquals(0, buf.readIndex());
        assertEquals(32, buf.writeIndex());
        assertEquals(32, buf.readableBytes());
        assertEquals(0, buf.writableBytes());

        assertTrue(buf.readable());
        assertFalse(buf.writeable());

        // assert move indexes
        nettyBuf.writerIndex(20);
        assertEquals(20, buf.writeIndex());
        buf.writeIndex(24);
        assertEquals(24, buf.writeIndex());

        nettyBuf.readerIndex(10);
        assertEquals(10, buf.readIndex());
        buf.readIndex(12);
        assertEquals(12, buf.readIndex());

        // clears
        buf.clear();
        assertEquals(0, buf.readIndex());
        assertEquals(0, buf.writeIndex());

        // move read index > write OR negative OR over capacity
        buf.writeIndex(10);
        assertThrows(IndexOutOfBoundsException.class, () -> buf.readIndex(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> buf.readIndex(40));
        assertThrows(IndexOutOfBoundsException.class, () -> buf.readIndex(12));
        assertThrows(IndexOutOfBoundsException.class, () -> buf.writeIndex(40));

        // assert invocation setIndex
        buf.setIndex(25, 27);
        assertEquals(25, buf.readIndex());
        assertEquals(27, buf.writeIndex());
        assertThrows(IndexOutOfBoundsException.class, () -> buf.setIndex(10, 8));

        // assert marks AND resets
        buf.clear();
        buf.setIndex(10, 20);
        buf.markReadIndex();
        buf.markWriteIndex();

        buf.setIndex(25, 30);
        buf.resetReadIndex();
        buf.resetWriteIndex();
        assertEquals(10, buf.readIndex());
        assertEquals(20, buf.writeIndex());
    }

    @Test
    void slice() {
        PegasusBuffer buffer = createBuffer();

        PegasusBuffer slice1 = buffer.slice();
        assertEquals(buffer.capacity(), slice1.capacity(),
                "slice1 capacity must equals to buffer capacity");
        assertNotSame(buffer, slice1, "slice1 is not buffer");

        // edit some data at buffer -> slice1
        buffer.byteBuf().array()[12] = (byte) 'a';
        assertEquals(buffer.byteBuf().unwrap(), slice1.byteBuf().unwrap(),
                "buffer and slice1 shared same data region");
        assertEquals((byte) 'a', slice1.byteBuf().nioBuffer().get(12),
                "element 12-th edited to 'a' affect on slice1");

        PegasusBuffer slice2 = buffer.slice(10, 6);
        assertNotSame(buffer, slice2, "slice2 is not buffer");
        buffer.byteBuf().array()[13] = (byte) 'b';
        assertEquals('b', slice2.byteBuf().nioBuffer().get(3));

        buffer.readIndex(0);
        PegasusBuffer slice3 = buffer.readSlice(10);

        buffer.byteBuf().array()[1] = (byte) 'c';
        assertEquals('c', slice3.byteBuf().nioBuffer().get(1));
    }

    @Test
    void copy() {
        PegasusBuffer buf1 = createBuffer();
        PegasusBuffer buf2 = createBuffer();

        PegasusBuffer copyBuf1 = buf1.copy();
        assertEquals(buf1.capacity(), copyBuf1.capacity());
        assertNotSame(buf1, copyBuf1);
        assertNotSame(buf1.byteBuf(), copyBuf1.byteBuf());
        assertEquals(buf1.byteBuf(), copyBuf1.byteBuf());

        PegasusBuffer copyBuf2 = buf2.copy(10, 6);
        assertEquals(6, copyBuf2.capacity());

        ByteBuf expect2 = buf2.byteBuf().slice(10, 6);
        assertNotSame(expect2, copyBuf2.byteBuf());
        assertEquals(expect2, copyBuf2.byteBuf());
    }

    @Test
    void setAndGet_atIndex() {
        PegasusBuffer buffer = createBuffer(32);

        // assert get value at index
        buffer.setByte(5, (byte) 1);
        assertEquals(1, buffer.getByte(5));
        assertEquals(1, buffer.getUnsignedByte(5));

        buffer.setShort(6, (short) 100);
        assertEquals(100, buffer.getShort(6));
        assertEquals(100, buffer.getUnsignedShort(6));

        buffer.setInt(6, 200);
        assertEquals(200, buffer.getInt(6));
        assertEquals(200, buffer.getUnsignedInt(6));

        buffer.setLong(7, 300_000);
        assertEquals(300_000, buffer.getLong(7));

        buffer.setFloat(8, 123.456f);
        assertEquals(123.456f, buffer.getFloat(8));

        buffer.setDouble(9, 2323.3434);
        assertEquals(2323.3434, buffer.getDouble(9));

        byte[] bytes = createBytes(10);
        buffer.setBytes(1, bytes);

        // get bytes array
        {

            assertThrows(IndexOutOfBoundsException.class, () -> {
                byte[] bs = new byte[100];
                buffer.getBytes(1, bs);
            });
            assertThrows(IndexOutOfBoundsException.class, () -> {
                byte[] bs = new byte[10];
                buffer.getBytes(-1, bs);
            });

            byte[] retBytes = new byte[10];
            buffer.getBytes(1, retBytes);
            assertTrue(BytesUtil.equals(bytes, retBytes));
            buffer.getBytes(3, retBytes, 4, 4);
            assertTrue(BytesUtil.equals(
                    BytesUtil.subseq(bytes, 2, 4),
                    BytesUtil.subseq(retBytes, 4, 4)));
        }

        // get bytes array with index and len
        {
            buffer.setBytes(5, bytes, 4, 3);
            byte[] retBytes = new byte[3];
            buffer.getBytes(5, retBytes);
            assertTrue(BytesUtil.equals(BytesUtil.subseq(bytes, 4, 3), retBytes));

            assertThrows(IndexOutOfBoundsException.class, () -> {
                byte[] bs = new byte[2];
                buffer.getBytes(-1, bs, 0, 2);
            });
            assertThrows(IndexOutOfBoundsException.class, () -> {
                byte[] bs = new byte[2];
                buffer.getBytes(100, bs, 0, 2);
            });
            assertThrows(IndexOutOfBoundsException.class, () -> {
                byte[] bs = new byte[2];
                buffer.getBytes(1, bs, 1, 2);
            });
            assertThrows(IndexOutOfBoundsException.class, () -> {
                byte[] bs = new byte[2];
                buffer.getBytes(1, bs, -1, 2);
            });
        }

        // get PegasusBuffer
        {
            assertThrows(IndexOutOfBoundsException.class, () -> {
                PegasusBuffer ret = createEmptyBuffer(32);
                buffer.getBytes(-1, ret);
            });
            assertThrows(IndexOutOfBoundsException.class, () -> {
                PegasusBuffer ret = createEmptyBuffer(64);
                buffer.getBytes(1, ret);
            });

            PegasusBuffer ret1 = createEmptyBuffer(32);
            buffer.getBytes(0, ret1);
            assertEquals(buffer.byteBuf(), ret1.byteBuf());

            PegasusBuffer ret2 = createEmptyBuffer(12);
            buffer.getBytes(10, ret2);
            assertEquals(buffer.byteBuf().slice(10, 12), ret2.byteBuf());
        }

        // get PegasusBuffer with index and len
        {
            assertThrows(IndexOutOfBoundsException.class, () -> {
                PegasusBuffer ret = createEmptyBuffer(32);
                buffer.getBytes(-1, ret, 10);
            });
            assertThrows(IndexOutOfBoundsException.class, () -> {
                PegasusBuffer ret = createEmptyBuffer(32);
                buffer.getBytes(1, ret, 50);
            });
            assertThrows(IndexOutOfBoundsException.class, () -> {
                PegasusBuffer ret = createEmptyBuffer(32);
                buffer.getBytes(20, ret, 20);
            });

            assertThrows(IndexOutOfBoundsException.class, () -> {
                PegasusBuffer ret = createEmptyBuffer(16);
                buffer.getBytes(-1, ret, 1, 10);
            });
            assertThrows(IndexOutOfBoundsException.class, () -> {
                PegasusBuffer ret = createEmptyBuffer(16);
                buffer.getBytes(30, ret, 1, 10);
            });
            assertThrows(IndexOutOfBoundsException.class, () -> {
                PegasusBuffer ret = createEmptyBuffer(16);
                buffer.getBytes(1, ret, 10, 10);
            });
            assertThrows(IndexOutOfBoundsException.class, () -> {
                PegasusBuffer ret = createEmptyBuffer(16);
                buffer.getBytes(1, ret, 1, 20);
            });

            PegasusBuffer ret1 = createEmptyBuffer(10);
            buffer.getBytes(10, ret1, 10);
            assertEquals(buffer.byteBuf().slice(10, 10), ret1.byteBuf());

            PegasusBuffer ret2 = createEmptyBuffer(10);
            buffer.getBytes(10, ret2, 2, 6);
            assertEquals(buffer.byteBuf().slice(10, 6), ret2.byteBuf().slice(2, 6));
        }
    }

}