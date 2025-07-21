package com.lazy.pegasus.core.common;

import com.lazy.pegasus.core.utils.MathsUtil;

public abstract class AbstractPool<I, T> {

    public static final int DEFAULT_POOL_SIZE = 32;

    private final T[] entries;
    private final int mask;
    private final int shift;

    public AbstractPool() {
        this(DEFAULT_POOL_SIZE);
    }

    @SuppressWarnings("unchecked")
    public AbstractPool(final int size) {
        this.entries = (T[]) new Object[MathsUtil.alignWithPowerOf2(size)];
        this.mask = this.entries.length - 1;
        this.shift = 31 - Integer.numberOfLeadingZeros(this.entries.length);
    }

    protected abstract boolean canPool(I value);

    /**
     * Creates a new interned entry.
     * @param value associated value
     * @return newly interned entry
     */
    protected abstract T create(I value);

    /**
     * Tests whether an entry equals to a value.
     * @param entry entry to check
     * @param value value to compare
     * @return true if content of entry equals to value, otherwise false
     */
    protected abstract boolean equals(T entry, I value);

    protected int hashCode(I value) {
        return value.hashCode();
    }

    /**
     * Gets a pooled entry if possible, otherwise an interned entry is created and returned.
     *
     * @param value value to create a new etry
     * @return pooled entry or newly interned entry
     */
    public T getOrCreate(final I value) {
        if (!canPool(value)) {
            return create(value);
        }

        final int hashCode = hashCode(value);
        final int firstIndex = hashCode & this.mask;
        final T firstEntry = this.entries[firstIndex];
        if (equals(firstEntry, value)) {
            return firstEntry;
        }


        final int secondIndex = (hashCode >> shift) & this.mask;
        final T secondEntry = this.entries[secondIndex];
        if (equals(secondEntry, value)) {
            return secondEntry;
        }

        final T internedEntry = create(value);
        final int entryIndex = firstEntry == null ? firstIndex : secondIndex;
        this.entries[entryIndex] = internedEntry;

        return internedEntry;
    }
}
