package com.github.johantiden.dwarfactory.struct;

import java.util.Iterator;
import java.util.Queue;
import java.util.Spliterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FifoWithLimit<E> implements Iterable<E> {
    private final Queue<E> queue = new LinkedBlockingQueue<>();

    private final int capacity;

    public FifoWithLimit(int capacity) {this.capacity = capacity;}

    public void add(E value) {
        queue.add(value);
        if (queue.size() > capacity) {
            queue.remove();
        }
    }

    public void forEach(Consumer<? super E> action) {queue.forEach(action);}

    public Iterator<E> iterator() {return queue.iterator();}

    public Spliterator<E> spliterator() {return queue.spliterator();}

    public Stream<E> stream() {return queue.stream();}

    public Stream<E> parallelStream() {return queue.parallelStream();}
}
