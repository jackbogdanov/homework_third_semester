public class MySynchronizedSet<T> {

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public MySynchronizedSet() {
        head = null;
        tail = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return !(size > 0);
    }

    public boolean contains(T value) {
        Node<T> tmp = head;

        while (tmp != null) {
            try {
                tmp.lockNode();
                if (tmp.getValue().equals(value)) {
                    return  true;
                }
            } finally {
                tmp.unlockNode();
            }

            tmp = tmp.getNext();
        }

        return false;
    }

    public boolean add(T t) {
        if (head == null && tail == null) {
            synchronized (this) {
                head = new Node<>(t);
                tail = head;
                size++;
            }

        } else {
            Node<T> newNode = new Node<>(t);

            synchronized (this) {
                tail.setNext(newNode);
                tail = newNode;
                size++;
            }

        }
        return true;
    }

    public boolean remove(T value) {

        if (head == null) {
            return false;
        }

        Node<T> tmp = head;
        Node<T> nextTmp = null;

        while (tmp.getNext() != null) {
            try {
                tmp.lockNode();
                nextTmp = tmp.getNext();
                nextTmp.lockNode();

                if (nextTmp.getValue().equals(value)) {
                    tmp.setNext(nextTmp.getNext());
                    synchronized (this) {
                        size--;
                        if (tmp.getNext() == null) {
                            tail = tmp;
                        }
                    }
                    return  true;
                }


            } finally {
                tmp.unlockNode();
                nextTmp.unlockNode();
            }

            tmp = tmp.getNext();
        }

        if (head.getValue().equals(value)) {
            synchronized (this) {
                size--;
                head = head.getNext();
            }
            return true;
        }

        return false;
    }


    public T getFirst() {
        T val = head.getValue();
        synchronized (this) {
            head = head.getNext();
            if (head == null) {
                tail = null;
            }
            size--;
        }
        return val;
    }
}