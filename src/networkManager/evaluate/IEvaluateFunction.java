/**
 * 
 */
package networkManager.evaluate;

import dataManager.EvalDataSetRow;

/**
 * @author Clement ORTIZ
 *
 */
public interface IEvaluateFunction {

	/**
	 * Function that receive a simple row and must look at inputs and outputs and say if it's correct.
	 * @param evalItem
	 * @return
	 */
	public Double evaluate( EvalDataSetRow evalItem );
	public String toString();
	
}
