package com.lazy.pegasus.core.common;

import com.lazy.pegasus.core.utils.BytesUtil;
import io.netty.buffer.ByteBuf;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * This class can be used for heavily throughout-put as String, it stores string as a simple {@code byte[]}, this can
 * minimize the cost of copying between String objects.
 */
public final class SpanString implements CharSequence, Serializable, Comparable<SpanString> {

    @Serial
    private static final long serialVersionUID = 5107791929712364362L;

    private static final SpanString EMPTY = new SpanString("");

    private final byte[] data;

    private transient int hash;
    /* cache the string */
    private transient String str;
    private transient String[] paths;

    /**
     * Creates a {@link SpanString} constructed from {@code s} parameter.
     *
     * @param s initial string.
     * @return newly SpanString.
     */
    public static SpanString of(final String s) {
        if (s == null || s.isEmpty()) {
            return EMPTY;
        }

        return new SpanString(s);
    }

    /**
     * Creates an interned {@link SpanString} constructed from string parameter via a pool, if possible.
     *
     * @param s    initial string
     * @param pool string pool
     * @return newly interned {@link SpanString} from initial string
     */
    public static SpanString of(final String s, SpanStringStringPool pool) {
        if (pool == null) {
            return SpanString.of(s);
        }

        return pool.getOrCreate(s);
    }

    /**
     * Creates a {@link SpanString} constructed from a character.
     *
     * @param c initial character.
     * @return newly SpanString.
     */
    public static SpanString of(final char c) {
        return new SpanString(c);
    }

    /**
     * Creates a {@link SpanString} from byte array source.
     *
     * @param data initial byte array source.
     * @return newly SpanString.
     */
    public static SpanString of(final byte[] data) {
        return new SpanString(data);
    }

    /**
     * @param str string to check.
     * @return the size of SpanString.
     */
    public static int sizeOfStr(final SpanString str) {
        return str.sizeof();
    }

    /**
     * @param str string to check.
     * @return the size of SpanString, which could be null.
     */
    public static int sizeOfNullableStr(final SpanString str) {
        if (str == null) {
            return 1;
        }
        return 1 + sizeOfStr(str);
    }

    private SpanString(final String s) {
        int len = s.length();
        this.data = new byte[len << 1];

        int j = 0;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            byte lo = (byte) (c & 0xff);
            this.data[j++] = lo;

            byte hi = (byte) ((c >> 8) & 0xff);
            this.data[j++] = hi;
        }

