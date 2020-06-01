/*
TCSS422 - Spring 2020
Assignment 2 - Parallel Matrix Multiplier
Kevin Bui and Diem Vu
*/
package worker;

import counter.Counter;
import counter.SumCounter;
import java.util.logging.Level;
import java.util.logging.Logger;
import pcmultiply.PCMatrix;

/**
 * Creates matrices to put into the buffer.
 * 
 * @author Kevin Bui
 * @author Diem Vu
 */
public class Producer extends Thread implements Runnable {
	/** Shared buffer to store produced matrices */
	private final Buffer sharedBuffer;

	/**
	 * Shared counter between all producer threads to keep track of total produced
	 * matrices
	 */
	private final Counter sharedProducerCounter;

	/**
	 * Shared counter between all producer threads to keep track of total sum of
	 * matrix elements
	 */
	private final SumCounter sharedSumCounter;

	/**
	 * Constructor
	 * 
	 * @param buffer     Buffer Shared Buffer of matrices
	 * @param counter    Counter Counter to keep track of produced matrices
	 * @param sumCounter SumCounter Counter to keep track of total sum of matrix
	 *                   elements
	 * @param threadName int Name of the thread
	 */
	public Producer(Buffer buffer, Counter counter, SumCounter sumCounter, int threadName) {
		super("PRODUCER " + threadName);
		this.sharedBuffer = buffer;
		this.sharedProducerCounter = counter;
		this.sharedSumCounter = sumCounter;
	}

	/**
	 * Producer threads will keep running until the amount of produced matrices
	 * reaches the amount specified.
	 * 
	 * Matrices will be generated depending on PCMatrix.MATRIX_MODE, then put into
	 * the shared buffer. Counters will be incremented accordingly.
	 */
	@Override
	public void run() {
		// Keep adding matrices until we reach the limit.
		while (sharedProducerCounter.get() < PCMatrix.MATRICIES) {
			Matrix m = new Matrix();

			m.generate();
			sharedSumCounter.increaseBy(m.getSum());
			try {

				sharedBuffer.put(m);

//                total++;
//                System.out.println(total);
				sharedProducerCounter.increment();
			} catch (InterruptedException ex) {
				Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

}
