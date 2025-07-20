package com.lazy.pegasus.core.common;


import com.lazy.pegasus.core.utils.RandsUtil;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@State(Scope.Benchmark)
@Fork(2)
@Threads(value = 2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.MICROSECONDS)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class SpanStringBenchmark {

    // TODO: Could check with real logic (serialization/data transmission) in app?
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SpanStringBenchmark.class.getSimpleName())
                .addProfiler(GCProfiler.class)
                .build();

        new Runner(opt).run();
    }

    private String message;
    private SpanString spanMessage;

    @Setup
    public void setup() {
        byte[] bytes = RandsUtil.randBytes(16634);
        message = new String(bytes, StandardCharsets.UTF_8);
        spanMessage = SpanString.of(bytes);
    }

    // === SERIALIZATION BENCHMARKS ===

    @Benchmark
    public byte[] serializeMessage() throws IOException {
        return serializeString(message);
    }

    @Benchmark
    public byte[] serializeSpanMessage() throws IOException {
        return serializeSpanString(spanMessage);
    }

    // === CREATION + SERIALIZATION BENCHMARKS ===

    @Benchmark
    public byte[] createAndSerializeMessage() throws IOException {
        String msg = new String("This is typical message from queue");
        return serializeString(msg);
    }

    @Benchmark
    public byte[] createAndSerializeSpanMessage() throws IOException {
        SpanString msg = SpanString.of("This is typical message from queue");
        return serializeSpanString(msg);
    }

    /**
     * Simulates how a String would be serialized in ActiveMQ Artemis
     * This involves UTF-8 encoding which creates temporary byte arrays
     */
    private byte[] serializeString(String str) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Simulate string serialization
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(bytes.length);  // Write length first
        dos.write(bytes);           // Write UTF-8 bytes

        dos.close();
        return baos.toByteArray();
    }

    /**
     * Simulates how a SimpleString would be serialized in ActiveMQ Artemis
     * Direct byte array access, no encoding conversion
     */
    private byte[] serializeSpanString(SpanString spanString) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // SpanString can write directly to stream without conversion
        byte[] bytes = spanString.getData(); // Direct access to internal byte array
        dos.writeInt(bytes.length);          // Write length first
        dos.write(bytes);                   // Write bytes directly

        dos.close();
        return baos.toByteArray();
    }
}
