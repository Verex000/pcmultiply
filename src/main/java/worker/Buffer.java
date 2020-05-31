/*
TCSS422 - Spring 2020
Assignment 2 - Parallel Matrix Multiplier
Kevin Bui and Diem Vu
*/
package worker;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import pcmultiply.PCMatrix;

/**
 * Stores Matrices for producers and consumers to use.
 * @author Kevin Bui
 * @author Diem Vu
 */
public class Buffer 
{

    // Bounded buffer data storage
    // Feel free to customize the data type
    //private Matrix[] bigMatrix = new Matrix[PCMatrix.BOUNDED_BUFFER_SIZE];
    private final Queue<Matrix> bigMatrix = new LinkedList<>();
    
    /**Used to synchronize put and get operations on the buffer*/
    private final Lock lock;
    
    /**Condition for producers to put more matrices into the buffer*/
    private final Condition producerCond;
    
    /**Condition for consumers to get more matrices from the buffer*/
    private final Condition consumerCond;

    /**
     * Constructor
     */
    public Buffer() {
        //Ensures a fair lock
        //Longest waiting thread will acquire this lock first
        this.lock = new ReentrantLock(true);
        this.producerCond = this.lock.newCondition();
        this.consumerCond = this.lock.newCondition();
    }
    
    /**
     * Producers will add matrices to the front of the queue 
     * as long as it is not full.
     * 
     * A lock surrounds the entire operation to ensure no other
     * producer thread can put a matrix into the buffer at the same time.
     * 
     * A signal is sent to the consumer condition when there is at least,
     * one matrix in the buffer.
     * @param matrix Is put into the buffer
     * @throws InterruptedException 
     */
    public void put(Matrix matrix) throws InterruptedException
    {
        this.lock.lock();
        try {
            while(bigMatrix.size() == PCMatrix.BOUNDED_BUFFER_SIZE) {
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
                throw new IllegalStateException();
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    /**
     * Consumers will retrieve matrices from the back of the queue.
     * 
     * A lock surrounds all the operations in order to ensure no two 
     * consumer threads retrieve the same matrix at the same time.
     * 
     * A signal is sent to the producers when the buffer is not full anymore.
     * @return A Matrix
     * @throws InterruptedException 
     */
    public Matrix get() throws InterruptedException
    {
        Matrix m = null;
        this.lock.lock();
        try {
            //Buffer is empty, so consumer has to wait
            while(bigMatrix.isEmpty()) {
                this.consumerCond.await();
            }
            m = this.bigMatrix.poll();
            //This will most likely never happen.
            //If this happens then a null matrix was produced.
            if(m == null) {
                throw new NoSuchElementException("Get: A null matrix was consumed");
            }
            this.producerCond.signalAll();
            return m;
        }
        finally {
            //We signal to the producer that the buffer is not full.
            this.lock.unlock();
        }
    }
}
