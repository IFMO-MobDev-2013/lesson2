package ru.zulyaev.ifmo.android.lesson2;

import java.util.Iterator;

/**
 * @author Никита
 */
public class CyclicArrayIterator<T> implements Iterator<T> {
    private T[] array;
    private int next = 0;

    public CyclicArrayIterator(T[] array) {
        this.array = array;
    }

    @Override
    public boolean hasNext() {
        return array.length != 0;
    }

    @Override
    public T next() {
        T value = array[next++];
        if (next == array.length) next = 0;
        return value;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
