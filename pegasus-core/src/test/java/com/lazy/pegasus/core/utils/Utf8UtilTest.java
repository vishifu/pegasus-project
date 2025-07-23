package com.lazy.pegasus.core.utils;

import com.lazy.pegasus.core.common.DataConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class Utf8UtilTest {

    @AfterEach
    void tearDown() {
        Utf8Util.clearLocalBuffer();
    }

    @Test
    void validateUtfWithEndChars() {
        verifyUtfWithChars(1024, (char) 0);
    }

    @Test
    void validateUtfWithLastAsciiChars() {
        verifyUtfWithChars(1024, (char) Byte.MAX_VALUE);
    }

    @Test
    void validateUtf() {
        ByteBuf buffer = Unpooled.buffer(60 * 1024);
        byte[] bytes = new byte[20_000];

        RandsUtil.getRands().nextBytes(bytes);
        String str = new String(bytes);

        Utf8Util.saveUtf(buffer, str);

        String retStr = Utf8Util.readUtf8(buffer);
        assertEquals(str, retStr);
    }

    @Test
    void validateUtfOnDataInput() throws IOException {
        for (int i = 0; i < 100; i++) {
            byte[] bytes = new byte[15_000 + RandsUtil.randPositiveInt() % 5_000];
            RandsUtil.getRands().nextBytes(bytes);

            String str = new String(bytes);

            validateUtfOnInputStream(str, Unpooled.wrappedBuffer(ByteBuffer.allocate(str.length() * 3 + DataConstants.SIZE_SHORT)));
            validateUtfOnInputStream(str, Unpooled.buffer(100));
            validateUtfOnInputStream(str, Unpooled.buffer(100 * 1024, 100 * 1024));
        }
    }

    @Test
    void validateBigSize() {
        // testing on overflow limit
        char[] chars = new char[0xffff + 1];
        Arrays.fill(chars, ' ');

        String str = new String(chars);
        ByteBuf nettyBuf = Unpooled.buffer(0xffff + 4, 0xffff + 4);
        try {
            Utf8Util.saveUtf(nettyBuf, str);
            fail("String is too big, expected to throw an exception");
        } catch (Exception e) {
        }

        assertEquals(0, nettyBuf.writerIndex());

        // testing on the right limit
        chars = new char[0xffff];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (i % 100 + 1);
        }

        str = new String(chars);
        Utf8Util.saveUtf(nettyBuf, str);
        assertEquals(0xffff + DataConstants.SIZE_SHORT, nettyBuf.writerIndex());
        String retStr = Utf8Util.readUtf8(nettyBuf);
        assertEquals(str, retStr);
    }

    private void validateUtfOnInputStream(final String str, ByteBuf buffer) throws IOException {
        buffer.clear();
        Utf8Util.saveUtf(buffer, str);

        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(buffer.nioBuffer().array()));
        String newStr = inputStream.readUTF();
        assertEquals(str, newStr);

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(bytesOut);

        outputStream.writeUTF(str);
        ByteBuf buf = Unpooled.wrappedBuffer(bytesOut.toByteArray());
        newStr = Utf8Util.readUtf8(buf);
        assertEquals(str, newStr);
    }

    private void verifyUtfWithChars(final int size, final char c) {
        final char[] chars = new char[size];
        Arrays.fill(chars, c);

        final String expectedUtf = new String(chars);
        final ByteBuf nettyBuf = Unpooled.buffer(4 * chars.length);

        Utf8Util.saveUtf(nettyBuf, expectedUtf);

        final byte[] expectedBytes = expectedUtf.getBytes(StandardCharsets.UTF_8);
        final int encodeSize = nettyBuf.readUnsignedShort();
        final byte[] readEncodeBytes = new byte[encodeSize];
        nettyBuf.getBytes(nettyBuf.readerIndex(), readEncodeBytes);

        assertArrayEquals(expectedBytes, readEncodeBytes);
    }

}