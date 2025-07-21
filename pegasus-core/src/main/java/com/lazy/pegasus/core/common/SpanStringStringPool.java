package com.lazy.pegasus.core.common;

public final class SpanStringStringPool extends AbstractPool<String, SpanString> {

    public SpanStringStringPool() {
        super();
    }

    public SpanStringStringPool(int size) {
        super(size);
    }

    @Override
    protected boolean canPool(String value) {
        return true;
    }

    @Override
    protected SpanString create(String value) {
        return SpanString.of(value);
    }

    @Override
    protected boolean equals(SpanString entry, String value) {
        if (entry == null) {
            return false;
        }

        return entry.toString().equals(value);
    }
}
