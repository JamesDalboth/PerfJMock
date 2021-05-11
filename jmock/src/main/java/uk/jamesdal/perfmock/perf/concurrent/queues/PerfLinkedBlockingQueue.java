package uk.jamesdal.perfmock.perf.concurrent.queues;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class PerfLinkedBlockingQueue<E> implements BlockingQueue<E> {
    private final HashMap<Integer, LinkedBlockingQueue<E>> queueMap;
    private final HashMap<Long, Integer> threadMap;
    private final ReentrantLock threadAllocationLock;

    private Integer currentQueue;

    public PerfLinkedBlockingQueue(int threadPoolSize) {
        this.queueMap = new HashMap<>();
        this.threadMap = new HashMap<>();

        for (int i = 0; i < threadPoolSize; i++) {
            this.queueMap.put(i, new LinkedBlockingQueue<>());
        }

        this.currentQueue = 0;
        this.threadAllocationLock = new ReentrantLock();
    }

    private LinkedBlockingQueue<E> getThreadQueue() {
        threadAllocationLock.lock();
        Long id = Thread.currentThread().getId();
        if (!threadMap.containsKey(id)) {
            threadMap.put(id, threadMap.size());
        }
        Integer queueId = threadMap.get(id);
        threadAllocationLock.unlock();

        return queueMap.get(queueId);
    }

    @Override
    public boolean offer(E e) {
        LinkedBlockingQueue<E> queue = queueMap.get(currentQueue);
        currentQueue = (currentQueue + 1) % queueMap.size();
        return queue.offer(e);
    }

    @Override
    public E poll() {
        LinkedBlockingQueue<E> queue = getThreadQueue();
        return queue.poll();
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        LinkedBlockingQueue<E> queue = getThreadQueue();
        return queue.poll(timeout, unit);
    }

    @Override
    public E take() throws InterruptedException {
        LinkedBlockingQueue<E> queue = getThreadQueue();
        return queue.take();
    }

    @Override
    public boolean remove(Object o) {
        for (LinkedBlockingQueue<E> queue : queueMap.values()) {
            if (queue.contains(o)) {
                return queue.remove(o);
            }
        }
        return false;
    }

    @Override
    public E remove() {
        return null;
    }

    @Override
    public boolean add(E e) {
        return false;
    }

    @Override
    public void put(E e) throws InterruptedException {

    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public int remainingCapacity() {
        return 0;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public int size() {
        int size = 0;
        for (LinkedBlockingQueue<E> queue : queueMap.values()) {
            size += queue.size();
        }

        return size;
    }

    @Override
    public boolean isEmpty() {
        for (LinkedBlockingQueue<E> queue : queueMap.values()) {
            if (!queue.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return 0;
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        return 0;
    }

    @Override
    public E element() {
        return null;
    }

    @Override
    public E peek() {
        return null;
    }
}
