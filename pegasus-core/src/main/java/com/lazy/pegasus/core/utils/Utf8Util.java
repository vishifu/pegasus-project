package com.lazy.pegasus.core.utils;

import com.lazy.pegasus.core.common.DataConstants;
import com.lazy.pegasus.core.common.SpanString;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.SoftReference;
import java.util.Objects;

/**
 * Collections of utility methods for UTF-8 String
 */
public class Utf8Util {

    private static final Logger log = LoggerFactory.getLogger(Utf8Util.class);

    private static final ThreadLocal<SoftReference<AdaptedBuffer>> localBuffer = new ThreadLocal<>();

    public static String readUtf8(final ByteBuf input) {
        Objects.requireNonNull(input, "input must not be null");

        AdaptedBuffer currBuf = getThreadLocalBuffer0();
        final int size = input.readUnsignedShort();

        log.trace("reading string with utf_size={}", size);
        if (PlatformDependent.hasUnsafe() && input.hasMemoryAddress()) {
            final long address = input.memoryAddress();
            final int index = input.readerIndex();

            input.skipBytes(size);
            final char[] chars = currBuf.borrowCharBuffer(size);

            return unsafeOffHeapReadUtf0(address, index, chars, size);
        }

        final byte[] bytes;
        final int index;
        if (input.hasArray()) {
            bytes = input.array();
            index = input.arrayOffset() + input.readerIndex();
            input.skipBytes(size);
        } else {
            bytes = currBuf.borrowByteBuffer(size);
            index = 0;
            input.readBytes(bytes, 0, size);
        }

        final char[] chars = currBuf.borrowCharBuffer(size);
        if (PlatformDependent.hasUnsafe()) {
            return unsafeOnHeapReadUtf0(bytes, index, chars, size);
        }

        return readUtf0(bytes, index, chars, size);
    }

    public static void saveUtf(final ByteBuf buffer, final String str) {
        if (str.length() > 0xffff) {
            throw new IllegalArgumentException("String is too long to write as UTF, len=" + str.length());
        }

        final int len = calcUtfSize(str);
        if (len > 0xffff) {
            throw new IllegalArgumentException("String is too log to write as UTF, len=" + len);
        }

        log.trace("saving string with utf_size={}, str_size={}", len, str.length());
        buffer.writeShort(len);

        if (buffer.hasArray()) {
            buffer.ensureWritable(len);
            final byte[] out = buffer.array();
            final int writerIdx = buffer.writerIndex();
            final int i = buffer.arrayOffset() + writerIdx;

            if (PlatformDependent.hasUnsafe()) {
                unsafeOnHeapWriteUtf0(str, out, i, str.length());
            } else {
                writeUtf0(str, out, i, str.length());
            }

            buffer.writerIndex(writerIdx + len);
        } else {
            if (PlatformDependent.hasUnsafe() && buffer.hasMemoryAddress()) {
                buffer.ensureWritable(len);
                final long address = buffer.memoryAddress();
                final int writeIdx = buffer.writerIndex();
                unsafeOffHeapWriteUtf0(str, address, writeIdx, len);
                buffer.writerIndex(writeIdx + len);
            } else {
                final byte[] out = getThreadLocalBuffer0().borrowByteBuffer(len);
                writeUtf0(str, out, 0, str.length());
                buffer.writeBytes(out, 0, len);
            }
        }
    }

    public static void writeString(final ByteBuf buffer, final String src) {
        int len = src.length();
        buffer.writeInt(len);

        if (len < 9) {
            writeAsShort(buffer, src);
        } else if (len < 0xfff) {
            saveUtf(buffer, src);
        } else {
            SpanString.writeSpanString(buffer, SpanString.of(src));
        }
    }

    public static void writeNullableString(final ByteBuf buffer, final String src) {
        if (src == null) {
            buffer.writeByte(DataConstants.NULL);
        } else {
            buffer.writeByte(DataConstants.NOT_NULL);
            writeString(buffer, src);
        }
    }

