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

	ManagedDataSet mds;
	/**
	 * 
	 */
	public XorEvaluation(ManagedDataSet mds) {
		this.mds = mds;
	}

	/* (non-Javadoc)
	 * @see network.IEvaluateFunction#evaluate(dataManager.EvalDataSetRow)
	 */
	@Override
	public Double evaluate(EvalDataSetRow evalItem) {
		double[] inputs = evalItem.getInput();
		double[] outputs = evalItem.getDesiredOutput();

		Double fitness = null;
		if( (inputs[0] == 0 || inputs[0] == 1) && (inputs[1] == 0 || inputs[1] == 1) ){
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
			
			fitness = mds.getMaxFitness() - (Math.abs((desiredOutput - outputs[0])) * mds.getMaxFitness());
			
		}
		return fitness;
	}
	
	/* (non-Javadoc)
	 * @see network.IEvaluateFunction#toString()
	 */
	@Override
	public String toString(){
		return "XOR";
	}
}
