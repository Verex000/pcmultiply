package worker;

import counter.Counter;
import counter.SumCounter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import pcmultiply.PCMatrix;

public class Consumer extends Thread implements Runnable
{
    private List<Matrix> consumedMatrices;
    private Counter sharedConsumerCounter;
    private Counter sharedMultCounter;
    private Buffer sharedBuffer;
    private SumCounter sharedSumCounter;
    private ReentrantLock printLock;
    
    public Consumer(Buffer buffer, Counter counter, Counter multCounter, 
            SumCounter sumCounter, int threadName, ReentrantLock lock) {
        super("CONSUMER " + threadName);
        this.sharedBuffer = buffer;
        this.sharedConsumerCounter = counter;
        this.consumedMatrices = new ArrayList<>();
        this.sharedMultCounter = multCounter;
        this.sharedSumCounter = sumCounter;
        this.printLock = lock;
    }

    @Override
    public void run()
    {
        
        //Keep consuming until we reach the limit
        //The amount of consumed matrices should be equal to the amount produced
        while(sharedConsumerCounter.get() < PCMatrix.MATRICIES) {
            //We have to see if the two consumed matrices
            //will result in a valid multiplication.
            while(consumedMatrices.size() == 2) {
                Matrix m1 = consumedMatrices.get(0);
                Matrix m2 = consumedMatrices.get(1);
                Matrix m3 = m1.multiply(m2);
                //Multiplication not valid
                //Keep m1, get rid of m2
                if(m3 == null) {
                    m2 = null;
                    consumedMatrices.remove(1);
                }
                //Multiplication successful
                else {
                    printLock.lock();
                    try {
                        System.out.print(m1);
                        System.out.printf("    X\n");
                        System.out.print(m2);
                        System.out.printf("    =\n");
                        System.out.print(m3);
                        System.out.printf("\n");
                        consumedMatrices.clear();
                        m3 = null;
                        sharedMultCounter.increment();
                    }  
                    finally {
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
