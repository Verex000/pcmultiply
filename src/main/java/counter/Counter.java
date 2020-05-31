/*
TCSS422 - Spring 2020
Assignment 2 - Parallel Matrix Multiplier
Kevin Bui and Diem Vu
*/
package counter;

//import java.util.concurrent.locks.Lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Used to share a value between threads
 * @author Kevin Bui
 */
public class Counter
{
    /** The shared value */
    private int value;
    
    /** Synchronizes thread access */
    private final Lock lock;

    /**
     * The constructor initiate the counter's value
     */
    public Counter()
    {
        this.value = 0;
        this.lock = new ReentrantLock(true);
    }

    /**
     * The method increment the value of the counter
     */
    public void increment()
    {
        this.lock.lock();
        try {
            this.value++;
        }
        finally {
            this.lock.unlock();
        }
    }

    /**
     * The method return the value of the counter
     * @return the value of the counter
     */
    public int get()
    {
        this.lock.lock();
        try {
            return this.value;
        }
        finally {
            this.lock.unlock();
        }
    }
}
