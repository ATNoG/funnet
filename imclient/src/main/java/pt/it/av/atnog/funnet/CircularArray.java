package pt.it.av.atnog.funnet;

import java.util.Iterator;

public class CircularArray<E> implements Iterable<E> {
    private final E array[];
    private int start = 0, size = 0;

    public CircularArray(int maxSize) {
        array = (E[]) new Object[maxSize];
    }

    public void in(E object) {
        int idx = 0;
        if (size == array.length) {
            idx = start;
            start = (start + 1) % array.length;
        } else {
            idx = (start + size) % array.length;
            size++;
        }
        array[idx] = object;
    }

    public E out() {
        E rv = null;

        if (!isEmpty()) {
            rv = array[start];
            start = (start + 1) % array.length;
            size--;
        }

        return rv;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return false;
    }

    public int size() {
        return size;
    }

    public void clear() {
        start = 0;
        size = 0;
    }

    public Iterator<E> iterator() {
        return new CircularQueueIterator();
    }

    public class CircularQueueIterator implements Iterator<E> {
        private int idx = start, count = 0;

        public boolean hasNext() {
            return count < size;
        }

        public E next() {
            E rv = null;
            if (hasNext()) {
                rv = array[idx];
                idx = (idx + 1) % array.length;
                count++;
            }
            return rv;
        }
    }
}