    public static void clearLocalBuffer() {
        SoftReference<AdaptedBuffer> ref = localBuffer.get();
        if (ref != null && ref.get() != null) {
            ref.clear();
        }
    }

    private static void writeAsShort(final ByteBuf buffer, final String s) {
        for (int i = 0; i < s.length(); i++) {
            buffer.writeShort((short) s.charAt(i));
        }
    }

    private static int calcUtfSize(final String str) {
        int len = 0;
        for (int i = 0, strLen = str.length(); i < strLen; i++) {
            final char c = str.charAt(i);
            if (c <= 0x7f) {
                len++;
            } else if (c >= 0x800) {
                len += 3;
            } else {
                len += 2;
            }
        }

        return len;
    }

    private static int unsafeOffHeapWriteUtf0(final CharSequence str,
                                              final long address,
                                              final int index,
                                              final int len) {
        int p = index;
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (c <= 0x7f) {
                PlatformDependent.putByte(address + p++, (byte) c);
            } else if (c >= 0x800) {
                PlatformDependent.putByte(address + p++, (byte) (0xe0 | c >> 12 & 0x0f));
                PlatformDependent.putByte(address + p++, (byte) (0x80 | c >> 6 & 0x3f));
                PlatformDependent.putByte(address + p++, (byte) (0x80 | c & 0x3f));
            } else {
                PlatformDependent.putByte(address + p++, (byte) (0xc0 | c >> 6 & 0x1f));
                PlatformDependent.putByte(address + p++, (byte) (0x08 | c & 0x3f));
            }
        }

