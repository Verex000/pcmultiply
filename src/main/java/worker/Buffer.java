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
        //Ensures a fair lock
        //Longest waiting thread will acquire this lock first
        this.lock = new ReentrantLock(true);
        this.producerCond = this.lock.newCondition();
        this.consumerCond = this.lock.newCondition();
    }
    // Bounded buffer put() and get()
    public void put(Matrix matrix) throws InterruptedException
    {
        this.lock.lock();
        try {
            //Buffer is full so producer has to wait
            while(bigMatrix.size() == PCMatrix.BOUNDED_BUFFER_SIZE) {
                System.out.println(Thread.currentThread().getName() + 
                        ": Buffer is Full, waiting");
                this.producerCond.await();
            }
            boolean isAdded = this.bigMatrix.offer(matrix);
            if(isAdded) {
                //We signal to the consumer that the buffer is not empty.
                //Therefore consumer can keep running.
                this.consumerCond.signalAll();
            }
            //This will never happen unless there is a null matrix, or an error
            else {
                System.out.println(Thread.currentThread().getName() + 
                        ": Matrix could not be added to the buffer");
                throw new IllegalStateException();
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
            //Buffer is empty, so consumer has to wait
            while(bigMatrix.isEmpty()) {
                System.out.println(Thread.currentThread().getName() + 
                        ": Buffer is empty, waiting");
                this.consumerCond.await();
            }
            m = this.bigMatrix.poll();
            //This will most likely never happen.
            //If this happens then a null matrix was produced.
            if(m == null) {
                throw new NoSuchElementException("Get: A null matrix was consumed");
            }
            return m;
            //this.producerCond.signalAll();
        }
        finally {
            //We signal to the producer that the buffer is not full.
            this.producerCond.signalAll();
            this.lock.unlock();
        }
    }

}
