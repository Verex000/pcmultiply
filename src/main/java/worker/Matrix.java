package worker;


import pcmultiply.PCMatrix;
import java.util.Random;

/**
 * Matrix header Function prototypes, data, and constants for matrix module
 *
 * @author
 * University of Washington, Tacoma
 * TCSS 422 - Operating Systems
 */
public class Matrix
{
	/**
	 * The random instance to generate a stream of numbers.
	 */
	private static final Random rand = new Random();

	/**
	 * The number of rows in matrix.
	 */
	private int rows;

	/**
	 * The number of columns in matrix.
	 */
	private int columns;

	/**
	 * The values of matrix.
	 */
	private int[][] values;

	/**
	 * Creates a new matrix. This constructor automatically sets
	 * a number of rows and columns of the matrix (1 to 4)
	 */
	public Matrix()
	{
            if (PCMatrix.MATRIX_MODE == 0)
            {
		this.rows = 1 + rand.nextInt(4);
		this.columns = 1 + rand.nextInt(4);
		this.values = new int[rows][columns];
            }
            else
            {
		this.rows = PCMatrix.MATRIX_MODE;
		this.columns = PCMatrix.MATRIX_MODE;
		this.values = new int[rows][columns];
            }
	}

	/**
	 * Creates a new matrix. This constructor sets up a matrix
	 * with the given numbers of rows and columns.
     * 
	 * @param r the number of rows in matrix
	 * @param c the number of columns in matrix
	 */
	public Matrix(int r, int c)
	{
		this.rows = r;
		this.columns = c;
		this.values = new int[r][c];
	}

	/**
	 * The method randomly generate values in the matrix.
	 */
	public void generate()
	{
		for (int i = 0; i < this.rows; i++)
		{
			for (int j = 0; j < this.columns; j++)
			{
                                if (PCMatrix.MATRIX_MODE == 0)
                                    this.values[i][j] = 1 + rand.nextInt(10);
                                else
                                    this.values[i][j] = 1;
			}
		}
	}
	
	/**
	 * The method calculate the dot product of two matrices.
	 * 
	 * @param other the other matrix.
	 * @return the new matrix produced from the dot product operation.
	 */
	public Matrix multiply(Matrix other)
	{
		// validate the other matrix
		if (other == null)
		{
			throw new NullPointerException("The matrix could not be null");
		}
		
		if (columns != other.rows)
		{
//			throw new IllegalArgumentException("The number of columns of matrix 1 and"
//					+ " the number of rows of matrix 2 must be the same");
			return null;
		}
		
		// create an instance for the new matrix
		Matrix output = new Matrix(rows, other.columns);
		
		int sum = 0;
		int[][] ma1 = values;
		int[][] ma2 = other.values;
		int[][] ma3 = output.values;
		
		// operate the dot product
		for (int c = 0; c < output.rows; c++)
		{
			for (int d = 0; d < output.columns; d++)
			{
				for (int k = 0; k < other.rows; k++)
				{
					sum += ma1[c][k] * ma2[k][d];
				}
				
				ma3[c][d] = sum;
				sum = 0;
			}
		}
		
		return output;
	}
	
	/**
	 * The method calculate the sum of values in the matrix.
	 * 
	 * @return the sum of all values in the matrix.
	 */
	public int getSum()
	{
		// initiate the sum
		int sum = 0;
		
		// iterate over the matrix and
		// add up each value to the sum
		for (int i = 0; i < this.rows; i++)
		{
			for (int j = 0; j < this.columns; j++)
			{
				sum += this.values[i][j];
			}
		}
		
		return sum;
	}
	
	/**
	 * The method calculate the average of values in the matrix.
	 * 
	 * @return the average of all values in the matrix.
	 */
	public int getAverage()
	{
		return getSum() / (rows * columns);
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < rows; i++)
		{
			builder.append('|');
			for (int j = 0; j < columns; j++)
			{
				if (j != 0)
					builder.append(' ');
				builder.append(String.format("%3d", values[i][j]));
			}
			builder.append('|').append('\n');
		}
		
		return builder.toString();
	}
	public int getRow() {
		return this.rows;
	}
	public int getColumn() {
		return this.columns;
	}

}
