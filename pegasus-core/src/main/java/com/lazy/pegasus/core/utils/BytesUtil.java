package com.lazy.pegasus.core.utils;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.PlatformDependent;

import java.util.Arrays;

/**
 * Collection of byte utilities
 */
public final class BytesUtil {

    public static byte[] subseq(final byte[] bs, final int from, final int len) {
        return Arrays.copyOfRange(bs, from, from + len);
    }

    public static boolean equals(final byte[] left, final byte[] right) {
        return equals(left, right, 0, right.length);
    }

    public static boolean equals(final byte[] left, final byte[] right, final int rightOffset, final int rightLen) {
        if (left == right) {
            return true;
        }

        if (left == null || right == null) {
            return false;
        }

        if (left.length != rightLen) {
            return false;
        }

        if (PlatformDependent.isUnaligned() && PlatformDependent.hasUnsafe()) {
            return unsafeEquals(left, right, rightOffset, rightLen);
        }

        return safeEquals(left, right, rightOffset, rightLen);
    }

    public static boolean equals(final byte[] left, final ByteBuf buf, final int offset, final int len) {
        if (left.length != len) {
            return false;
        }

        if (PlatformDependent.isUnaligned() && PlatformDependent.hasUnsafe()) {
            if ((offset + len) > buf.writerIndex()) {
                throw new IndexOutOfBoundsException("Index [" + offset + "," + (offset + len) + " is out of bound " +
                        "[0," + buf.writerIndex() + "]");
            }

            if (buf.hasArray()) {
                return equals(left, buf.array(), buf.arrayOffset() + offset, len);
            } else if (buf.hasMemoryAddress()) {
                return equalsOffHeap(left, buf.memoryAddress(), offset, len);
            }
        }

        return equalsOnHeap(left, buf, offset, len);
    }

    private static boolean unsafeEquals(byte[] left, byte[] right, int rightOffset, int rightLen) {
        final int longCount = rightLen >>> 3;
        final int byteCount = rightLen & 7;
        int byteOffset = rightOffset;
        int charIndex = 0;

        for (int i = 0; i < longCount; i++) {
            final long charLong = PlatformDependent.getLong(left, charIndex);
            final long byteLong = PlatformDependent.getLong(right, byteOffset);
            if (charLong != byteLong) {
                return false;
            }

            byteOffset += 8;
            charIndex += 8;
        }
        for (int i = 0; i < byteCount; i++) {
            final byte charLong = PlatformDependent.getByte(left, charIndex);
            final byte byteLong = PlatformDependent.getByte(right, byteOffset);
            if (charLong != byteLong) {
                return false;
            }

            byteOffset++;
            charIndex++;
        }

        return true;
    }

    private static boolean safeEquals(byte[] left, byte[] right, int rightOffset, int rightLen) {
        for (int i = 0; i < rightLen; i++) {
            if (left[i] != right[i]) {
                return false;
            }
        }

        return true;
    }

    private static boolean equalsOnHeap(final byte[] left, final ByteBuf buf, final int offset, final int len) {
        if (left.length != len) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            if (left[i] != buf.getByte(offset + i)) {
                return false;
            }
        }

        return true;
    }

    private static boolean equalsOffHeap(final byte[] left, final long address, final int offset, final int len) {
        final int longCount = len >>> 3;
        final int byteCount = len & 7;

        int charsIndex = 0;
        long bytesAdrr = address + offset;
        for (int i = 0; i < longCount; i++) {
            final long charLong = PlatformDependent.getLong(left, charsIndex);
            final long byteLong = PlatformDependent.getLong(bytesAdrr);
            if (charLong != byteLong) {
                return false;
            }

            charsIndex += 8;
            bytesAdrr += 8;
        }
        for (int i = 0; i < byteCount; i++) {
            final byte charLong = PlatformDependent.getByte(left, charsIndex);
            final byte byteLong = PlatformDependent.getByte(bytesAdrr);
            if (charLong != byteLong) {
                return false;
            }

            charsIndex += 8;
            bytesAdrr += 8;
        }

        return true;
    }
}
