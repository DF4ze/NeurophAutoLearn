package dataManager;

import java.util.ArrayList;

import org.neuroph.core.data.DataSetRow;



/**
 * This class represents single data row in a data set. It has input, desired output and fitness of the output
 * for supervised learning rules and deepLearning. It can also be used only with input for unsupervised learning rules.
 *
 * @author Clement ORTIZ <df4ze31@gmail.com>
 * @see DataSetRow
 */

public class FitDataSetRow extends DataSetRow{
	

	private static final long serialVersionUID = 1L;

    /**
     * Fitness of output for this training element
     */
    private Double fitness = null;
    
    /**
     * Does this row contains fitness values?
     */
    private boolean fited;

    /**
     * Creates new training element with specified input and desired output
     * vectors specifed as strings
     *
     * @param input input vector as space separated string
     * @param desiredOutput desired output vector as space separated string
     */
    public FitDataSetRow(String input, String desiredOutput) {
    	super( input, desiredOutput );
    	setFited(false);
    }
	
    /**
     * Creates new training element with specified input and desired output
     * vectors specifed as strings
     *
     * @param input input vector as space separated string
     * @param desiredOutput desired output vector as space separated string
     * @param fitness fitness of the output vector as string
     */
    public FitDataSetRow(String input, String desiredOutput, String fitness) {
    	super( input, desiredOutput );
        this.setFitness(Double.parseDouble(fitness));
        setFited(true);
    }
	
    
    /**
     * Creates new training element with specified input and desired output
     * vectors
     *
     * @param input input array
     * @param desiredOutput desired output array
     */
    public FitDataSetRow(double[] input, double[] desiredOutput ) {
    	super( input, desiredOutput );
        setFited(false);
    }
    
    /**
     * Creates new training element with specified input and desired output
     * vectors
     *
     * @param input input array
     * @param desiredOutput desired output array
     * @param fitness fitness of the output array
     */
    public FitDataSetRow(double[] input, double[] desiredOutput, double fitness ) {
    	super( input, desiredOutput );
        this.setFitness(fitness);
        setFited(true);
    }

	/**
	 * Creates new training element with specified input and desired output
	 * vectors
	 * 
	 * @param input
	 *            input vector
	 * @param desiredOutput
	 *            desired output vector
	 * @param fitness
	 *            fitness of the output vector
	 */
	public FitDataSetRow(ArrayList<Double> input,
			ArrayList<Double> desiredOutput,
			Double fitness) {
    	super( input, desiredOutput );
        this.setFitness(fitness);
        setFited(false);
	}
	
	/**
	 * Creates new training element with specified input and desired output
	 * vectors
	 * 
	 * @param input
	 *            input vector
	 * @param desiredOutput
	 *            desired output vector
	 * @param fitness
	 *            fitness of the output vector
	 */
	public FitDataSetRow(ArrayList<Double> input,
			ArrayList<Double> desiredOutput) {
    	super( input, desiredOutput );
        this.setFitness(fitness);
        setFited(true);
	}
	
    /**
     * Creates new training element with input array
     *
     * @param input input array
     */
    public FitDataSetRow(double... input) {
        super( input );
        setFited(false);
    }
	
	
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Input: ");
        for(double in : input) {
            sb.append(in).append(", ");
        }
        sb.delete(sb.length()-2, sb.length()-1);
        
        if (isSupervised()) {
            sb.append(" Desired output: ");
            for(double out : getDesiredOutput()) {
                sb.append(out).append(", ");
            }           
        }
                      
        if( isFited() ){
        	sb.append(" Fitness: ").append( getFitness() );
        }else
        	sb.delete(sb.length()-2, sb.length()-1);
        
        return sb.toString();        
    }
    
    @Override
    public String toCSV() {
        StringBuilder sb = new StringBuilder();
        
        for(double in : input) {
            sb.append(in).append(", ");
        }
        
        if (isSupervised()) {
            for(double out : getDesiredOutput()) {
                sb.append(out).append(", ");
            }           
        }
        
        if( isFited() ){
        	sb.append( getFitness() );
        }else
        	sb.delete(sb.length()-2, sb.length()-1);
                       
        return sb.toString();        
    }
    
    public DataSetRow toDataSetRow( ){
    	return new DataSetRow(getInput(), getDesiredOutput());
    }

    /**
     * Does this row contains fitness informations
     * 
     * @return fitness of the ouput vector
     */
	public boolean isFited() {
		return fited;
	}


    /**
     * Set if this row contains fitness informations
     * 
     * @param fited true if this row contain fitness informations about the ouput vector
     */
	public void setFited(boolean fited) {
		this.fited = fited;
	}


	/**
	 * Get training element Fitness
	 * 
	 * @return fitness of the ouput vector
	 */
	public Double getFitness() {
		return fitness;
	}

	/**
	 * Set training element Fitness
	 * 
	 * @param d fitness of the ouput vector
	 */
	public void setFitness(double d) {
		this.fitness = d;
	}
	
	
	@Override
	public boolean equals(Object v) {
        boolean retVal = true;

        if (v instanceof DataSetRow){
        	DataSetRow ptr = (DataSetRow) v;
        	double[] inputsA = getInput();
        	double[] inputsB = ptr.getInput();
        	
        	if( inputsA.length != inputsB.length )
        		return false;
        	
        	for( int i=0; i<inputsA.length; i++ )
        		if( inputsA[i] != inputsB[i] ){
        			retVal = false;
        			break;
        		}
        }
        return retVal;
	}

	@Override
	public int hashCode() {
	    int hash = 7;
	    
	    double som = 0;
	    for( int i=0; i<getInput().length; i++ )
	    	som += getInput()[i];
	    
	    hash = 17 * hash + (int)(hash*som);
	    return hash;
	}	
}
