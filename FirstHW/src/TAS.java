import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TAS implements Lock {
    private AtomicBoolean locked;

    public TAS() {
        locked = new AtomicBoolean();
    }

    public void lock(){
        while (!locked.compareAndSet(false, true)){};
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void unlock() {
        locked.set(false);
    }

    @Override
    public Condition newCondition() {
        return null;
    }

}
