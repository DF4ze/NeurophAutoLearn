package dataManager;

import java.util.ArrayList;

import org.neuroph.core.data.DataSetRow;

public class EvalDataSetRow extends FitDataSetRow {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * DataSet of previous evaluations
	 */
	private FitDataSet prevEval;

	

	public EvalDataSetRow( FitDataSetRow fitDataSetRow ) {
		super(fitDataSetRow.getInput(), fitDataSetRow.getDesiredOutput(), fitDataSetRow.getFitness());
		
		setFited(true);
		setPrevEval(new FitDataSet( getInput().length, getDesiredOutput().length ));
	}
	
	public EvalDataSetRow(String input, String desiredOutput) {
		super(input, desiredOutput);
		
		setFited(true);
		setPrevEval(new FitDataSet( getInput().length, getDesiredOutput().length ));
	}

	public EvalDataSetRow(String input, String desiredOutput, String fitness) {
		super(input, desiredOutput, fitness);
		
		setPrevEval(new FitDataSet( getInput().length, getDesiredOutput().length ));
	}

	
	public EvalDataSetRow(double[] input, int outputSize) {
		super(input);
		
		setFited(true);
		setPrevEval(new FitDataSet( getInput().length, outputSize ));
	}
	
	public EvalDataSetRow(double[] input, double[] desiredOutput) {
		super(input, desiredOutput);
		
		setFited(true);
		setPrevEval(new FitDataSet( getInput().length, getDesiredOutput().length ));
	}

	public EvalDataSetRow(double[] input, double[] desiredOutput, double fitness) {
		super(input, desiredOutput, fitness);
		
		setPrevEval(new FitDataSet( getInput().length, getDesiredOutput().length ));
	}

	public EvalDataSetRow(ArrayList<Double> input, ArrayList<Double> desiredOutput, Double fitness) {
		super(input, desiredOutput, fitness);
		
		setPrevEval(new FitDataSet( getInput().length, getDesiredOutput().length ));
	}

	public EvalDataSetRow(ArrayList<Double> input, ArrayList<Double> desiredOutput) {
		super(input, desiredOutput);
		
		setFited(true);
		setPrevEval(new FitDataSet( getInput().length, getDesiredOutput().length ));
	}

	/**
	 * Set training element Fitness and store olds values
	 * 
	 * @param fitness fitness of the ouput vector
	 */
	public void setFitness( double fitness ){
		if( getFitness() != null )
			getPrevEval().addRow(getInput(), getDesiredOutput(), getFitness());
			
		super.setFitness(fitness);
	}

	/**
	 * Convert Eval DataSet row to Fit DataSet row
	 * (Ligther than Eval DataSet that contains all previous evaluations )
	 * 
	 * @return converted DataSet
	 */
	public FitDataSetRow toFitDataSetRow(){
		return new FitDataSetRow( getInput(), getDesiredOutput(), getFitness() );
	}

	/**
	 * Convert Eval DataSet row to DataSet row
	 * (Ligther than Eval DataSet that contains all previous evaluations )
	 * 
	 * @return converted DataSet
	 */
	@Override
	public DataSetRow toDataSetRow(){
		return toFitDataSetRow().toDataSetRow();
	}

	/**
	 * Get DataSet of previous evaluations
	 * 
	 * @return
	 */
	public FitDataSet getPrevEval() {
		return prevEval;
	}


	/**
	 * set DataSet of previous evaluations
	 * 
	 * @param prevEval a DataSet of previous evaluations
	 */
	protected void setPrevEval(FitDataSet prevEval) {
		this.prevEval = prevEval;
	}
}
