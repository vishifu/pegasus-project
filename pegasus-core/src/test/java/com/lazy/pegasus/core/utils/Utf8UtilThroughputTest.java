package com.lazy.pegasus.core.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class Utf8UtilThroughputTest {
    private static final String str = "abcdef&^*&!^ghijkl\uB5E2\uCAC7\uB2BB\uB7DD\uB7C7\uB3A3\uBCE4\uB5A5"
            + "abcdef&^*&!^ghijkl\uB5E2\uCAC7\uB2BB\uB7DD\uB7C7\uB3A3\uBCE4\uB5A5"
            + "abcdef&^*&!^ghijkl\uB5E2\uCAC7\uB2BB\uB7DD\uB7C7\uB3A3\uBCE4\uB5A5"
            + "abcdef&^*&!^ghijkl\uB5E2\uCAC7\uB2BB\uB7DD\uB7C7\uB3A3\uBCE4\uB5A5"
            + "abcdef&^*&!^ghijkl\uB5E2\uCAC7\uB2BB\uB7DD\uB7C7\uB3A3\uBCE4\uB5A5"
            + "abcdef&^*&!^ghijkl\uB5E2\uCAC7\uB2BB\uB7DD\uB7C7\uB3A3\uBCE4\uB5A5"
            + "abcdef&^*&!^ghijkl\uB5E2\uCAC7\uB2BB\uB7DD\uB7C7\uB3A3\uBCE4\uB5A5"
            + "abcdef&^*&!^ghijkl\uB5E2\uCAC7\uB2BB\uB7DD\uB7C7\uB3A3\uBCE4\uB5A5"
            + "abcdef&^*&!^ghijkl\uB5E2\uCAC7\uB2BB\uB7DD\uB7C7\uB3A3\uBCE4\uB5A5";

    final int TIMES = 5;
    final int iters = 1_000_000;
    volatile Object blackHole;

    @AfterEach
    void tearDown() {
        Utf8Util.clearLocalBuffer();
    }

    @Test
    void writeUtf() {
        ByteBuf nettyBuf = Unpooled.buffer(10 * 1024, 10 * 1024);
        for (int i = 0; i < TIMES; i++) {
            final long startAt = System.nanoTime();
            for (int j = 0; j < iters; j++) {
                nettyBuf.clear();
                Utf8Util.saveUtf(nettyBuf, str);
                blackHole = nettyBuf;
            }
            final long elapsed = System.nanoTime() - startAt;
            final long elapsedMs = (long) (elapsed / 1e6);

            print("UTF-8 write fork: " + (i + 1));
            print("UTF-8 write elapsed time = " + elapsedMs + " ms");
            print("UTF-8 write throughput = " + (iters / elapsedMs) + " ops/ms");
        }
    }

    @Test
    void readUtf() {
        ByteBuf nettBuf = Unpooled.buffer(10 * 1024, 10 * 1024);
        Utf8Util.saveUtf(nettBuf, str);

        for (int i = 0; i < TIMES; i++) {
            final long startAt = System.nanoTime();
            for (int j = 0; j < iters; j++) {
                nettBuf.resetReaderIndex();
                String retStr = Utf8Util.readUtf8(nettBuf);
                blackHole = retStr;
            }
            final long elapsed = System.nanoTime() - startAt;
            final long elapsedMs = (long) (elapsed / 1e6);

            print("UTF-8 read fork: " + (i + 1));
            print("UTF-8 read elapsed time = " + elapsedMs + "ms");
            print("UTF-8 read throughput = " + (iters / elapsedMs) + " ops/ms");
        }
    }

    private void print(String text) {
        System.out.println(text);
    }
}
