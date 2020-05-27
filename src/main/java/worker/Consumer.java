package worker;

import counter.Counter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Consumer extends Thread implements Runnable
{
    private List<Matrix> consumedMatrices;
    private Counter sharedConsumerCounter;
    private Buffer sharedBuffer;
    
    public Consumer(Buffer buffer, Counter counter) {
        super("CONSUMER");
        this.sharedBuffer = buffer;
        this.sharedConsumerCounter = counter;
        this.consumedMatrices = new ArrayList<>();
    }

    @Override
    public void run()
    {
        if(consumedMatrices.size() == 2) {
            Matrix m1 = consumedMatrices.get(0);
            Matrix m2 = consumedMatrices.get(1);
            Matrix m3 = m1.multiply(m2);
            if(m3 == null) {
                m2 = null;
                consumedMatrices.remove(1);
            }
            else {
                System.out.println(m3.toString());
            }
        }
        try {
            Matrix newMatrix = sharedBuffer.get();
            consumedMatrices.add(newMatrix);
            sharedConsumerCounter.increment();
        } catch (InterruptedException ex) {
            Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
