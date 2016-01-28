/**
 * 
 */
package networkManager.evaluate;

import dataManager.EvalDataSetRow;
import dataManager.ManagedDataSet;

/**
 * @author Clement ORTIZ
 *
 */
public class XorEvaluation implements IEvaluateFunction {

	/**
	 * 
	 */
	public XorEvaluation() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see network.IEvaluateFunction#evaluate(dataManager.EvalDataSetRow)
	 */
	@Override
	public double evaluate(EvalDataSetRow evalItem) {
		double[] inputs = evalItem.getInput();
		double[] outputs = evalItem.getDesiredOutput();

		double fitness = 0;
		double desiredOutput;
		if( inputs[0] == 0 && inputs[1] == 0 ){
			desiredOutput = 0;
		}else if( inputs[0] == 1 && inputs[1] == 0 ){
			desiredOutput = 1;
		}else if( inputs[0] == 0 && inputs[1] == 1 ){
			desiredOutput = 1;
		}else if( inputs[0] == 1 && inputs[1] == 1 ){
			desiredOutput = 0;
		}else 
			desiredOutput = 0;
		
		fitness = ManagedDataSet.getMaxFitness() - (Math.abs((desiredOutput - outputs[0])) * ManagedDataSet.getMaxFitness());
		
		
		return fitness;
	}

}
