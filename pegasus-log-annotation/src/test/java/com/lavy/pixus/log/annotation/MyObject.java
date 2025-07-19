package com.lavy.pixus.log.annotation;

public class MyObject {
    private final int value;
    private final String desc;

    public MyObject(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "MyObject[" +
                "value=" + value +
                ", desc='" + desc + '\'' +
                ']';
    }
}
