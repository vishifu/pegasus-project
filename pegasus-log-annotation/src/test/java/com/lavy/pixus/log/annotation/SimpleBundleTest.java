package com.lavy.pixus.log.annotation;

import com.lavy.pixus.utils.log.AssertionLoggerCapture;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleBundleTest {

    @Test
    void simple() throws IOException {
        try (AssertionLoggerCapture loggerCapture = new AssertionLoggerCapture()) {
            for (int i = 0; i < 10; i++){
                SimpleBundle.BUNDLE.simple();
            }

            assertEquals(10, loggerCapture.getNumberOfMessages());
            assertEquals(10, loggerCapture.countText("Simple1: simple message"));
        }
    }

    @Test
    void withParameters() throws IOException {
        try (AssertionLoggerCapture loggerCapture = new AssertionLoggerCapture()){
            SimpleBundle.BUNDLE.parameters(4,"four");
            assertEquals(1, loggerCapture.getNumberOfMessages());
            assertEquals(1, loggerCapture.countText("Simple2: parameters, 4, four"));
        }
    }

    @Test
    void withException() throws IOException {
        try (AssertionLoggerCapture loggerCapture = new AssertionLoggerCapture(true)) {
            SimpleBundle.BUNDLE.exception(6, new IllegalStateException("Illegal state of test"));
            assertEquals(1, loggerCapture.getNumberOfMessages());
            assertEquals(1, loggerCapture.countText("Simple3: parameters with exception, 6"));

            String st = loggerCapture.getLogEntries().getFirst().getStackTrace();
            assertTrue(st.contains("Illegal state of test"));
        }
    }

    @Test
    void withObject() throws IOException {
        try (AssertionLoggerCapture loggerCapture = new AssertionLoggerCapture()) {
            MyObject myObject = new MyObject(25, "value is 25");
            SimpleBundle.BUNDLE.object(myObject);

            String message = loggerCapture.getLogEntries().getFirst().getMessage();
            assertEquals("Simple4: parameters with object, " + myObject + "", message);
        }
    }

}
