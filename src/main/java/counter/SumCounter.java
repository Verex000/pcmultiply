/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package counter;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This counter will be used to keep track of total matrices sum.
 * @author Kevin Bui
 */
public class SumCounter extends Counter {
    
    private final Lock lock;
    private int sharedSum;
    
    public SumCounter() {
        super();
        lock = new ReentrantLock(true);
        sharedSum = 0;
    }
    
    public void increaseBy(int value) {
        lock.lock();
        try {
            sharedSum += value;
        }
        finally {
            lock.unlock();
        }
    }
    
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
