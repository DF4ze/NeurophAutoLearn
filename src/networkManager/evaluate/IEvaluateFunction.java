/**
 * 
 */
package networkManager;

import dataManager.EvalDataSetRow;

/**
 * @author Clement ORTIZ
 *
 */
public interface IEvaluateFunction {

	public double evaluate( EvalDataSetRow evalItem );
}