        this.str = s; // caching
    }

    private SpanString(final char c) {
        this.data = new byte[2];
        this.data[0] = (byte) (c & 0xff);
        this.data[1] = (byte) ((c >> 8) & 0xff);
    }

    private SpanString(final byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public boolean isBlank() {
        boolean check = true;
        for (int i = 0; i < length(); i++) {
            if (!Character.isWhitespace(charAt(i))) {
                check = false;
                break;
            }
        }

        return check;
    }

    /**
     * @return size of this SpanString
     */
    public int sizeof() {
        return DataConstants.SIZE_INT + this.data.length;
    }

    @Override
    public boolean isEmpty() {
        return this.data.length == 0;
    }

    @Override
    public int length() {
        return this.data.length >> 1;
    }

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= length()) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bound [0," + length() + ']');
        }

        index <<= 1;
        return (char) ((this.data[index] & 0xff) | ((this.data[index + 1] << 8) & 0xff00));
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return subSeq(start, end);
    }

    @Override
    public int compareTo(SpanString o) {
        Objects.requireNonNull(o, "SpanString must not be null");
        return toString().compareTo(o.toString());
    }

    /**
     * Subsequences the current string of range [start, end].
     *
     * @param start start index of source.
     * @param end   end index of source.
     * @return a newly copied SpanString of source.
     */
    public SpanString subSeq(final int start, final int end) {
        int len = length();
        if (end < start || start < 0 || end > len) {
            throw new IndexOutOfBoundsException("Sequence [" + start + "," + end + "] out of bound [0," + len + "]");
        }

        int copies = (end - start) << 1;
        byte[] dest = new byte[copies];
        System.arraycopy(this.data, start << 1, dest, 0, copies);

        return SpanString.of(dest);
    }

    /**
     * Reads source string from in range [start,end] into a char array destination.
     *
     * @param srcStart start index.
     * @param srcEnd   end index.
     * @param dest     destination array.
     * @param destPos  start to write of destination.
     */
    public void readChars(final int srcStart, final int srcEnd, final char[] dest, final int destPos) {
        if (srcEnd > length()) {
            throw new IndexOutOfBoundsException("Index end " + srcEnd + " is out of bound [0," + length() + "]");
        }
        if (srcStart < 0 || srcStart > srcEnd) {
            throw new IndexOutOfBoundsException("Index start " + srcStart + " is out of bound");
        }

        int j = srcStart << 1;
        int d = destPos;
        for (int i = srcStart; i < srcEnd; i++) {
            int lo = this.data[j++] & 0xff;
            int hi = (this.data[j++] << 8) & 0xff00;
            dest[d++] = (char) (lo | hi);
        }
    }

    /**
     * Tests whether string contains a single character
     *
     * @param c character to check.
     * @return true if string contains test character, otherwise false,
     */
    public boolean contains(final char c) {
        if (this.str != null) {
            return this.str.indexOf(c) != -1;
        }

        final byte lo = (byte) (c & 0xff);
        final byte hi = (byte) ((c >> 8) & 0xff);

        for (int i = 0; i + 1 < this.data.length; i += 2) {
            if (this.data[i] == lo && this.data[i + 1] == hi) {
                return true;
            }
        }

        return false;
    }

    /**
     * Tests whether this source starts with another SpanString at beginning.
     *
     * @param compare other to check.
     * @return true if this source starts with check SpanString, otherwise false.
     */
    public boolean startsWith(final SpanString compare) {
        byte[] otherData = compare.getData();
        if (otherData.length > this.data.length) {
            return false;
        }

        for (int i = 0; i < otherData.length; i++) {
            if (this.data[i] != otherData[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Concatenates a SpanString with a String.
     *
     * @param add append String.
     * @return newly constructed SpanString that is concat of this an append string.
     */
    public SpanString concat(final String add) {
        int toAddLen = add.length();
        byte[] buf = new byte[this.data.length + toAddLen * 2];
        System.arraycopy(this.data, 0, buf, 0, this.data.length);
        for (int i = 0; i < toAddLen; i++) {
            char c = add.charAt(i);
            int offset = this.data.length + i * 2;
            buf[offset] = (byte) (c & 0xff);
            buf[offset + 1] = (byte) ((c >> 8) & 0xff);
        }

        return SpanString.of(buf);
    }

    /**
     * Concatenates a SpanString with another SpanString.
     *
     * @param add append SpanString.
     * @return newly constructed SpanString that is concat of this and append one.
     */
    public SpanString concat(final SpanString add) {
        byte[] buf = new byte[this.data.length + add.getData().length];
        System.arraycopy(this.data, 0, buf, 0, this.data.length);
        System.arraycopy(add.getData(), 0, buf, this.data.length, add.getData().length);

        return SpanString.of(buf);
    }

    /**
     * Concatenates a SpanString with another char.
     *
     * @param c append char.
     * @return newly constructed SpanString that is concat of this and append char.
     */
    public SpanString concat(final char c) {
        byte[] buf = new byte[this.data.length + 2];
        System.arraycopy(this.data, 0, buf, 0, this.data.length);
        buf[this.data.length] = (byte) (c & 0xff);
        buf[this.data.length + 1] = (byte) ((c >> 8) & 0xff);

        return SpanString.of(buf);
    }

    /**
     * Gets the result of split paths from this source. Note that this action will cache paths in the first time call,
     * in following call, the cache result is used.
     *
     * @param separator separator path char.
     * @return an array of String of split paths.
     */
    public String[] getPaths(final char separator) {
        if (this.paths != null) {
            return this.paths;
        }

        List<String> list = new ArrayList<>();
        StringBuilder accumulator = new StringBuilder();
        for (char c : toString().toCharArray()) {
            if (c == separator) {
                list.add(accumulator.toString());
                accumulator.delete(0, accumulator.length()); // reset accumulator
            } else {
                accumulator.append(c);
            }
        }

        list.add(accumulator.toString());
        this.paths = list.toArray(new String[0]);
        return this.paths;
    }

    public SpanString[] split(final char delim) {
        if (this.str == null) {
            return splitNonCachedStr(this, delim);
        }

        return splitCachedStr(this, delim);
    }

    private static SpanString[] splitNonCachedStr(final SpanString s, final char delim) {
        List<SpanString> list = null;
        byte lo = (byte) (delim & 0xff);
        byte hi = (byte) ((delim >> 8) & 0xff);

        int lastPos = 0;
        for (int i = 0; i + 1 < s.data.length; i += 2) {
            if (s.data[i] == lo && s.data[i + 1] == hi) {
                byte[] buf = new byte[i - lastPos];
                System.arraycopy(s.data, lastPos, buf, 0, buf.length);
                lastPos = i + 2;

                if (list == null) {
                    list = new ArrayList<>(2);
                }
                list.add(SpanString.of(buf));
            }
        }

        if (list == null) {
            return new SpanString[]{s};
        }

        byte[] buf = new byte[s.data.length - lastPos];
        System.arraycopy(s.data, lastPos, buf, 0, buf.length);
        list.add(SpanString.of(buf));

        SpanString[] parts = new SpanString[list.size()];
        return list.toArray(parts);
    }

    private static SpanString[] splitCachedStr(final SpanString s, final char delim) {
        final String str = s.str;
        final byte[] data = s.getData();
        final int len = str.length();
        List<SpanString> list = null;

        int j = 0;
        while (j < len) {
            final int delimIndex = str.indexOf(delim, j);
            if (delimIndex == -1) {
                break;
            }

            list = addSpanStringPart(list, data, j, delimIndex);
            j = delimIndex + 1;
        }

        if (list == null) {
            return new SpanString[]{s};
        }

        list = addSpanStringPart(list, data, j, len);
        final SpanString[] parts = new SpanString[list.size()];
        return list.toArray(parts);
    }

    private static List<SpanString> addSpanStringPart(List<SpanString> list, final byte[] data, final int start, final int end) {
        final int expect = end - start;
        final SpanString s;
        if (expect == 0) {
            s = EMPTY;
        } else {
            final int sIndex = start << 1;
            final int delIndex = end << 1;
            final byte[] bytes = Arrays.copyOfRange(data, sIndex, delIndex);
            s = SpanString.of(bytes);
        }

        if (list == null) {
            list = new ArrayList<>(3);
        }

        list.add(s);
        return list;
    }

    public boolean equals(final ByteBuf buf, final int offset, final int len) {
        return BytesUtil.equals(this.data, buf, offset, len);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SpanString other)) {
            return false;
        }

        return BytesUtil.equals(this.data, other.data);
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            int tmp = 0;
            for (byte b : this.data) {
                tmp = (tmp << 5) - tmp + b;
            }
            hash = tmp;
        }

        return hash;
    }

    @Override
    public String toString() {
        if (this.str == null) {
            // cache is null, do cache
            int len = length();
            char[] chars = new char[len];
            int j = 0;
            for (int i = 0; i < len; i++) {
                int lo = data[j++] & 0xff;
                int hi = (data[j++] << 8) & 0xff00;
                chars[i] = (char) (lo | hi);
            }

            this.str = new String(chars);
        }

        return this.str;
    }

}
