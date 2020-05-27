package counter;

//import java.util.concurrent.locks.Lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import pcmultiply.PCMatrix;

//import java.util.concurrent.locks.ReentrantLock;

/**
 *  TO DO: This counter needs to be synchronized..
 */
public class Counter
{
	private int value;
        //private static final int BUFFER_CAPACITY = PCMatrix.BOUNDED_BUFFER_SIZE;
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
