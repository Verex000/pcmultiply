package worker;

import counter.Counter;
import counter.SumCounter;
import java.util.logging.Level;
import java.util.logging.Logger;
import pcmultiply.PCMatrix;


public class Producer extends Thread implements Runnable
{
    private final Buffer sharedBuffer;
    private final Counter sharedProducerCounter;
    private final SumCounter sharedSumCounter;
    public Producer(Buffer buffer, Counter counter, SumCounter sumCounter, int threadName) {
        super("PRODUCER " + threadName);
        this.sharedBuffer = buffer;
        this.sharedProducerCounter = counter;
        this.sharedSumCounter = sumCounter;
    }

    @Override
    public void run()
    {
        //Keep adding matrices until we reach the limit.
        while(sharedProducerCounter.get() < PCMatrix.MATRICIES) {
            Matrix m = new Matrix();
            m.generate();
            try {
                sharedBuffer.put(m);
                sharedSumCounter.increaseBy(m.getSum());
                sharedProducerCounter.increment();
            } catch (InterruptedException ex) {
                Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
