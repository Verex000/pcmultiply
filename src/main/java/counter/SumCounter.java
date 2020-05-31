/*
TCSS422 - Spring 2020
Assignment 2 - Parallel Matrix Multiplier
Kevin Bui and Diem Vu
*/
package counter;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This counter will be used to keep track of total matrices sum.
 * @author Kevin Bui
 */
public class SumCounter extends Counter {
    
    /** Synchronizes thread access */
    private final Lock lock;
    
    /** Sum shared between threads */
    private int sharedSum;
    
    /**
     * Constructor
     */
    public SumCounter() {
        super();
        lock = new ReentrantLock(true);
        sharedSum = 0;
    }
    
    /**
     * Increases the sharedSum by value
     * @param value 
     */
    public void increaseBy(int value) {
        lock.lock();
        try {
            sharedSum += value;
        }
        finally {
            lock.unlock();
        }
    }
    
    /**
     * Retrieves the sharedSum
     * @return int sum
     */
    @Override
    public int get() {
        lock.lock();
        try {
            return sharedSum;
        }
        finally {
            lock.unlock();
        }
    }
    
}
