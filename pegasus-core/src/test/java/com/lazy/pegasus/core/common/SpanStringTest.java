package com.lazy.pegasus.core.common;

import com.lazy.pegasus.core.utils.RandsUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpanStringTest {

    @Test
    void readChars() {
        for (int i = 0; i < (1 << 16); i++) {
            char c = (char) i;
            SpanString s1 = create(c);

            char[] c1 = new char[1];
            s1.readChars(0, 1, c1, 0);
            assertEquals(c, c1[0], "expecting " + i);
            assertEquals(c, s1.charAt(0), "expecting " + i);

            SpanString s2 = s1.concat(c);
            assertEquals(c, s2.charAt(1), "expecting " + i);
        }
    }

    @Test
    void splits() {
        for (int i = 0; i < (1 << 16); i++) {
            char c = (char) i;
            SpanString s = create("foo" + c + "bar");
            SpanString[] chunks = s.split(c);
            SpanString[] split1 = create("foo").split(c);
            SpanString[] split2 = create("bar").split(c);

            assertEquals(split1.length + split2.length, chunks.length);
            int j = 0;
            for (SpanString ss : split1) {
                assertEquals(ss, chunks[j++]);
            }
            for (SpanString ss : split2) {
                assertEquals(ss, chunks[j++]);
            }
        }

        SpanString s = create("abcdef12345");
        SpanString[] splits = s.split('.');
        assertEquals(1, splits.length);
        assertEquals(s, splits[0]);
    }

    @Test
    void string() {
        final String str = "helloworld123ABC_`xyz`!%20%%!$!$!$%%\uA324";
        SpanString s = create(str);
        assertEquals(str, s.toString());
        assertEquals(2 * str.length(), s.getData().length);

        byte[] data = s.getData();
        SpanString sother = create(data);
        assertEquals(str, sother.toString());
    }

    @Test
    void startsWith() {
        SpanString s = create("abcdef12345");
        assertTrue(s.startsWith(create("abc")));
        assertTrue(s.startsWith(create("abcdef")));
        assertTrue(s.startsWith(create("abcdef12345")));
        assertFalse(s.startsWith(create("abdef")));
        assertFalse(s.startsWith(create("abcde123")));
        assertFalse(s.startsWith(create("2")));
    }

    @Test
    void charSequence() {
        String str = "abcdefghi";
        SpanString s = create(str);

        // checking each char
        assertEquals('a', s.charAt(0));
        assertEquals('b', s.charAt(1));
        assertEquals('c', s.charAt(2));
        assertEquals('d', s.charAt(3));
        assertEquals('e', s.charAt(4));
        assertEquals('f', s.charAt(5));
        assertEquals('g', s.charAt(6));
        assertEquals('h', s.charAt(7));
        assertEquals('i', s.charAt(8));

        // checking throws out of bound
        assertThrows(IndexOutOfBoundsException.class, () -> s.charAt(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> s.charAt(str.length()));

        // checking length
        assertEquals(str.length(), s.length());

        // checking subsequence
        CharSequence cs = s.subSequence(0, s.length());
        assertEquals(cs, s);

        cs = s.subSequence(0, 4);
        assertEquals(create("abcd"), cs);

        assertThrows(IndexOutOfBoundsException.class, () -> s.subSequence(-1, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> s.subSequence(4, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> s.subSequence(0, s.length() + 1));
    }

    @Test
    void equals() {
        assertNotEquals(create("abcd"), new Object());
        assertNotEquals(create("abcd"), null);
        assertNotEquals(create("abcd"), create("acdb"));
        assertEquals(create("abcd"), create("abcd"));
    }

    @Test
    void hashcode() {
        SpanString s1 = create("abcdef");
        SpanString s2 = create("abcdef");
        SpanString s3 = create("abczzz");

        assertEquals(s1.hashCode(), s2.hashCode());
        assertNotEquals(s1.hashCode(), s3.hashCode());
    }

    @Test
    void unicode() {
        String str = "abcdef^&^&ghilkmn\uB435\uC244\uC432\uB3A4\uAA33\uD355";

        SpanString s = create(str);
        byte[] data = s.getData();
        s = create(data);

        assertEquals(str, s.toString());
    }

    @Test
    void unicodeWithSurrogates() {
        String str = "abcdef^&^$$\uD900\uDD00";
        SpanString s = create(str);
        byte[] data = s.getData();
        s = create(data);

        assertEquals(str, s.toString());
    }

    @Test
    void contains() {
        SpanString s = create("abcdef12345");
        assertFalse(s.contains('g'));
        assertFalse(s.contains(' '));
        assertFalse(s.contains('.'));

        assertTrue(s.contains('a'));
        assertTrue(s.contains('b'));
        assertTrue(s.contains('c'));
        assertTrue(s.contains('d'));
        assertTrue(s.contains('e'));
        assertTrue(s.contains('f'));
        assertTrue(s.contains('1'));
        assertTrue(s.contains('2'));
        assertTrue(s.contains('3'));
        assertTrue(s.contains('4'));
        assertTrue(s.contains('5'));
    }

    @Test
    void concat() {
        SpanString start = create("abcdef");
        SpanString mid = create("hilkmn");
        SpanString end = create("12345");

        assertEquals(start.concat(mid), create("abcdefhilkmn"));
        assertEquals(start.concat(".").concat(end), create("abcdef.12345"));
    }

    @Test
    void sizeof() {
        assertEquals(DataConstants.SIZE_INT, create("").sizeof());
        assertEquals(DataConstants.SIZE_INT, SpanString.sizeOfStr(create("")));

        SpanString s = create(RandsUtil.randUUID());
        assertEquals(DataConstants.SIZE_INT + s.getData().length, SpanString.sizeOfStr(s));
    }

    @Test
    void sizeofNullable() {
        assertEquals(1, SpanString.sizeOfNullableStr(null));
        assertEquals(1 + DataConstants.SIZE_INT, SpanString.sizeOfNullableStr(create("")));

        SpanString s = create(RandsUtil.randUUID());
        assertEquals(1 + DataConstants.SIZE_INT + s.getData().length, SpanString.sizeOfNullableStr(s));
    }

    private SpanString create(String s) {
        return SpanString.of(s);
    }

    private SpanString create(char c) {
        return SpanString.of(c);
    }

    private SpanString create(byte[] bytes) {
        return SpanString.of(bytes);
    }
}