        return (p - index);
    }

    private static int unsafeOnHeapWriteUtf0(final CharSequence str,
                                             final byte[] out,
                                             final int index,
                                             final int len) {
        int p = index;
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (c <= 0x7f) {
                PlatformDependent.putByte(out, p++, (byte) c);
            } else if (c >= 0x800) {
                PlatformDependent.putByte(out, p++, (byte) (0xe0 | (c >> 12 & 0x0f)));
                PlatformDependent.putByte(out, p++, (byte) (0x80 | (c >> 6 & 0x3f)));
                PlatformDependent.putByte(out, p++, (byte) (0x80 | (c & 0x3f)));
            } else {
                PlatformDependent.putByte(out, p++, (byte) (0xc0 | c >> 6 & 0x1f));
                PlatformDependent.putByte(out, p++, (byte) (0x80 | c & 0x3f));
            }
        }

        return (p - index);
    }

    private static int writeUtf0(final CharSequence str,
                                 final byte[] out,
                                 final int index,
                                 final int len) {
        int p = index;
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (c <= 0x7f) {
                out[p++] = (byte) c;
            } else if (c >= 0x800) {
                out[p++] = (byte) (0xe0 | c >> 12 & 0x0f);
                out[p++] = (byte) (0x80 | c >> 6 & 0x3f);
                out[p++] = (byte) (0x80 | c & 0x3f);
            } else {
                out[p++] = (byte) (0xc0 | c >> 6 & 0x1f);
                out[p++] = (byte) (0x80 | c & 0x3f);
            }
        }

        return (p - index);
    }

    private static String readUtf0(final byte[] bytes,
                                   final int index,
                                   final char[] chars,
                                   final int size) {
        int i = index;
        final int lim = index + size;
        int byte1, byte2, byte3;
        int charCount = 0;

        while (i < lim) {
            byte1 = bytes[i++];
            if (byte1 >= 0 && byte1 <= 0x7f) {
                chars[charCount++] = (char) byte1;
            } else {
                int c = byte1 & 0xff;
                switch (c >> 4) {
                    case 0xc:
                    case 0xd:
                        byte2 = bytes[i++];
                        chars[charCount++] = (char) ((c & 0x1f) << 6 | (byte2 & 0x3f));
                        break;
                    case 0xe:
                        byte2 = bytes[i++];
                        byte3 = bytes[i++];
                        chars[charCount++] = (char) ((c & 0x0f) << 12 | (byte2 & 0x3f) << 6 | (byte3 & 0x3f));
                        break;
                    default:
                        throw new InternalError("Unexpected utf8 byte " + c);
                }
            }
        }

        return new String(chars, 0, charCount);
    }

    private static String unsafeOnHeapReadUtf0(final byte[] bytes,
                                               final int index,
                                               final char[] chars,
                                               final int size) {
        int i = index;
        final int lim = index + size;
        int byte1, byte2, byte3;
        int charCount = 0;

        while (i < lim) {
            byte1 = PlatformDependent.getByte(bytes, i++);
            if (byte1 >= 0 && byte1 <= 0x7f) {
                chars[charCount++] = (char) byte1;
            } else {
                int c = byte1 & 0xff;
                switch (c >> 4) {
                    case 0xc:
                    case 0xd:
                        byte2 = PlatformDependent.getByte(bytes, i++);
                        chars[charCount++] = (char) ((c & 0x1f) << 6 | (byte2 & 0x3f));
                        break;
                    case 0xe:
                        byte2 = PlatformDependent.getByte(bytes, i++);
                        byte3 = PlatformDependent.getByte(bytes, i++);
                        chars[charCount++] = (char) ((c & 0x0f) << 12 | (byte2 & 0x3f) << 6 | (byte3 & 0x3f));
                        break;
                    default:
                        throw new InternalError("Unexpected utf byte " + c);
                }
            }
        }

        return new String(chars, 0, charCount);
    }

    private static String unsafeOffHeapReadUtf0(final long address,
                                                final int index,
                                                final char[] chars,
                                                final int size) {
        int i = index;
        final int lim = index + size;
        int byte1, byte2, byte3;
        int charCount = 0;

        while (i < lim) {
            byte1 = PlatformDependent.getByte(address + i++);
            if (byte1 >= 0 && byte1 <= 0x7f) {
                chars[charCount++] = (char) byte1;
            } else {
                int c = byte1 & 0xff;
                switch (c >> 4) {
                    case 0xc:
                    case 0xd:
                        byte2 = PlatformDependent.getByte(address + i++);
                        chars[charCount++] = (char) ((c & 0x1f) << 6 | byte2 & 0x3f);
                        break;
                    case 0xe:
                        byte2 = PlatformDependent.getByte(address + i++);
                        byte3 = PlatformDependent.getByte(address + i++);
                        chars[charCount++] = (char) ((c & 0x0f) << 12 | (byte2 & 0x3f) << 6 | (byte3 & 0x3f));
                        break;
                    default:
                        throw new InternalError("Unexpected utf-8 byte " + c);
                }
            }
        }

        return new String(chars, 0, charCount);
    }

    private static AdaptedBuffer getThreadLocalBuffer0() {
        SoftReference<AdaptedBuffer> softReference = localBuffer.get();
        AdaptedBuffer valBuffer;
        if (softReference == null) {
            valBuffer = new AdaptedBuffer();
            softReference = new SoftReference<>(valBuffer);
            localBuffer.set(softReference);
        } else {
            valBuffer = softReference.get();
        }

        if (valBuffer == null) {
            valBuffer = new AdaptedBuffer();
            softReference = new SoftReference<>(valBuffer);
            localBuffer.set(softReference);
        }

        return valBuffer;
    }

    private static final class AdaptedBuffer {

        private char[] charBuffer = null;
        private byte[] byteBuffer = null;

        char[] borrowCharBuffer(final int size) {
            if (charBuffer == null || size > charBuffer.length) {
                charBuffer = new char[size];
            }
            return charBuffer;
        }

        byte[] borrowByteBuffer(final int size) {
            if (byteBuffer == null || size > byteBuffer.length) {
                byteBuffer = new byte[size];
            }

            return byteBuffer;
        }
    }

}
