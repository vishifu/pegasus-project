package com.lavy.pixus.log.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

@LogBundle(code = "Simple")
public interface SimpleBundle {

    static SimpleBundle init() {
        Logger logger = LoggerFactory.getLogger(SimpleBundle.class.getName() + "_impl");
        try {
            return (SimpleBundle) Class.forName(SimpleBundle.class.getName() + "_impl")
                    .getConstructor(Logger.class)
                    .newInstance(logger);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | ClassNotFoundException |
                 InvocationTargetException e) {
            throw new IllegalStateException("Could not access implement of bundle");
        }
    }

    SimpleBundle BUNDLE = init();

    @LogMessage(id = 1, message = "simple message")
    void simple();

    @LogMessage(id = 2, message = "parameters, {}, {}")
    void parameters(int v, String s);

    @LogMessage(id = 3, message = "parameters with exception, {}")
    void exception(int v, Exception ex);

    @LogMessage(id = 4, message = "parameters with object, {}")
    void object(MyObject obj);
}
