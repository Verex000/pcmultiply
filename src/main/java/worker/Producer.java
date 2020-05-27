package worker;

import counter.Counter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Producer extends Thread implements Runnable
{
    private Buffer buffer;
    private Counter counter;
    public Producer(Buffer buffer, Counter counter) {
        super("PRODUCER");
        this.buffer = buffer;
        this.counter = counter;
    }

    @Override
    public void run()
    {
        Matrix m = new Matrix();
        m.generate();
        try {
            buffer.put(m);
            counter.increment();
        } catch (InterruptedException ex) {
            Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
