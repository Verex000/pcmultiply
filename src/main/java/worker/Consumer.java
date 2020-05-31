/*
TCSS422 - Spring 2020
Assignment 2 - Parallel Matrix Multiplier
Kevin Bui and Diem Vu
*/
package worker;

import counter.Counter;
import counter.SumCounter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import pcmultiply.PCMatrix;

/**
 * Retrieves and multiplies matrices from Buffer.
 * @author Kevin Bui
 * @author Diem Vu
 */
public class Consumer extends Thread implements Runnable {

    /**Holds consumed matrices for multiplication*/
    private final List<Matrix> consumedMatrices;
    
    /** Shared counter between consumer threads to keep track of total consumed matrices*/
    private final Counter sharedConsumerCounter;
    
    /** Shared counter between consumer threads to keep track of total multiplied matrices*/
    private final Counter sharedMultCounter;
    
    /** Shared buffer to retrieve matrices from */
    private final Buffer sharedBuffer;
    
    /** Shared counter between consumer threads to keep track of total sum of matrix elements*/
    private final SumCounter sharedSumCounter;
    
    /** Lock used to synchronize printing of multiplying matrices*/
    private final ReentrantLock printLock;

    /**
     * Constructor
     * @param buffer Buffer Shared Buffer of matrices
     * @param counter Counter Counter for total consumed matrices
     * @param multCounter Counter Counter for total multiplied matrices
     * @param sumCounter SumCounter Counter for total sum of matrix elements
     * @param threadName int Name for this thread
     * @param lock ReentrantLocklock to be used for printing.
     */
    public Consumer(Buffer buffer, Counter counter, 
            Counter multCounter, SumCounter sumCounter, 
            int threadName, ReentrantLock lock) {
        
        super("CONSUMER " + threadName);
        this.sharedBuffer = buffer;
        this.sharedConsumerCounter = counter;
        this.consumedMatrices = new ArrayList<>();
        this.sharedMultCounter = multCounter;
        this.sharedSumCounter = sumCounter;
        this.printLock = lock;
    }

    /**
     * Consumer threads will keep retrieving matrices from the buffer
     * until it reaches the specified amount of matrices to retrieve.
     * 
     * When there are two matrices retrieved and stored, then the consumer
     * will multiply both matrices. If the multiplication is a success then
     * the result will be displayed and the multipliers will be discarded from 
     * the list.
     * Else, the consumer will keep the first matrix and discard the second matrix.
     * 
     * The consumer will retrieve one matrix at a time from the shared buffer, and
     * increment the counters. 
     */
    @Override
    public void run() {

        // Keep consuming until we reach the limit
        // The amount of consumed matrices should be equal to the amount produced
        while (sharedConsumerCounter.get() < PCMatrix.MATRICIES) {
            // We have to see if the two consumed matrices
            // will result in a valid multiplication.
            while (consumedMatrices.size() == 2) {
                Matrix m1 = consumedMatrices.get(0);
                Matrix m2 = consumedMatrices.get(1);
                Matrix m3 = m1.multiply(m2);
                // Multiplication not valid
                // Keep m1, get rid of m2
                if (m3 == null) {
                    m2 = null;
                    consumedMatrices.remove(1);
                }
                // Multiplication successful
                else {
                    printLock.lock();
                    try {
                        System.out.printf("MULTIPLY (%d x %d) BY (%d x  %d)\n", m1.getRow(), m1.getColumn(),
                                        m2.getRow(), m2.getColumn());

                        System.out.print(m1);
                        System.out.printf("    X\n");
                        System.out.print(m2);
                        System.out.printf("    =\n");
                        System.out.print(m3);
                        System.out.printf("\n");
                        consumedMatrices.clear();
                        m3 = null;
                        sharedMultCounter.increment();
                    } finally {
                        printLock.unlock();
                    }
                }
            }
            try {
                Matrix newMatrix = sharedBuffer.get();
                sharedSumCounter.increaseBy(newMatrix.getSum());
                consumedMatrices.add(newMatrix);
                sharedConsumerCounter.increment();
            } catch (InterruptedException ex) {
                Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
