package dataManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.exceptions.NeurophException;
import org.neuroph.core.exceptions.VectorSizeMismatchException;


/**
 * This class represents a collection of data rows (DataSetFitRow instances) used
 * for training and testing neural network.
 *
 * @author Clement ORTIZ <df4ze31@gmail.com>
 * @see DataSetRow
 * @see DataSet
 * http://openforecast.sourceforge.net/docs/net/sourceforge/openforecast/DataSet.html
 */
public class FitDataSet extends DataSet {


	private static final long serialVersionUID = 1L;

    /**
     * Flag which indicates if this data set containes data rows for Fitness deepLearning
     */
    private boolean isFited = false;
    
    

	public FitDataSet(int inputSize) {
		super(inputSize);
		setFited( false );	
	}

	public FitDataSet(int inputSize, int outputSize) {
		super(inputSize, outputSize);
		setFited( false );		
	}

	public FitDataSet(int inputSize, int outputSize, boolean isFited) {
		super(inputSize, outputSize);
		setFited( isFited );		
	}

	
    /**
     * Adds new row to this data set
     *
     * @param row data set row to add
     */
    public void addRow(FitDataSetRow row)
            throws VectorSizeMismatchException, NeurophException {

    	if( row != null && row.isFited() != isFited() )
    		throw new NeurophException("Mismatch on row and data set fitness!");
    	
        super.addRow(row);
    }

    /**
     * Adds a new dataset row with specified input, output and fitness
     *
     * @param input
     * @param output
     * @param fitness
     */
    public void addRow(double[] input, double[] output, double fitness)throws VectorSizeMismatchException, NeurophException {
        this.addRow(new FitDataSetRow(input, output, fitness));
    }
    
	
	/**
	 * Get Flag which indicates if this data set containes data rows for Fitness deepLearning
	 * @return true if contains Fitness information
	 */
	public boolean isFited() {
		return isFited;
	}

	/**
	 * Set Flag which indicates if this data set containes data rows for Fitness deepLearning
	 * 
	 * @param isFited true if contains Fitness information
	 */
	public void setFited(boolean isFited) {
		this.isFited = isFited;
	}

	
    public void saveAsTxt(String filePath, String delimiter) {

        if (filePath == null) throw new IllegalArgumentException("File path is null!");

        // default delimiter is space if other is not specified
        if ((delimiter == null) || delimiter.equals("")) {
            delimiter = " ";
        }


        try (PrintWriter out = new PrintWriter(new FileWriter(new File(filePath)))) {

            int columnCount = getInputSize() + getOutputSize() + ( isFited()?1:0 );
            if ((getColumnNames() != null) && (getColumnNames().length > 0)) {
                for (int i = 0; i < getColumnNames().length; i++) {
                    out.print(getColumnNames()[i]);
                    if (i < columnCount - 1) out.print(delimiter);
                }
                out.println();
            }

            for (DataSetRow row : getRows()) {
            	
                double[] input = row.getInput();
                for (int i = 0; i < input.length; i++) {
                    out.print(input[i]);
                    if (i < columnCount - 1) out.print(delimiter);
                }

                if (row.isSupervised()) {
                    double[] output = row.getDesiredOutput();
                    for (int j = 0; j < output.length; j++) {
                        out.print(output[j]);
                        if (getInputSize() + j < columnCount - 1) out.print(delimiter);
                    }
                }
                
                if( row instanceof FitDataSetRow ){
                	FitDataSetRow fitRow = (FitDataSetRow)row;
                	out.print( fitRow.getFitness() );
                }
                out.println();
            }

            out.flush();

        } catch (IOException ex) {
            throw new NeurophException("Error saving data set file!", ex);
        }
    }
    
    
    /**
     * Creates and returns data set from specified csv file
     *
     * @param filePath        path to csv dataset file to import
     * @param inputsCount     number of inputs
     * @param outputsCount    number of outputs
     * @param delimiter       delimiter of values
     * @param isFited         contains Fitness informations
     * @param loadColumnNames true if csv file contains column names in first line, false otherwise
     * @return instance of datasetfit with values from specified file
     */
    public static FitDataSet createFromFile(String filePath, int inputsCount, int outputsCount, String delimiter, boolean isFited, boolean loadColumnNames) {
        BufferedReader reader = null;

        if (filePath == null) throw new IllegalArgumentException("File name cannot be null!");
        if (inputsCount <= 0) throw new IllegalArgumentException("Number of inputs cannot be <= 0");
        if (outputsCount < 0) throw new IllegalArgumentException("Number of outputs cannot be < 0");
        if ((delimiter == null) || delimiter.isEmpty())
            throw new IllegalArgumentException("Delimiter cannot be null or empty!");

        try {
            FitDataSet dataSet = new FitDataSet(inputsCount, outputsCount, isFited);
            dataSet.setFilePath(filePath);            
            reader = new BufferedReader(new FileReader(new File(filePath)));

            String line = null;

            if (loadColumnNames) {
                // get column names from the first line
                line = reader.readLine();
                String[] colNames = line.split(delimiter);
                dataSet.setColumnNames(colNames);
            }

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(delimiter);

                double[] inputs = new double[inputsCount];
                double[] outputs = new double[outputsCount];
                double fitness = 0;

                if (values[0].equals("")) {
                    continue; // skip if line was empty
                }
                for (int i = 0; i < inputsCount; i++) {
                    inputs[i] = Double.parseDouble(values[i]);
                }

                for (int i = 0; i < outputsCount; i++) {
                    outputs[i] = Double.parseDouble(values[inputsCount + i]);
                }
                
                if( isFited )
                	fitness = Double.parseDouble(values[ inputsCount + outputsCount ]);

                if (outputsCount > 0 && isFited ) {
                	dataSet.addRow(inputs, outputs, fitness);
                }else if (outputsCount > 0) {
                	dataSet.addRow(new DataSetRow(inputs, outputs));
                } else {
                    dataSet.addRow(new DataSetRow(inputs));
                }
            }

            reader.close();
            
            return dataSet;

        } catch (FileNotFoundException ex) {
            throw new NeurophException("Could not find data set file!", ex);
        } catch (IOException ex) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex1) {
                }
            }
            throw new NeurophException("Error reading data set file!", ex);
        } catch (NumberFormatException ex) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex1) {
                }
            }
            ex.printStackTrace();
            throw new NeurophException("Bad number format in data set file!", ex);
        }

    }
    
    /**
     * Creates and returns data set from specified csv file
     *
     * @param filePath        path to csv dataset file to import
     * @param inputsCount     number of inputs
     * @param outputsCount    number of outputs
     * @param delimiter       delimiter of values
     * @return instance of dataset with values from specified file
     */    
    public static FitDataSet createFromFile(String filePath, int inputsCount, int outputsCount, String delimiter, boolean isFited) {    
        return createFromFile(filePath, inputsCount, outputsCount, delimiter, isFited, false);
    }




}
