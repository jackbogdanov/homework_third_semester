import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Node<T> {

    private T value;
    private Node<T> next;
    private Lock lock;

    public Node(T value) {
        this.value = value;
        next = null;

        lock = new ReentrantLock();
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public T getValue() {
        return value;
    }

    public void lockNode() {
        lock.lock();
    }

    public void unlockNode() {
        lock.unlock();
    }

}
