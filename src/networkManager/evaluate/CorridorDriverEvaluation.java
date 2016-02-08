
package networkManager.evaluate;

import dataManager.EvalDataSetRow;
import dataManager.ManagedDataSet;

/**
 * @author Clement ORTIZ
 *
 */
public class CorridorDriverEvaluation implements IEvaluateFunction {

	ManagedDataSet mds;
	/**
	 * 
	 */
	public CorridorDriverEvaluation(ManagedDataSet mds) {
		this.mds = mds;
	}

	/* (non-Javadoc)
	 * @see networkManager.evaluate.IEvaluateFunction#evaluate(dataManager.EvalDataSetRow)
	 */
	@Override
	public Double evaluate(EvalDataSetRow evalItem) {
		//System.out.println( "evaluation" );
		double[] inputs = evalItem.getInput();
		double[] outputs = evalItem.getDesiredOutput();

		Double fitness = null;
		if( (inputs[0] >= 0 && inputs[0] <= 1) && (inputs[1] >= 0 && inputs[1] <= 1) ){
			double dif = inputs[0] - inputs[1];
			double reg = (outputs[0]-0.5)*2;
			fitness = mds.getMaxFitness()-( Math.abs( dif+reg )*10 );
			if( fitness < 0 )
				fitness = 0.0;
			
		}		
		
		return fitness;
	}

	/* (non-Javadoc)
	 * @see network.IEvaluateFunction#toString()
	 */
	@Override
	public String toString(){
		return "CorridorDriver";
	}
}
