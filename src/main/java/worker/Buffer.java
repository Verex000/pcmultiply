package worker;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import pcmultiply.PCMatrix;

public class Buffer 
{

    // Bounded buffer data storage
    // Feel free to customize the data type
    //private Matrix[] bigMatrix = new Matrix[PCMatrix.BOUNDED_BUFFER_SIZE];
    private Queue<Matrix> bigMatrix = new LinkedList<Matrix>();
    private final Lock lock;
    private final Condition producerCond;
    private final Condition consumerCond;
    
    public Buffer() {
        this.lock = new ReentrantLock(true);
        this.producerCond = this.lock.newCondition();
        this.consumerCond = this.lock.newCondition();
    }
    // Bounded buffer put() and get()
    public void put(Matrix matrix) throws InterruptedException
    {
        this.lock.lock();
        try {
            while(bigMatrix.size() == PCMatrix.BOUNDED_BUFFER_SIZE) {
                this.producerCond.await();
            }
            boolean isAdded = this.bigMatrix.offer(matrix);
            if(isAdded) {
                this.consumerCond.signalAll();
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    public Matrix get() throws InterruptedException
    {
        Matrix m = null;
        this.lock.lock();
        try {
            while(bigMatrix.isEmpty()) {
                this.consumerCond.await();
            }
            m = this.bigMatrix.poll();
            if(m == null) {
                throw new NoSuchElementException("Get: A null matrix was consumed");
            }
            return m;
            //this.producerCond.signalAll();
        }
        finally {
            this.producerCond.signalAll();
            this.lock.unlock();
        }
    }

}
