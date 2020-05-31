/*
TCSS422 - Spring 2020
Assignment 2 - Parallel Matrix Multiplier
Kevin Bui and Diem Vu
*/
/**************************************************
 * Extra Credit Features: EC1, EC2, EC3 implemented
 **************************************************/
/**
 *  pcmatrix module
 *  Primary module providing control flow for the pcMatrix program
 * 
 *  Producer consumer bounded buffer program to produce random matrices in parallel
 *  and consume them while searching for valid pairs for matrix multiplication.
 *  Matrix multiplication requires the first matrix column count equal the 
 *  second matrix row count.  
 *
 *  A matrix is consumed from the bounded buffer.  Then matrices are consumed
 *  from the bounded buffer, one at a time, until an eligible matrix for multiplication
 *  is found.
 *
 *  Totals are tracked using the ProdConsStats Struct for:
 *  - the total number of matrices multiplied (multtotal from consumer threads)
 *  - the total number of matrices produced (matrixtotal from producer threads)
 *  - the total number of matrices consumed (matrixtotal from consumer threads)
 *  - the sum of all elements of all matrices produced and consumed (sumtotal from producer and consumer threads)
 *
 *  Correct programs will produce and consume the same number of matrices, and
 *  report the same sum for all matrix elements produced and consumed. 
 *
 *  For matrix multiplication only ~25% may be e
 *  and consume matrices.  Each thread produces a total sum of the value of
 *  randomly generated elements.  Producer sum and consumer sum must match.
 *
 *  University of Washington, Tacoma
 *  TCSS 422 - Operating Systems
 *  Spring 2020
 */
package pcmultiply;

import counter.Counter;
import counter.SumCounter;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import worker.Buffer;

import worker.Consumer;
import worker.Matrix;
import worker.Producer;

public class PCMatrix {
	public static final String S_WORKER_THREADS = "worker-threads";
	public static final String S_BOUNDED_BUFFER_SIZE = "bounded-buffer-size";
	public static final String S_MATRICES = "matrices";
	public static final String S_MATRIX_MODE = "matrix-mode";

//        public static int MATRIX_MODE = 0;
//        public static int WORKER_THREADS = 1;
//        public static int MATRICIES = 1200;
//        public static int BOUNDED_BUFFER_SIZE = 200;
	public static int MATRIX_MODE = 0;
	public static int WORKER_THREADS = 5;
	public static int MATRICIES = 70;
	public static int BOUNDED_BUFFER_SIZE = 5;

	public static void main(String[] args) throws InterruptedException {
		Options options = new Options();

		Option optThreads = new Option("t", S_WORKER_THREADS, true, "the number of worker threads");
		optThreads.setRequired(false);
		options.addOption(optThreads);

		Option optBoundedBuffer = new Option("b", S_BOUNDED_BUFFER_SIZE, true, "the size of the bounded buffer");
		optBoundedBuffer.setRequired(false);
		options.addOption(optBoundedBuffer);

		Option optMatrices = new Option("m", S_MATRICES, true, "the number of matrices");
		optMatrices.setRequired(false);
		options.addOption(optMatrices);

		Option optMatrixMode = new Option("o", S_MATRIX_MODE, true, "the matrix mode");
		optMatrixMode.setRequired(false);
		options.addOption(optMatrixMode);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
			List<String> list = cmd.getArgList();
			if (list.size() == 4) {
				WORKER_THREADS = Integer.parseInt(list.get(0));
				BOUNDED_BUFFER_SIZE = Integer.parseInt(list.get(1));
				MATRICIES = Integer.parseInt(list.get(2));
				MATRIX_MODE = Integer.parseInt(list.get(3));
			}
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("PCMatrix", options);

			System.exit(1);
		}

//		String value = cmd.getOptionValue("t");
//		System.out.println("value" + value);
//		if (value != null) {
//			try {
//				WORKER_THREADS = Integer.parseInt(value);
//			} catch (NumberFormatException e) {
//			}
//		}
//
//		value = cmd.getOptionValue(S_BOUNDED_BUFFER_SIZE);
//		if (value != null) {
//			try {
//				BOUNDED_BUFFER_SIZE = Integer.parseInt(value);
//			} catch (NumberFormatException e) {
//			}
//		}
//
//		value = cmd.getOptionValue(S_MATRICES);
//		if (value != null) {
//			try {
//				MATRICIES = Integer.parseInt(value);
//			} catch (NumberFormatException e) {
//			}
//		}
//
//		value = cmd.getOptionValue(S_MATRIX_MODE);
//		if (value != null) {
//			try {
//				MATRIX_MODE = Integer.parseInt(value);
//			} catch (NumberFormatException e) {
//			}
//		}

		StringBuilder builder = new StringBuilder();
		builder.append(S_WORKER_THREADS).append('=').append(WORKER_THREADS).append(' ');
		builder.append(S_BOUNDED_BUFFER_SIZE).append('=').append(BOUNDED_BUFFER_SIZE).append(' ');
		builder.append(S_MATRICES).append('=').append(MATRICIES).append(' ');
		builder.append(S_MATRIX_MODE).append('=').append(MATRIX_MODE).append(' ');
		System.out.println(builder.toString());

		System.out.printf("Producing %d matrices in mode %d.\n", MATRICIES, MATRIX_MODE);
		System.out.printf("Using a shared buffer of size=%d\n", BOUNDED_BUFFER_SIZE);
		System.out.printf("With %d producer and consumer thread(s).\n", WORKER_THREADS);
		System.out.printf("\n");

		Counter prodCounter = new Counter();
		Counter consCounter = new Counter();
		Counter consMultCounter = new Counter();
		SumCounter prsCounter = new SumCounter();
		SumCounter cosCounter = new SumCounter();
		Buffer sharedBuffer = new Buffer();
		ReentrantLock printLock = new ReentrantLock();

		Producer[] prodArr = new Producer[WORKER_THREADS];
		Consumer[] consArr = new Consumer[WORKER_THREADS];
		for (int i = 0; i < WORKER_THREADS; i++) {
			prodArr[i] = new Producer(sharedBuffer, prodCounter, prsCounter, i + 1);
			consArr[i] = new Consumer(sharedBuffer, consCounter, consMultCounter, cosCounter, i + 1, printLock);
		}
		for (int i = 0; i < WORKER_THREADS; i++) {
			prodArr[i].start();
		}
		for (int i = 0; i < WORKER_THREADS; i++) {
			consArr[i].start();
		}
		for (int i = 0; i < WORKER_THREADS; i++) {
			prodArr[i].join();
		}
		for (int i = 0; i < WORKER_THREADS; i++) {
			consArr[i].join();
		}

		int prs = prsCounter.get();
		int cos = cosCounter.get();
		int prodtot = prodCounter.get() + 1 - WORKER_THREADS;
		int constot = consCounter.get() + 1 - WORKER_THREADS;
		int consmul = consMultCounter.get();

		// consume ProdConsStats from producer and consumer threads
		// add up total matrix stats in prs, cos, prodtot, constot, consmul

		System.out.printf("Sum of Matrix elements --> Produced=%d = Consumed=%d\n", prs, cos);
		System.out.printf("Matrices produced=%d consumed=%d multiplied=%d\n", prodtot, constot, consmul);
	}
}
